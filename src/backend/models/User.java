package backend.models;

/**
 * User Model - Represents a person in our system
 * 
 * This is like a blueprint for any user (Admin, Doctor, or Patient).
 * It holds all the information about a person and provides easy ways
 * to get and set that information.
 * 
 * Think of it as a digital ID card with all the person's details!
 * 
 * @author Hospital Management Team
 */
public class User {
    
    // Basic user information
    private int id;              // Unique identifier from database
    private String username;     // Login username (e.g., @john)
    private String password;     // Login password (should be hashed in production!)
    private String role;         // ADMIN, DOCTOR, or PATIENT
    
    // Personal details
    private String fullName;     // Full name (e.g., "John Doe")
    private String email;        // Email address (optional)
    private String phone;        // Phone number (optional)

    /**
     * Empty constructor - creates a blank user
     * Useful when we want to fill in details later
     */
    public User() {
        // Nothing to do here - all fields start as null/0
    }

    /**
     * Full constructor - creates a user with all details at once
     * Useful when loading from database
     * 
     * @param id Database ID
     * @param username Login username
     * @param role User role (ADMIN/DOCTOR/PATIENT)
     * @param fullName Person's full name
     * @param email Email address
     * @param phone Phone number
     */
    public User(int id, String username, String role, String fullName, String email, String phone) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    // ========== GETTERS AND SETTERS ==========
    // These let us safely access and modify user data
    // It's better than making fields public because we can add validation later
    
    /** Get the user's database ID */
    public int getId() { 
        return id; 
    }
    
    /** Set the user's database ID */
    public void setId(int id) { 
        this.id = id; 
    }
    
    /** Get the username (e.g., @john) */
    public String getUsername() { 
        return username; 
    }
    
    /** Set the username */
    public void setUsername(String username) { 
        this.username = username; 
    }
    
    /** Get the password (careful with this in production!) */
    public String getPassword() { 
        return password; 
    }
    
    /** Set the password */
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    /** Get the user's role (ADMIN, DOCTOR, or PATIENT) */
    public String getRole() { 
        return role; 
    }
    
    /** Set the user's role */
    public void setRole(String role) { 
        this.role = role; 
    }
    
    /** Get the person's full name */
    public String getFullName() { 
        return fullName; 
    }
    
    /** Set the person's full name */
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }
    
    /** Get the email address */
    public String getEmail() { 
        return email; 
    }
    
    /** Set the email address */
    public void setEmail(String email) { 
        this.email = email; 
    }
    
    /** Get the phone number */
    public String getPhone() { 
        return phone; 
    }
    
    /** Set the phone number */
    public void setPhone(String phone) { 
        this.phone = phone; 
    }

    /**
     * Convert user to string (useful for debugging and dropdowns)
     * When you print a User object, you'll see their full name
     */
    @Override 
    public String toString() { 
        return fullName; 
    }
}
