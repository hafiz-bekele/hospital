package backend.dao;

import backend.db.DBConnection;
import backend.models.Schedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDAO {

    // Holds the last error so the UI can display it instead of silently failing
    private String lastError = "";

    public String getLastError() { return lastError; }

    public boolean addSchedule(Schedule s) {
        lastError = "";
        String sql = "INSERT INTO schedules (doctor_id, available_date, start_time, end_time, max_patients) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, s.getDoctorId());
            ps.setString(2, s.getAvailableDate());
            ps.setString(3, s.getStartTime());
            ps.setString(4, s.getEndTime());
            ps.setInt(5, s.getMaxPatients());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            lastError = e.getMessage();
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSchedule(int scheduleId) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Schedule> getAllSchedules() {
        List<Schedule> list = new ArrayList<>();
        String sql = "SELECT s.*, u.full_name FROM schedules s " +
                     "JOIN doctors d ON s.doctor_id = d.id " +
                     "JOIN users u ON d.user_id = u.id ORDER BY s.available_date";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Schedule sc = mapSchedule(rs);
                sc.setDoctorName(rs.getString("full_name"));
                list.add(sc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Schedule> getSchedulesByDoctor(int doctorId) {
        List<Schedule> list = new ArrayList<>();
        // Only return today's and future schedules — past slots are not bookable
        String sql = "SELECT s.*, u.full_name FROM schedules s " +
                     "JOIN doctors d ON s.doctor_id = d.id " +
                     "JOIN users u ON d.user_id = u.id " +
                     "WHERE s.doctor_id = ? AND s.available_date >= CURDATE() " +
                     "ORDER BY s.available_date";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Schedule sc = mapSchedule(rs);
                sc.setDoctorName(rs.getString("full_name"));
                list.add(sc);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Schedule mapSchedule(ResultSet rs) throws SQLException {
        Schedule s = new Schedule();
        s.setId(rs.getInt("id"));                   s.setDoctorId(rs.getInt("doctor_id"));
        s.setAvailableDate(rs.getString("available_date")); s.setStartTime(rs.getString("start_time"));
        s.setEndTime(rs.getString("end_time"));     s.setMaxPatients(rs.getInt("max_patients"));
        return s;
    }
}
