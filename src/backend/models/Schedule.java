package backend.models;

public class Schedule {
    private int id, doctorId, maxPatients;
    private String doctorName, availableDate, startTime, endTime;

    public Schedule() {}

    public Schedule(int id, int doctorId, String availableDate,
                    String startTime, String endTime, int maxPatients) {
        this.id = id; this.doctorId = doctorId; this.availableDate = availableDate;
        this.startTime = startTime; this.endTime = endTime; this.maxPatients = maxPatients;
    }

    public int getId() { return id; }
    public void setId(int v) { id = v; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int v) { doctorId = v; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String v) { doctorName = v; }

    public String getAvailableDate() { return availableDate; }
    public void setAvailableDate(String v) { availableDate = v; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String v) { startTime = v; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String v) { endTime = v; }

    public int getMaxPatients() { return maxPatients; }
    public void setMaxPatients(int v) { maxPatients = v; }

    @Override
    public String toString() { return availableDate + " | " + startTime + " - " + endTime; }
}
