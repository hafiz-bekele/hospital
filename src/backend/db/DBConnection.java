package backend.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Manager
 * 
 * Think of this as the "phone line" to our database. Instead of creating
 * a new connection every time we need data (which is slow), we create one
 * connection and reuse it throughout the app.
 * 
 * This is called the "Singleton Pattern" - fancy name for "only one instance exists"
 * 
 * How to use:
 *   Connection conn = DBConnection.getConnection();
 *   // Do your database stuff
 *   // Connection stays open for next time!
 * 
 * @author Hospital Management Team
 */
public class DBConnection {
    
    // Database connection details - change these if your setup is different
    private static final String URL      = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; // XAMPP default is empty password
    
    // This is our single, shared connection that everyone uses
    private static Connection connection = null;

    /**
     * Get the database connection (creates it if it doesn't exist yet).
     *
     * Returns null if the connection cannot be established — callers
     * should handle null gracefully (all DAOs use try-with-resources
     * which will throw NullPointerException if this returns null, so
     * we show a clear one-time dialog instead of a cryptic stack trace).
     */
    public static Connection getConnection() {
        try {
            // Reconnect if we don't have a connection, it was closed, or it timed out
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Connected to database successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL Driver not found: " + e.getMessage());
            System.err.println("   Make sure mysql-connector-j.jar is in your classpath!");
            showConnectionError("MySQL driver not found.\nMake sure the mysql-connector JAR is on the classpath.");
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            System.err.println("   Is MySQL running? Check XAMPP or your MySQL server.");
            showConnectionError("Cannot connect to the database.\n\n" +
                "Please make sure:\n" +
                "  1. XAMPP is open and MySQL is started\n" +
                "  2. The database 'hospital_db' exists\n\n" +
                "Error: " + e.getMessage());
        }
        return connection;
    }

    /** Shows a one-time error dialog on the EDT so the user knows what went wrong. */
    private static void showConnectionError(String message) {
        // Only show if we're in a GUI context (Swing is available)
        try {
            javax.swing.SwingUtilities.invokeLater(() ->
                javax.swing.JOptionPane.showMessageDialog(null,
                    message, "Database Connection Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE)
            );
        } catch (Exception ignored) {
            // Headless environment — error already printed to stderr above
        }
    }

    /**
     * Close the database connection when we're done
     * 
     * Call this when shutting down the application to clean up properly.
     * Though honestly, for a desktop app, the OS will clean up when the app closes.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error closing connection: " + e.getMessage());
        }
    }
}
