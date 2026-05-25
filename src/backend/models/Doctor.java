package backend.models;

/**
 * Doctor Model - Represents a doctor in our hospital
 * 
 * This holds all the professional information about a doctor.
 * Note: A doctor is also a User (they have login credentials),
 * so we link to the User table via userId.
 * 
 * @author Hospital Management Team
 */
public class Doctor {
    
    // Database identifiers
    private int id;              // Doctor's unique ID in doctors table
    private int userId;          // Links to the user account (for login)
    
    // Professional information
    private String fullName;         // Doctor's full name (e.g., "Dr. Sarah Johnson")
    private String specialization;   // What they specialize in (e.g., "Cardiology")
    private String qualification;    // Their degrees (e.g., "MBBS, MD")
    private int experienceYears;     // Years of experience

    /**
     * Empty constructor - creates a blank doctor
     */
    public Doctor() {
        // All fields start empty
    }

    /**
     * Full constructor - creates a doctor with all details
     * Useful when loading from database
     */
    public Doctor(int id, int userId, String fullName, String specialization, 
                  String qualification, int experienceYears) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
    }

    // ========== GETTERS AND SETTERS ==========
    
    /** Get doctor's database ID */
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    /** Get the linked user account ID */
    public int getUserId() { 
        return userId; 
    }
    
    public void setUserId(int userId) { 
        this.userId = userId; 
    }
    
    /** Get doctor's full name */
    public String getFullName() { 
        return fullName; 
    }
    
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }
    
    /** Get specialization (e.g., "Cardiology", "Pediatrics") */
    public String getSpecialization() { 
        return specialization; 
    }
    
    public void setSpecialization(String specialization) { 
        this.specialization = specialization; 
    }
    
    /** Get qualifications (e.g., "MBBS, MD") */
    public String getQualification() { 
        return qualification; 
    }
    
    public void setQualification(String qualification) { 
        this.qualification = qualification; 
    }
    
    /** Get years of experience */
    public int getExperienceYears() { 
        return experienceYears; 
    }
    
    public void setExperienceYears(int experienceYears) { 
        this.experienceYears = experienceYears; 
    }

    /**
     * Convert to string for display
     * Shows as "Dr. Sarah Johnson (Cardiology)"
     */
    @Override 
    public String toString() { 
        return "Dr. " + fullName + " (" + specialization + ")"; 
    }
}
