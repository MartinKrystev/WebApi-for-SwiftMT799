import controller.SwiftController;
import db.DBOperations;

import static spark.Spark.port;

public class SwiftMT799Api {

    /**
     * The entry point for the SwiftMT799 API application.
     *
     * <p>This method performs the following actions:
     * <ul>
     *     <li>Sets the server to run on port 8080.</li>
     *     <li>Initializes the database operations and ensures that the required table is created if it doesn't already exist.</li>
     *     <li>Creates an instance of {@link SwiftController} and initializes the API routes.</li>
     *     <li>Prints a message to the console indicating that the SwiftMT799 API is running.</li>
     * </ul>
     *
     * @param args command-line arguments passed to the application (not used in this implementation)
     */
    public static void main(String[] args) {
        // Set the server to run on port 8080
        port(8080);

        DBOperations dbOperations = new DBOperations();

        // Create the table if it doesn't exist
        dbOperations.createTable();

        // Initialize routes
        SwiftController controller = new SwiftController();
        controller.initRoutes();

        System.out.println("SwiftMT799 API is running...");

    }

}
