package backend.models;

/**
 * Notification Model - Represents a message/alert for a user
 * 
 * This is how we send updates to users! For example:
 * - "Your appointment has been approved"
 * - "Dr. Smith added medical notes to your appointment"
 * - "New doctor schedule available"
 * 
 * Each notification can be marked as read/unread.
 * 
 * @author Hospital Management Team
 */
public class Notification {
    
    // Database identifiers
    private int id;              // Unique notification ID
    private int userId;          // Who this notification is for
    
    // Notification content
    private String message;      // The actual notification text
    private String createdAt;    // When it was created
    private boolean isRead;      // Has the user seen it yet?

    /**
     * Empty constructor - creates a blank notification
     */
    public Notification() {
        // All fields start empty
    }

    // ========== GETTERS AND SETTERS ==========
    
    /** Get notification ID */
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    /** Get user ID (who receives this notification) */
    public int getUserId() { 
        return userId; 
    }
    
    public void setUserId(int v) { 
        userId = v; 
    }
    
    /** Get the notification message */
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String v) { 
        message = v; 
    }
    
    /** Check if notification has been read */
    public boolean isRead() { 
        return isRead; 
    }
    
    /** Mark notification as read or unread */
    public void setRead(boolean v) { 
        isRead = v; 
    }
    
    /** Get when notification was created */
    public String getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(String v) { 
        createdAt = v; 
    }
}
