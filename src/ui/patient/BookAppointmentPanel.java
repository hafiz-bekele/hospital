package ui.patient;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.ScheduleDAO;
import models.Appointment;
import models.Doctor;
import models.Schedule;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookAppointmentPanel extends JPanel {

    private User patient;
    private JComboBox<Doctor> doctorCombo;
    private JComboBox<Schedule> scheduleCombo;
    private JTextArea reasonArea;
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JLabel selectedDoctorInfo;

    private DoctorDAO doctorDAO       = new DoctorDAO();
    private ScheduleDAO scheduleDAO   = new ScheduleDAO();
    private AppointmentDAO apptDAO    = new AppointmentDAO();

    public BookAppointmentPanel(User patient) {
        this.patient = patient;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        // ── Top: Doctor table ────────────────────────────────────
        String[] cols = {"ID", "Doctor Name", "Specialization", "Qualification", "Experience (yrs)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setRowHeight(24);
        doctorTable.setFont(new Font("Arial", Font.PLAIN, 13));
        doctorTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        doctorTable.getColumnModel().getColumn(0).setMaxWidth(45);

        // When user clicks a row in the table, auto-select that doctor in the combo
        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = doctorTable.getSelectedRow();
                if (row >= 0) {
                    int docId = (int) tableModel.getValueAt(row, 0);
                    for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                        if (doctorCombo.getItemAt(i).getId() == docId) {
                            doctorCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        JScrollPane tableScroll = new JScrollPane(doctorTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Available Doctors  (click a row to select)"));
        tableScroll.setPreferredSize(new Dimension(0, 190));

        // ── Bottom: Booking form ─────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        form.setBorder(BorderFactory.createTitledBorder("Book an Appointment"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Doctor combo
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(bold("Select Doctor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        doctorCombo.addActionListener(e -> onDoctorSelected());
        form.add(doctorCombo, gbc);

        // Doctor info strip
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        selectedDoctorInfo = new JLabel(" ");
        selectedDoctorInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        selectedDoctorInfo.setForeground(new Color(0, 100, 100));
        form.add(selectedDoctorInfo, gbc);

        // Schedule combo
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        form.add(bold("Select Schedule:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        scheduleCombo = new JComboBox<>();
        scheduleCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        form.add(scheduleCombo, gbc);

        // Reason
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(bold("Reason for Visit:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        reasonArea = new JTextArea(3, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(new Font("Arial", Font.PLAIN, 13));
        form.add(new JScrollPane(reasonArea), gbc);

        // Book button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER; gbc.fill = GridBagConstraints.NONE;
        JButton bookBtn = new JButton("  Book Appointment  ");
        bookBtn.setBackground(new Color(30, 100, 180));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));
        bookBtn.setFocusPainted(false);
        bookBtn.setPreferredSize(new Dimension(220, 36));
        bookBtn.addActionListener(e -> bookAppointment());
        form.add(bookBtn, gbc);

        add(tableScroll, BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);

        loadDoctors();
    }

    private void loadDoctors() {
        tableModel.setRowCount(0);
        doctorCombo.removeAllItems();
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{d.getId(), "Dr. " + d.getFullName(), d.getSpecialization(), d.getQualification(), d.getExperienceYears()});
            doctorCombo.addItem(d);
        }
        onDoctorSelected();
    }

    private void onDoctorSelected() {
        Doctor d = (Doctor) doctorCombo.getSelectedItem();
        if (d == null) { selectedDoctorInfo.setText(" "); return; }
        selectedDoctorInfo.setText("  Specialization: " + d.getSpecialization() +
            "   |   Qualification: " + (d.getQualification() != null ? d.getQualification() : "—") +
            "   |   Experience: " + d.getExperienceYears() + " yrs");
        loadSchedules(d);
    }

    private void loadSchedules(Doctor d) {
        scheduleCombo.removeAllItems();
        for (Schedule s : scheduleDAO.getSchedulesByDoctor(d.getId())) {
            scheduleCombo.addItem(s);
        }
    }

    private void bookAppointment() {
        Doctor doctor   = (Doctor)   doctorCombo.getSelectedItem();
        Schedule sched  = (Schedule) scheduleCombo.getSelectedItem();

        if (doctor == null || sched == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor and a schedule.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment a = new Appointment();
        a.setPatientId(patient.getId());
        a.setDoctorId(doctor.getId());
        a.setScheduleId(sched.getId());
        a.setAppointmentDate(sched.getAvailableDate());
        a.setReason(reasonArea.getText().trim());

        if (apptDAO.bookAppointment(a)) {
            JOptionPane.showMessageDialog(this,
                "Appointment booked!\nDoctor: Dr. " + doctor.getFullName() +
                "\nDate: " + sched.getAvailableDate() +
                "\nTime: " + sched.getStartTime() + " - " + sched.getEndTime() +
                "\nStatus: PENDING — waiting for approval.",
                "Booked Successfully", JOptionPane.INFORMATION_MESSAGE);
            reasonArea.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to book appointment. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        return l;
    }
}
