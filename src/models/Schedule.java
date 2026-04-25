package models;

public class Schedule {
    private int id;
    private int doctorId;
    private String doctorName;
    private String availableDate;
    private String startTime;
    private String endTime;
    private int maxPatients;

    public Schedule() {}

    public Schedule(int id, int doctorId, String availableDate, String startTime, String endTime, int maxPatients) {
        this.id = id;
        this.doctorId = doctorId;
        this.availableDate = availableDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxPatients = maxPatients;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getAvailableDate() { return availableDate; }
    public void setAvailableDate(String availableDate) { this.availableDate = availableDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getMaxPatients() { return maxPatients; }
    public void setMaxPatients(int maxPatients) { this.maxPatients = maxPatients; }

    @Override
    public String toString() {
        return availableDate + " | " + startTime + " - " + endTime;
    }
}
