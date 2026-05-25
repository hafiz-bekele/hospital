package backend.models;

/**
 * Schedule Model - Represents a doctor's available time slot
 * 
 * Think of this as a doctor's calendar entry. It says:
 * "Dr. Smith is available on May 25th from 9:00 AM to 12:00 PM
 *  and can see up to 10 patients during that time"
 * 
 * Patients book appointments by choosing from available schedules.
 * 
 * @author Hospital Management Team
 */
public class Schedule {
    
    // Database identifiers
    private int id;              // Unique schedule ID
    private int doctorId;        // Which doctor this schedule belongs to
    
    // Schedule details
    private String doctorName;       // Doctor's name (for display)
    private String availableDate;    // Date (YYYY-MM-DD format)
    private String startTime;        // When doctor starts (e.g., "09:00")
    private String endTime;          // When doctor finishes (e.g., "17:00")
    private int maxPatients;         // Maximum patients in this slot

    /**
     * Empty constructor - creates a blank schedule
     */
    public Schedule() {
        // All fields start empty
    }

    /**
     * Full constructor - creates a schedule with all details
     * Useful when loading from database
     */
    public Schedule(int id, int doctorId, String availableDate, 
                    String startTime, String endTime, int maxPatients) {
        this.id = id;
        this.doctorId = doctorId;
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxPatients = maxPatients;
    }

    // ========== GETTERS AND SETTERS ==========
    
    /** Get schedule ID */
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    /** Get doctor's ID */
    public int getDoctorId() { 
        return doctorId; 
    }
    
    public void setDoctorId(int v) { 
        doctorId = v; 
    }
    
    /** Get doctor's name (for display) */
    public String getDoctorName() { 
        return doctorName; 
    }
    
    public void setDoctorName(String v) { 
        doctorName = v; 
    }
    
    /** Get available date */
    public String getAvailableDate() { 
        return availableDate; 
    }
    
    public void setAvailableDate(String v) { 
        availableDate = v; 
    }
    
    /** Get start time (e.g., "09:00") */
    public String getStartTime() { 
        return startTime; 
    }
    
    public void setStartTime(String v) { 
        startTime = v; 
    }
    
    /** Get end time (e.g., "17:00") */
    public String getEndTime() { 
        return endTime; 
    }
    
    public void setEndTime(String v) { 
        endTime = v; 
    }
    
    /** Get maximum patients allowed in this slot */
    public int getMaxPatients() { 
        return maxPatients; 
    }
    
    public void setMaxPatients(int v) { 
        maxPatients = v; 
    }

    /**
     * Convert to string for display
     * Shows as "2026-05-25 | 09:00 - 17:00"
     */
    @Override 
    public String toString() { 
        return availableDate + " | " + startTime + " - " + endTime; 
    }
}
