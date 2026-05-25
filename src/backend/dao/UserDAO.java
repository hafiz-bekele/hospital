package backend.dao;

import backend.db.DBConnection;
import backend.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User Data Access Object (DAO)
 * 
 * This class handles ALL database operations related to users.
 * Think of it as the "middleman" between our app and the users table.
 * 
 * What it does:
 * - Login verification
 * - User registration
 * - Profile updates
 * - Password changes
 * - Fetching user lists
 * 
 * Why use a DAO?
 * - Keeps database code separate from UI code
 * - Makes it easy to change database logic without touching UI
 * - All SQL queries in one place = easier to maintain
 * 
 * @author Hospital Management Team
 */
public class UserDAO {

    /**
     * Verify user login credentials
     * 
     * This checks if the username and password match what's in the database.
     * If they match, we return the User object. If not, we return null.
     * 
     * @param username The username to check
     * @param password The password to verify
     * @return User object if credentials are valid, null otherwise
     */
    public User login(String username, String password) {
        // SQL query with ? placeholders (prevents SQL injection!)
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            // Fill in the ? placeholders with actual values
            ps.setString(1, username);
            ps.setString(2, password);
            
            // Execute the query and get results
            ResultSet rs = ps.executeQuery();
            
            // If we found a matching user, convert the database row to a User object
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Login failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        // No matching user found
        return null;
    }

    /**
     * Register a new user in the system
     * 
     * This creates a new user account. It's used for patient registration
     * and when admin creates doctor accounts.
     * 
     * @param user The User object with all the details to save
     * @return true if registration succeeded, false if it failed
     */
    public boolean register(User user) {
        // Insert a new row into the users table
        String sql = "INSERT INTO users (username, password, role, full_name, email, phone) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            // Fill in all the user details
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());  // TODO: Should hash this in production!
            ps.setString(3, user.getRole());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPhone());
            
            // Execute the insert and check if it worked
            // executeUpdate() returns number of rows affected
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Registration failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Check if a username is already taken
     * 
     * This prevents duplicate usernames. We check this before
     * allowing someone to register.
     * 
     * @param username The username to check
     * @return true if username exists, false if it's available
     */
    public boolean usernameExists(String username) {
        // We only need the ID, not all columns (faster query)
        String sql = "SELECT id FROM users WHERE username = ?";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            
            // If next() returns true, we found a match = username exists
            return ps.executeQuery().next();
        } catch (SQLException e) {
            System.err.println("❌ Username check failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Get all patients in the system
     * 
     * This is used by admin to view all registered patients.
     * We only get users with role = 'PATIENT', sorted by name.
     * 
     * @return List of all patient User objects
     */
    public List<User> getAllPatients() {
        List<User> list = new ArrayList<>();
        
        // Get all patients, sorted alphabetically by name
        String sql = "SELECT * FROM users WHERE role = 'PATIENT' ORDER BY full_name";
        
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            
            // Loop through all rows and convert each to a User object
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to fetch patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return list;
    }

    /**
     * Update a user's profile information
     * 
     * This lets users update their name, email, and phone number.
     * Note: Username and password are NOT updated here (separate methods for those)
     * 
     * @param userId The user's ID
     * @param fullName New full name
     * @param email New email
     * @param phone New phone number
     * @return true if update succeeded, false otherwise
     */
    public boolean updateProfile(int userId, String fullName, String email, String phone) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE id = ?";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Profile update failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Change a user's password
     * 
     * This is a two-step process:
     * 1. Verify the old password is correct
     * 2. If yes, update to the new password
     * 
     * This prevents someone from changing your password without knowing the old one!
     * 
     * @param userId The user's ID
     * @param oldPassword Current password (for verification)
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // Step 1: Verify old password is correct
        String check = "SELECT id FROM users WHERE id = ? AND password = ?";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(check)) {
            ps.setInt(1, userId);
            ps.setString(2, oldPassword);
            
            // If no match found, old password is wrong
            if (!ps.executeQuery().next()) {
                System.out.println("❌ Old password is incorrect");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Password verification failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        
        // Step 2: Old password is correct, now update to new password
        String update = "UPDATE users SET password = ? WHERE id = ?";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(update)) {
            ps.setString(1, newPassword);  // TODO: Should hash this in production!
            ps.setInt(2, userId);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Password update failed: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }

    /**
     * Count total number of patients
     * 
     * This is used for dashboard statistics.
     * Quick count query - doesn't fetch all the data, just the number.
     * 
     * @return Total number of patients in the system
     */
    public int getTotalPatients() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'PATIENT'";
        
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            
            if (rs.next()) {
                return rs.getInt(1);  // Get the count from first column
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to count patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }

    /**
     * Helper method: Convert database row to User object
     * 
     * This is a private helper that takes a ResultSet (database row)
     * and converts it into a nice User object we can work with.
     * 
     * We use this in multiple methods to avoid repeating code.
     * 
     * @param rs ResultSet positioned at a user row
     * @return User object with data from the database
     * @throws SQLException if there's a problem reading the data
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        
        // Pull each column from the database row and set it in the User object
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setRole(rs.getString("role"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        
        // Note: We don't set the password here for security reasons
        // (we don't want to pass passwords around unless absolutely necessary)
        
        return u;
    }
}
