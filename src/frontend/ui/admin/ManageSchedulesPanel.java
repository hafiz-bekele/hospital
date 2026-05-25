package frontend.ui.admin;

import backend.dao.DoctorDAO;
import backend.dao.ScheduleDAO;
import backend.models.Doctor;
import backend.models.Schedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageSchedulesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private ScheduleDAO scheduleDAO = new ScheduleDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();

    public ManageSchedulesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
    }

    private void buildUI() {
        String[] cols = {"ID", "Doctor", "Date", "Start Time", "End Time", "Max Patients"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Doctor Schedules"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton addBtn = new JButton("Add Schedule");
        styleBtn(addBtn, new Color(40, 140, 40));
        addBtn.addActionListener(e -> showAddScheduleDialog());

        JButton deleteBtn = new JButton("Delete Schedule");
        styleBtn(deleteBtn, new Color(200, 50, 50));
        deleteBtn.addActionListener(e -> deleteSchedule());

        JButton refreshBtn = new JButton("Refresh");
        styleBtn(refreshBtn, new Color(70, 70, 70));
        refreshBtn.addActionListener(e -> loadSchedules());

        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        add(btnPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        loadSchedules();
    }

    private void loadSchedules() {
        tableModel.setRowCount(0);
        for (Schedule s : scheduleDAO.getAllSchedules()) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getDoctorName(), s.getAvailableDate(),
                s.getStartTime(), s.getEndTime(), s.getMaxPatients()
            });
        }
    }

    private void showAddScheduleDialog() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        if (doctors.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No doctors available. Add doctors first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Doctor> doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));
        JTextField dateField = new JTextField("2026-05-01", 12);
        JTextField startField = new JTextField("09:00", 8);
        JTextField endField = new JTextField("17:00", 8);
        JTextField maxField = new JTextField("10", 5);

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Doctor:")); panel.add(doctorCombo);
        panel.add(new JLabel("Date (YYYY-MM-DD):")); panel.add(dateField);
        panel.add(new JLabel("Start Time (HH:MM):")); panel.add(startField);
        panel.add(new JLabel("End Time (HH:MM):")); panel.add(endField);
        panel.add(new JLabel("Max Patients:")); panel.add(maxField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Schedule", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();
        int max = 10;
        try { max = Integer.parseInt(maxField.getText().trim()); } catch (NumberFormatException ignored) {}

        Schedule s = new Schedule();
        s.setDoctorId(doctor.getId());
        s.setAvailableDate(dateField.getText().trim());
        s.setStartTime(startField.getText().trim());
        s.setEndTime(endField.getText().trim());
        s.setMaxPatients(max);

        if (scheduleDAO.addSchedule(s)) {
            JOptionPane.showMessageDialog(this, "Schedule added.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSchedules();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add schedule.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSchedule() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a schedule.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this schedule?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (scheduleDAO.deleteSchedule(id)) {
                JOptionPane.showMessageDialog(this, "Schedule deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSchedules();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
