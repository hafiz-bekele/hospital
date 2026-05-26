package backend.dao;

import backend.db.DBConnection;
import backend.models.Doctor;
import backend.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    // Add a new doctor (creates user account + doctor profile in one transaction)
    public boolean addDoctor(User user, String specialization, String qualification, int experience) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String userSql = "INSERT INTO users (username, password, role, full_name, email, phone) VALUES (?, ?, 'DOCTOR', ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername()); ps.setString(2, user.getPassword());
            ps.setString(3, user.getFullName()); ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int userId = keys.getInt(1);
                String docSql = "INSERT INTO doctors (user_id, specialization, qualification, experience_years) VALUES (?, ?, ?, ?)";
                PreparedStatement dps = conn.prepareStatement(docSql);
                dps.setInt(1, userId); dps.setString(2, specialization);
                dps.setString(3, qualification); dps.setInt(4, experience);
                dps.executeUpdate();
            }
            conn.commit(); conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
        return false;
    }

    // Delete a doctor (also deletes their user account due to CASCADE)
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM users WHERE id = (SELECT user_id FROM doctors WHERE id = ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // Get all doctors in the system
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = new ArrayList<>();
        String sql = "SELECT d.id, d.user_id, u.full_name, d.specialization, d.qualification, d.experience_years " +
                     "FROM doctors d JOIN users u ON d.user_id = u.id ORDER BY u.full_name";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapDoctor(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // Find doctor profile by their user ID (used when doctor logs in)
    public Doctor getDoctorByUserId(int userId) {
        String sql = "SELECT d.id, d.user_id, u.full_name, d.specialization, d.qualification, d.experience_years " +
                     "FROM doctors d JOIN users u ON d.user_id = u.id WHERE d.user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapDoctor(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Get user account details for a specific doctor
    public User getUserByDoctorId(int doctorId) {
        String sql = "SELECT u.* FROM users u JOIN doctors d ON d.user_id = u.id WHERE d.id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));          u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password")); u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email")); u.setPhone(rs.getString("phone"));
                u.setRole(rs.getString("role"));
                return u;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // Update doctor's user account and profile information
    public boolean updateDoctor(int doctorId, String fullName, String username, String password,
                                String email, String phone, String spec, String qual, int exp) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String userSql = "UPDATE users SET full_name=?, username=?, password=?, email=?, phone=? " +
                             "WHERE id = (SELECT user_id FROM doctors WHERE id = ?)";
            PreparedStatement ps = conn.prepareStatement(userSql);
            ps.setString(1, fullName); ps.setString(2, username); ps.setString(3, password);
            ps.setString(4, email);   ps.setString(5, phone);     ps.setInt(6, doctorId);
            ps.executeUpdate();
            String docSql = "UPDATE doctors SET specialization=?, qualification=?, experience_years=? WHERE id=?";
            PreparedStatement dps = conn.prepareStatement(docSql);
            dps.setString(1, spec); dps.setString(2, qual); dps.setInt(3, exp); dps.setInt(4, doctorId);
            dps.executeUpdate();
            conn.commit(); conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try { conn.rollback(); conn.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        }
        return false;
    }

    // Get total number of doctors in the system
    public int getTotalDoctors() {
        String sql = "SELECT COUNT(*) FROM doctors";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Convert database row into Doctor object
    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        return new Doctor(rs.getInt("id"), rs.getInt("user_id"), rs.getString("full_name"),
            rs.getString("specialization"), rs.getString("qualification"), rs.getInt("experience_years"));
    }
}
