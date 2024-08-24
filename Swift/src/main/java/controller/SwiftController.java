package controller;

import service.SwiftService;

import static spark.Spark.post;
import static spark.Spark.get;

public class SwiftController {

    private final SwiftService swiftService = new SwiftService();

    /**
     * Initializes the routes for the web API.
     *
     * <p>This method sets up the following routes:
     * <ul>
     *     <li><code>POST /upload</code>: Accepts a SWIFT message as the request body, processes it,
     *         and returns a status message. The response status is:
     *         <ul>
     *             <li>400 if the message is invalid or empty.</li>
     *             <li>500 if there is an error processing the message.</li>
     *             <li>200 if the message is processed successfully.</li>
     *         </ul>
     *     </li>
     *     <li><code>GET /</code>: A simple route that returns a message indicating that the
     *         SwiftMT799 API is running. This can be used as a health check endpoint.</li>
     * </ul>
     */
    public void initRoutes() {
        post("/upload", (request, response) -> {
            String message = request.body();

            if (message == null || message.isEmpty()) {
                response.status(400);
                return "Invalid message!";
            }

            try {
                swiftService.processSwiftMessage(message);
            } catch (Exception e) {
                response.status(500);
                return "Failed to process message!";
            }

            response.status(200);
            return "Message processed successfully.";
        });

        // Route to test API
        get("/", (req, res) -> "SwiftMT799 API is running.");
    }

}
