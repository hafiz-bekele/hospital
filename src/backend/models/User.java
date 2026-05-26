package backend.models;

public class User {
    private int id;
    private String username, password, role, fullName, email, phone;

    public User() {}

    public User(int id, String username, String role, String fullName, String email, String phone) {
        this.id = id; this.username = username; this.role = role;
        this.fullName = fullName; this.email = email; this.phone = phone;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String v) { username = v; }

    public String getPassword() { return password; }
    public void setPassword(String v) { password = v; }

    public String getRole() { return role; }
    public void setRole(String v) { role = v; }

    public String getFullName() { return fullName; }
    public void setFullName(String v) { fullName = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { phone = v; }

    @Override
    public String toString() { return fullName; }
}
