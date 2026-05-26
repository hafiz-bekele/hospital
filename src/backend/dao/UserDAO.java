package backend.dao;

import backend.db.DBConnection;
import backend.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapUser(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean register(User user) {
        String sql = "INSERT INTO users (username, password, role, full_name, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, user.getUsername()); ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());     ps.setString(4, user.getFullName());
            ps.setString(5, user.getEmail());    ps.setString(6, user.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean usernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<User> getAllPatients() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'PATIENT' ORDER BY full_name";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) list.add(mapUser(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean updateProfile(int userId, String fullName, String email, String phone) {
        String sql = "UPDATE users SET full_name = ?, email = ?, phone = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, fullName); ps.setString(2, email);
            ps.setString(3, phone);   ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        String check = "SELECT id FROM users WHERE id = ? AND password = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(check)) {
            ps.setInt(1, userId); ps.setString(2, oldPassword);
            if (!ps.executeQuery().next()) return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }

        String update = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(update)) {
            ps.setString(1, newPassword); ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public int getTotalPatients() {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'PATIENT'";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));           u.setUsername(rs.getString("username"));
        u.setRole(rs.getString("role"));    u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));  u.setPhone(rs.getString("phone"));
        return u;
    }
}
