package backend.db;

import java.sql.*;
import javax.swing.*;

public class DBConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    // Get database connection (creates new one if needed or if old one is closed)
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException e) {
            showError("MySQL driver not found. Make sure mysql-connector JAR is on the classpath.");
        } catch (SQLException e) {
            showError("Cannot connect to database.\n\nMake sure XAMPP MySQL is running and 'hospital_db' exists.\n\nError: " + e.getMessage());
        }
        return connection;
    }

    // Show error dialog to user
    private static void showError(String msg) {
        SwingUtilities.invokeLater(() ->
            JOptionPane.showMessageDialog(null, msg, "Database Error", JOptionPane.ERROR_MESSAGE)
        );
    }

    // Close the database connection when app shuts down
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }
}
