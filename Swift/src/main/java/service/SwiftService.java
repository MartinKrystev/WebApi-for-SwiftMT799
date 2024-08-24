package service;

import db.DBOperations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SwiftService {
    private static final Logger logger = LogManager.getLogger(SwiftService.class);

    private final DBOperations dbOperations = new DBOperations();

    /**
     * Processes a SWIFT message by parsing its content and saving relevant data to the database.
     *
     * <p>This method performs the following steps:
     * <ol>
     *     <li>Logs the received SWIFT message.</li>
     *     <li>Parses the message into its constituent blocks.</li>
     *     <li>Extracts relevant fields from the parsed blocks, including Block 1, Block 2, Block 4,
     *         the Message Authentication Code (MAC) from Block 5, and the checksum.</li>
     *     <li>Extracts specific tags from Block 4, such as the transaction reference, related reference, and narrative.</li>
     *     <li>Saves the parsed and extracted data into the database using the {@link DBOperations#insertSwiftMessage} method.</li>
     * </ol>
     *
     * @param message the raw SWIFT message to be processed
     * @throws Exception if any error occurs during the processing of the SWIFT message, such as parsing errors or database access issues
     *
     * <p>This method logs any exceptions that occur during processing and rethrows them to allow for higher-level handling.</p>
     */
    public void processSwiftMessage(String message) throws Exception {
        try {
            logger.info("Received SWIFT message: {}", message);

            Map<String, String> blocks = parseSwiftMessage(message);

            // Extract relevant fields
            String block1 = blocks.get("1");
            String block2 = blocks.get("2");
            String block4 = blocks.get("4");
            String mac = extractMAC(blocks.get("5"));
            String checksum = blocks.get("CHK");

            // Extract specific fields from block 4
            String transactionReference = extractTagValue(block4, ":20:");
            String relatedReference = extractTagValue(block4, ":21:");
            String narrative = extract79TagContent(block4);

            // Save the parsed data to the database
            dbOperations.insertSwiftMessage(block1, block2, transactionReference, relatedReference, narrative, mac, checksum);

        } catch (Exception e) {
            logger.error("Error processing SWIFT message", e);
            throw e;
        }
    }

    /**
     * Parses a raw SWIFT message and extracts its blocks into a map.
     *
     * <p>This method uses regular expressions to identify and extract blocks from the SWIFT message.
     * Each block is identified by a number (e.g., "1", "2", "4") or specific labels like "MAC" or "CHK".
     * The content of each block is then trimmed and stored in a map, where the key is the block identifier
     * (e.g., "1", "2", "MAC") and the value is the block's content.</p>
     *
     * @param message the raw SWIFT message to be parsed
     * @return a map containing the blocks of the SWIFT message, where the key is the block identifier and the value is the block's content
     */
    private Map<String, String> parseSwiftMessage(String message) {
        Map<String, String> blocks = new HashMap<>();

        String regex = "\\{(\\d+|MAC|CHK):((?s).*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);

        while (matcher.find()) {
            String blockNumber = matcher.group(1);
            String blockContent = matcher.group(2).trim();
            blocks.put(blockNumber, blockContent);
        }

        return blocks;
    }

    /**
     * Extracts the value of a specific tag from the given block content of a SWIFT message.
     *
     * <p>This method searches for a tag (e.g., ":20:", ":21:") within the provided block content
     * and extracts the value associated with that tag. The tag value is expected to end either
     * with a newline followed by another tag or at the end of the block.</p>
     *
     * @param blockContent the content of the SWIFT message block from which the tag value is to be extracted
     * @param tag the tag (e.g., ":20:", ":21:") whose value needs to be extracted
     * @return the extracted value associated with the specified tag, or <code>null</code> if the tag is not found
     */
    private String extractTagValue(String blockContent, String tag) {
        // Extract tags 20 and 21
        String regex = tag + "(.*?)\\r?\\n(?=\\n?:\\d{2,3}:|\\})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(blockContent);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractMAC(String blockContent) {
        return blockContent.substring(5);
    }

    private String extract79TagContent(String blockContent) {
        String[] result = blockContent.split(":79:");
        return result[1];
    }

}
