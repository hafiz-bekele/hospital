package models;

public class Doctor {
    private int id;
    private int userId;
    private String fullName;
    private String specialization;
    private String qualification;
    private int experienceYears;

    public Doctor() {}

    public Doctor(int id, int userId, String fullName, String specialization, String qualification, int experienceYears) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.specialization = specialization;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    @Override
    public String toString() { return "Dr. " + fullName + " (" + specialization + ")"; }
}
