package backend.models;

public class Doctor {
    private int id, userId, experienceYears;
    private String fullName, specialization, qualification;

    public Doctor() {}

    public Doctor(int id, int userId, String fullName, String specialization,
                  String qualification, int experienceYears) {
        this.id = id; this.userId = userId; this.fullName = fullName;
        this.specialization = specialization; this.qualification = qualification;
        this.experienceYears = experienceYears;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int v) { userId = v; }

    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String v) { specialization = v; }

    public String getQualification() { return qualification; }
    public void setQualification(String v) { qualification = v; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int v) { experienceYears = v; }

    @Override
    public String toString() { return "Dr. " + fullName + " (" + specialization + ")"; }
}
