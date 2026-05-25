package backend.models;

/**
 * Appointment Model - Represents a patient's appointment with a doctor
 * 
 * This is the heart of our booking system! It connects patients with doctors
 * at specific times and tracks the status of each appointment.
 * 
 * Status can be: PENDING, APPROVED, or REJECTED
 * 
 * @author Hospital Management Team
 */
public class Appointment {
    
    // Database IDs (links to other tables)
    private int id;              // Unique appointment ID
    private int patientId;       // Which patient booked this
    private int doctorId;        // Which doctor they're seeing
    private int scheduleId;      // Which time slot they booked
    
    // Display names (for showing in UI without extra queries)
    private String patientName;  // Patient's full name
    private String doctorName;   // Doctor's full name
    
    // Appointment details
    private String appointmentDate;  // Date of appointment (YYYY-MM-DD)
    private String startTime;        // Start time (HH:MM)
    private String endTime;          // End time (HH:MM)
    private String status;           // PENDING, APPROVED, or REJECTED
    
    // Additional information
    private String reason;           // Why patient is visiting
    private String medicalNotes;     // Doctor's notes after appointment
    private String createdAt;        // When appointment was booked

    /**
     * Empty constructor - creates a blank appointment
     */
    public Appointment() {
        // All fields start empty
    }

    // ========== GETTERS AND SETTERS ==========
    // Lots of them because appointments have many details!
    
    /** Get appointment ID */
    public int getId() { 
        return id; 
    }
    
    public void setId(int id) { 
        this.id = id; 
    }
    
    /** Get patient's user ID */
    public int getPatientId() { 
        return patientId; 
    }
    
    public void setPatientId(int v) { 
        patientId = v; 
    }
    
    /** Get doctor's ID */
    public int getDoctorId() { 
        return doctorId; 
    }
    
    public void setDoctorId(int v) { 
        doctorId = v; 
    }
    
    /** Get schedule ID (which time slot) */
    public int getScheduleId() { 
        return scheduleId; 
    }
    
    public void setScheduleId(int v) { 
        scheduleId = v; 
    }
    
    /** Get patient's name (for display) */
    public String getPatientName() { 
        return patientName; 
    }
    
    public void setPatientName(String v) { 
        patientName = v; 
    }
    
    /** Get doctor's name (for display) */
    public String getDoctorName() { 
        return doctorName; 
    }
    
    public void setDoctorName(String v) { 
        doctorName = v; 
    }
    
    /** Get appointment date */
    public String getAppointmentDate() { 
        return appointmentDate; 
    }
    
    public void setAppointmentDate(String v) { 
        appointmentDate = v; 
    }
    
    /** Get appointment status (PENDING/APPROVED/REJECTED) */
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String v) { 
        status = v; 
    }
    
    /** Get doctor's medical notes */
    public String getMedicalNotes() { 
        return medicalNotes; 
    }
    
    public void setMedicalNotes(String v) { 
        medicalNotes = v; 
    }
    
    /** Get reason for visit */
    public String getReason() { 
        return reason; 
    }
    
    public void setReason(String v) { 
        reason = v; 
    }
    
    /** Get when appointment was created */
    public String getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(String v) { 
        createdAt = v; 
    }
    
    /** Get start time */
    public String getStartTime() { 
        return startTime; 
    }
    
    public void setStartTime(String v) { 
        startTime = v; 
    }
    
    /** Get end time */
    public String getEndTime() { 
        return endTime; 
    }
    
    public void setEndTime(String v) { 
        endTime = v; 
    }
}
