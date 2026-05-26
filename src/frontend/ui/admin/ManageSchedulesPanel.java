package frontend.ui.admin;

import backend.dao.DoctorDAO;
import backend.dao.ScheduleDAO;
import backend.models.Doctor;
import backend.models.Schedule;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
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

    // Build the main UI with table and action buttons
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

    // Load all schedules from DB and populate the table
    private void loadSchedules() {
        tableModel.setRowCount(0);
        for (Schedule s : scheduleDAO.getAllSchedules()) {
            tableModel.addRow(new Object[]{
                s.getId(), s.getDoctorName(), s.getAvailableDate(),
                s.getStartTime(), s.getEndTime(), s.getMaxPatients()
            });
        }
    }

    // Open a dialog to add a new schedule slot for a doctor
    private void showAddScheduleDialog() {
        // Always use the top-level JFrame as dialog parent.
        // Using 'this' (a JPanel inside CardLayout) can cause dialogs to
        // appear behind the main window or not appear at all.
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);

        List<Doctor> doctors = doctorDAO.getAllDoctors();
        if (doctors.isEmpty()) {
            JOptionPane.showMessageDialog(owner,
                    "No doctors available. Add a doctor first.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Doctor> doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));

        String today = LocalDate.now().toString();
        JTextField dateField  = new JTextField(today, 12);
        JTextField startField = new JTextField("09:00", 8);
        JTextField endField   = new JTextField("17:00", 8);
        JTextField maxField   = new JTextField("10", 5);

        JLabel dateHint  = hint("Format: YYYY-MM-DD  (e.g. " + today + ")");
        JLabel timeHint  = hint("Format: HH:MM  24-hour  (e.g. 09:00)");
        JLabel timeHint2 = hint("Format: HH:MM  24-hour  (e.g. 17:00)");

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 2, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        addFormRow(panel, g, r++, "Doctor:",             doctorCombo, null);
        addFormRow(panel, g, r++, "Date (YYYY-MM-DD):",  dateField,   dateHint);
        addFormRow(panel, g, r++, "Start Time (HH:MM):", startField,  timeHint);
        addFormRow(panel, g, r++, "End Time (HH:MM):",   endField,    timeHint2);
        addFormRow(panel, g, r,   "Max Patients:",        maxField,    null);

        int result = JOptionPane.showConfirmDialog(owner, panel,
                "Add Schedule", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        // ── Validate ──────────────────────────────────────────────
        String date  = dateField.getText().trim();
        String start = startField.getText().trim();
        String end   = endField.getText().trim();

        if (date.isEmpty() || start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(owner, "Date, start time, and end time are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(owner,
                    "Date must be in YYYY-MM-DD format.\nExample: " + today,
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!start.matches("\\d{2}:\\d{2}") || !end.matches("\\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(owner,
                    "Times must be in HH:MM format.\nExample: 09:00",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (start.compareTo(end) >= 0) {
            JOptionPane.showMessageDialog(owner, "Start time must be before end time.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int max = 10;
        try { max = Integer.parseInt(maxField.getText().trim()); }
        catch (NumberFormatException ignored) {}
        if (max <= 0) max = 1;

        Doctor doctor = (Doctor) doctorCombo.getSelectedItem();

        Schedule s = new Schedule();
        s.setDoctorId(doctor.getId());
        s.setAvailableDate(date);
        s.setStartTime(start);
        s.setEndTime(end);
        s.setMaxPatients(max);

        if (scheduleDAO.addSchedule(s)) {
            JOptionPane.showMessageDialog(owner,
                    "Schedule added!\n\n" +
                    "Doctor : " + doctor.getFullName() + "\n" +
                    "Date   : " + date + "\n" +
                    "Time   : " + start + " – " + end,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSchedules();
        } else {
            String err = scheduleDAO.getLastError();
            JOptionPane.showMessageDialog(owner,
                    "Failed to add schedule.\n\n" +
                    (err.isEmpty() ? "Check that MySQL is running." : "Error: " + err),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Small grey hint label shown below a field */
    private JLabel hint(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.ITALIC, 10));
        l.setForeground(new Color(130, 130, 130));
        return l;
    }

    /** Adds a label + field (+ optional hint) row to a GridBagLayout panel */
    private void addFormRow(JPanel p, GridBagConstraints g,
                             int row, String label, JComponent field, JLabel hint) {
        g.gridx = 0; g.gridy = row * 2; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(lbl, g);

        g.gridx = 1; g.weightx = 1;
        p.add(field, g);

        if (hint != null) {
            g.gridx = 1; g.gridy = row * 2 + 1;
            p.add(hint, g);
        }
    }

    // Delete the selected schedule slot
    private void deleteSchedule() {
        int row = table.getSelectedRow();
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        if (row < 0) {
            JOptionPane.showMessageDialog(owner, "Please select a schedule.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(owner, "Delete this schedule?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (scheduleDAO.deleteSchedule(id)) {
                JOptionPane.showMessageDialog(owner, "Schedule deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSchedules();
            } else {
                JOptionPane.showMessageDialog(owner, "Failed to delete schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Create a styled action button with a colored background
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
