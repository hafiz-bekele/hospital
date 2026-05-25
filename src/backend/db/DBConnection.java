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
     * Get the database connection (creates it if it doesn't exist yet)
     * 
     * This method is smart - it checks if we already have a connection.
     * If yes, it returns the existing one. If no, it creates a new one.
     * 
     * @return Connection object to interact with the database
     */
    public static Connection getConnection() {
        try {
            // Check if we need to create a new connection
            // (either we don't have one, or the old one got closed/timed out)
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                
                // Step 1: Load the MySQL driver (tells Java how to talk to MySQL)
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Step 2: Actually connect to the database
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                
                System.out.println("✓ Connected to database successfully!");
            }
        } catch (ClassNotFoundException e) {
            // Oops! MySQL driver not found - did you include the JAR file?
            System.err.println("❌ MySQL Driver not found: " + e.getMessage());
            System.err.println("   Make sure mysql-connector-j.jar is in your classpath!");
        } catch (SQLException e) {
            // Database connection failed - check your credentials and if MySQL is running
            System.err.println("❌ Database connection failed: " + e.getMessage());
            System.err.println("   Is MySQL running? Check XAMPP or your MySQL server.");
        }
        
        return connection;
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
