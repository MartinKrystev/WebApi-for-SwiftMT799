package db;

import java.sql.*;

/**
 * The DBOperations class provides methods for establishment of the database connection and its creation.
 */
public class DBOperations {

    /**
     * The DB_URL string holds the Java Database Connectivity, the type of database being used,
     * and the name of the database.
     */
    private static final String DB_URL = "jdbc:sqlite:swift_messages.db";

    /**
     * Connects to the database using a predefined URL.
     *
     * @return a Connection object to the database, or null if the connection fails
     */
    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Creates a new table in the database if it does not already exist.
     *
     * <p>The table is named <code>swift_messages</code> and contains the following columns:
     * <ul>
     *     <li><code>id</code>: An integer primary key that autoincrements.</li>
     *     <li><code>block1</code>: A text field for storing block 1 of the message.</li>
     *     <li><code>block2</code>: A text field for storing block 2 of the message.</li>
     *     <li><code>transaction_reference</code>: A text field for storing the transaction reference.</li>
     *     <li><code>related_reference</code>: A text field for storing the related reference.</li>
     *     <li><code>narrative</code>: A text field for storing the narrative.</li>
     *     <li><code>mac</code>: A text field for storing the message authentication code (MAC).</li>
     *     <li><code>checksum</code>: A text field for storing the checksum.</li>
     * </ul>
     *
     * <p>If the table already exists, this method will not create a new table but will ensure the table structure is available for use.</p>
     *
     * <p>Logs a success message to the console if the table is created successfully, or logs the error message if an exception occurs.</p>
     */
    public void createTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS swift_messages (\n"
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " block1 TEXT,\n"
                + " block2 TEXT,\n"
                + " transaction_reference TEXT,\n"
                + " related_reference TEXT,\n"
                + " narrative TEXT,\n"
                + " mac TEXT,\n"
                + " checksum TEXT\n"
                + ");";

        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);

            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Inserts a new SWIFT message into the database.
     *
     * <p>This method adds a new record to the <code>swift_messages</code> table with the provided data for each column.</p>
     *
     * @param block1 the content of the first block of the SWIFT message
     * @param block2 the content of the second block of the SWIFT message
     * @param transactionReference the transaction reference associated with the SWIFT message
     * @param relatedReference the related reference associated with the SWIFT message
     * @param narrative the narrative or description of the transaction
     * @param mac the Message Authentication Code (MAC) for the SWIFT message
     * @param checksum the checksum value for the SWIFT message
     *
     * <p>If the insertion is successful, the data is committed to the database. If a database access error occurs,
     * an error message is printed to the console.</p>
     */
    public void insertSwiftMessage(String block1, String block2, String transactionReference, String relatedReference, String narrative, String mac, String checksum) {
        String sql = "INSERT INTO swift_messages(block1, block2, transaction_reference, related_reference, narrative, mac, checksum) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = this.connect();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, block1);
            pstmt.setString(2, block2);
            pstmt.setString(3, transactionReference);
            pstmt.setString(4, relatedReference);
            pstmt.setString(5, narrative);
            pstmt.setString(6, mac);
            pstmt.setString(7, checksum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
