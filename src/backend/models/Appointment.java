package backend.models;

public class Appointment {
    private int id, patientId, doctorId, scheduleId;
    private String patientName, doctorName, appointmentDate;
    private String startTime, endTime, status, reason, medicalNotes, createdAt;

    public Appointment() {}

    public int getId() { return id; }
    public void setId(int v) { id = v; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int v) { patientId = v; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int v) { doctorId = v; }

    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int v) { scheduleId = v; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String v) { patientName = v; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String v) { doctorName = v; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String v) { appointmentDate = v; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String v) { startTime = v; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String v) { endTime = v; }

    public String getStatus() { return status; }
    public void setStatus(String v) { status = v; }

    public String getReason() { return reason; }
    public void setReason(String v) { reason = v; }

    public String getMedicalNotes() { return medicalNotes; }
    public void setMedicalNotes(String v) { medicalNotes = v; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { createdAt = v; }
}
