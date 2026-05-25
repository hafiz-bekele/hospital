package backend.dao;

import backend.db.DBConnection;
import backend.models.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {

    private static final String BASE_SQL =
        "SELECT a.*, u.full_name AS patient_name, du.full_name AS doctor_name, s.start_time, s.end_time " +
        "FROM appointments a " +
        "JOIN users u ON a.patient_id = u.id " +
        "JOIN doctors d ON a.doctor_id = d.id " +
        "JOIN users du ON d.user_id = du.id " +
        "JOIN schedules s ON a.schedule_id = s.id ";

    public boolean bookAppointment(Appointment a) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, schedule_id, appointment_date, reason) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, a.getPatientId()); ps.setInt(2, a.getDoctorId());
            ps.setInt(3, a.getScheduleId()); ps.setString(4, a.getAppointmentDate());
            ps.setString(5, a.getReason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /**
     * Check if a patient already has an active (non-cancelled, non-rejected) booking
     * for the same schedule slot. Prevents duplicate bookings.
     */
    public boolean hasActiveBooking(int patientId, int scheduleId) {
        String sql = "SELECT id FROM appointments WHERE patient_id = ? AND schedule_id = ? " +
                     "AND status NOT IN ('CANCELLED', 'REJECTED')";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, patientId);
            ps.setInt(2, scheduleId);
            return ps.executeQuery().next();
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status); ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean cancelAppointment(int appointmentId) {
        String sql = "UPDATE appointments SET status = 'CANCELLED' WHERE id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean updateMedicalNotes(int appointmentId, String notes) {
        String sql = "UPDATE appointments SET medical_notes = ? WHERE id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, notes); ps.setInt(2, appointmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return queryById(BASE_SQL + "WHERE a.patient_id = ? ORDER BY a.appointment_date DESC", patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        return queryById(BASE_SQL + "WHERE a.doctor_id = ? ORDER BY a.appointment_date DESC", doctorId);
    }

    public List<Appointment> getAppointmentsByDoctorFiltered(int doctorId, String status, String fromDate, String toDate) {
        List<Appointment> list = new ArrayList<>();
        // Build query with placeholders only — never concatenate user input into SQL
        StringBuilder sql = new StringBuilder(BASE_SQL + "WHERE a.doctor_id = ?");
        if (status   != null && !status.equals("ALL")) sql.append(" AND a.status = ?");
        if (fromDate != null && !fromDate.isEmpty())   sql.append(" AND a.appointment_date >= ?");
        if (toDate   != null && !toDate.isEmpty())     sql.append(" AND a.appointment_date <= ?");
        sql.append(" ORDER BY a.appointment_date DESC");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, doctorId);
            if (status   != null && !status.equals("ALL")) ps.setString(idx++, status);
            if (fromDate != null && !fromDate.isEmpty())   ps.setString(idx++, fromDate);
            if (toDate   != null && !toDate.isEmpty())     ps.setString(idx,   toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(BASE_SQL + "ORDER BY a.appointment_date DESC");
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Appointment> searchAppointments(String patientName, String doctorName, String status, String fromDate, String toDate) {
        List<Appointment> list = new ArrayList<>();
        // Use ? placeholders for all user-supplied values to prevent SQL injection
        StringBuilder sql = new StringBuilder(BASE_SQL + "WHERE 1=1");
        if (patientName != null && !patientName.isEmpty()) sql.append(" AND u.full_name LIKE ?");
        if (doctorName  != null && !doctorName.isEmpty())  sql.append(" AND du.full_name LIKE ?");
        if (status      != null && !status.equals("ALL"))  sql.append(" AND a.status = ?");
        if (fromDate    != null && !fromDate.isEmpty())    sql.append(" AND a.appointment_date >= ?");
        if (toDate      != null && !toDate.isEmpty())      sql.append(" AND a.appointment_date <= ?");
        sql.append(" ORDER BY a.appointment_date DESC");
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            if (patientName != null && !patientName.isEmpty()) ps.setString(idx++, "%" + patientName + "%");
            if (doctorName  != null && !doctorName.isEmpty())  ps.setString(idx++, "%" + doctorName  + "%");
            if (status      != null && !status.equals("ALL"))  ps.setString(idx++, status);
            if (fromDate    != null && !fromDate.isEmpty())    ps.setString(idx++, fromDate);
            if (toDate      != null && !toDate.isEmpty())      ps.setString(idx,   toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Appointment> getAppointmentsByDateRange(String fromDate, String toDate) {
        List<Appointment> list = new ArrayList<>();
        String sql = BASE_SQL + "WHERE a.appointment_date BETWEEN ? AND ? ORDER BY a.appointment_date";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, fromDate); ps.setString(2, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<String[]> getAppointmentsPerDoctor() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT u.full_name, COUNT(a.id) as cnt FROM appointments a " +
                     "JOIN doctors d ON a.doctor_id = d.id JOIN users u ON d.user_id = u.id " +
                     "GROUP BY d.id ORDER BY cnt DESC";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) result.add(new String[]{rs.getString("full_name"), rs.getString("cnt")});
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    public List<String[]> getMonthlyStats() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT DATE_FORMAT(appointment_date,'%Y-%m') as month, COUNT(*) as cnt " +
                     "FROM appointments GROUP BY month ORDER BY month DESC LIMIT 12";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) result.add(new String[]{rs.getString("month"), rs.getString("cnt")});
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE status = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTotalAppointments() {
        String sql = "SELECT COUNT(*) FROM appointments";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private List<Appointment> queryById(String sql, int id) {
        List<Appointment> list = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapAppointment(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private Appointment mapAppointment(ResultSet rs) throws SQLException {
        Appointment a = new Appointment();
        a.setId(rs.getInt("id"));                       a.setPatientId(rs.getInt("patient_id"));
        a.setDoctorId(rs.getInt("doctor_id"));          a.setScheduleId(rs.getInt("schedule_id"));
        a.setPatientName(rs.getString("patient_name")); a.setDoctorName(rs.getString("doctor_name"));
        a.setAppointmentDate(rs.getString("appointment_date")); a.setStatus(rs.getString("status"));
        a.setMedicalNotes(rs.getString("medical_notes")); a.setReason(rs.getString("reason"));
        a.setStartTime(rs.getString("start_time"));     a.setEndTime(rs.getString("end_time"));
        return a;
    }
}
