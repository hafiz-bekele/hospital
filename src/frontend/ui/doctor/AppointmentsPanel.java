package frontend.ui.doctor;

import backend.dao.AppointmentDAO;
import backend.dao.NotificationDAO;
import backend.models.Appointment;
import backend.models.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AppointmentsPanel extends JPanel {

    private Doctor doctor;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel pendingCount, approvedCount, rejectedCount;
    private JComboBox<String> filterStatus;
    private JTextField filterFrom, filterTo;
    private AppointmentDAO apptDAO   = new AppointmentDAO();
    private NotificationDAO notifDAO = new NotificationDAO();

    public AppointmentsPanel(Doctor doctor) {
        this.doctor = doctor;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        // ── Filter bar ───────────────────────────────────────────
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        filterBar.setBackground(new Color(230, 245, 245));
        filterBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        filterStatus = new JComboBox<>(new String[]{"ALL", "PENDING", "APPROVED", "REJECTED", "CANCELLED"});
        filterFrom   = new JTextField("2026-01-01", 10);
        filterTo     = new JTextField("2026-12-31", 10);

        filterBar.add(new JLabel("Status:"));  filterBar.add(filterStatus);
        filterBar.add(new JLabel("From:"));    filterBar.add(filterFrom);
        filterBar.add(new JLabel("To:"));      filterBar.add(filterTo);

        JButton applyBtn = new JButton("Apply Filter");
        applyBtn.setFocusPainted(false);
        applyBtn.setBackground(new Color(0, 120, 120));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Arial", Font.BOLD, 13));
        applyBtn.setOpaque(true);
        applyBtn.setBorderPainted(false);
        applyBtn.setPreferredSize(new Dimension(130, 32));
        applyBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        applyBtn.addActionListener(e -> refresh());
        filterBar.add(applyBtn);

        // ── Stats bar ────────────────────────────────────────────
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        statsBar.setBackground(new Color(230, 245, 245));
        statsBar.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        pendingCount  = makeBadge("Pending",  new Color(200, 150, 0));
        approvedCount = makeBadge("Approved", new Color(40, 140, 40));
        rejectedCount = makeBadge("Rejected", new Color(180, 40, 40));
        statsBar.add(pendingCount); statsBar.add(approvedCount); statsBar.add(rejectedCount);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filterBar, BorderLayout.NORTH);
        topPanel.add(statsBar,  BorderLayout.SOUTH);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"ID", "Patient Name", "Date", "Time Slot", "Reason", "Status", "Medical Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String status = (String) getValueAt(row, 5);
                if (!isRowSelected(row)) {
                    if ("APPROVED".equals(status))        c.setBackground(new Color(220, 255, 220));
                    else if ("REJECTED".equals(status))   c.setBackground(new Color(255, 220, 220));
                    else if ("CANCELLED".equals(status))  c.setBackground(new Color(220, 220, 220));
                    else                                  c.setBackground(new Color(255, 255, 210));
                } else {
                    c.setBackground(new Color(173, 216, 230));
                }
                return c;
            }
        };
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Patient Appointments"));

        // ── Buttons ──────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton approveBtn = styledBtn("✔  Approve",     new Color(40, 140, 40));
        JButton rejectBtn  = styledBtn("✘  Reject",      new Color(180, 40, 40));
        JButton notesBtn   = styledBtn("📝  Add Notes",  new Color(30, 100, 180));
        JButton viewBtn    = styledBtn("👁  View Patient", new Color(100, 60, 160));
        JButton refreshBtn = styledBtn("↻  Refresh",     new Color(70, 70, 70));

        approveBtn.addActionListener(e -> updateStatus("APPROVED"));
        rejectBtn.addActionListener(e  -> updateStatus("REJECTED"));
        notesBtn.addActionListener(e   -> addNotes());
        viewBtn.addActionListener(e    -> viewPatientDetails());
        refreshBtn.addActionListener(e -> refresh());

        btnPanel.add(approveBtn); btnPanel.add(rejectBtn);
        btnPanel.add(notesBtn);   btnPanel.add(viewBtn); btnPanel.add(refreshBtn);

        add(topPanel,  BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(btnPanel,  BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        int pending = 0, approved = 0, rejected = 0;
        String status = (String) filterStatus.getSelectedItem();
        String from   = filterFrom.getText().trim();
        String to     = filterTo.getText().trim();

        List<Appointment> list = apptDAO.getAppointmentsByDoctorFiltered(doctor.getId(), status, from, to);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                a.getId(), a.getPatientName(), a.getAppointmentDate(),
                a.getStartTime() + " - " + a.getEndTime(),
                a.getReason() != null ? a.getReason() : "",
                a.getStatus(),
                a.getMedicalNotes() != null ? a.getMedicalNotes() : ""
            });
            if ("PENDING".equals(a.getStatus()))       pending++;
            else if ("APPROVED".equals(a.getStatus())) approved++;
            else if ("REJECTED".equals(a.getStatus())) rejected++;
        }
        pendingCount.setText("Pending: " + pending);
        approvedCount.setText("Approved: " + approved);
        rejectedCount.setText("Rejected: " + rejected);
    }

    private int getSelectedId() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) tableModel.getValueAt(row, 0);
    }

    private void updateStatus(String status) {
        int id = getSelectedId();
        if (id < 0) return;
        int row = table.getSelectedRow();
        String patientName = (String) tableModel.getValueAt(row, 1);
        String date        = (String) tableModel.getValueAt(row, 2);

        String label = "APPROVED".equals(status) ? "approve" : "reject";
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to " + label + " this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        if (apptDAO.updateStatus(id, status)) {
            // Find patient_id from the full list to send notification
            List<Appointment> all = apptDAO.getAppointmentsByDoctor(doctor.getId());
            all.stream().filter(a -> a.getId() == id).findFirst().ifPresent(a ->
                notifDAO.send(a.getPatientId(),
                    "Your appointment with Dr. " + doctor.getFullName() + " on " + date +
                    " has been " + status.toLowerCase() + ".")
            );
            JOptionPane.showMessageDialog(this, "Appointment " + status.toLowerCase() + ".", "Done", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
    }

    private void addNotes() {
        int id = getSelectedId();
        if (id < 0) return;
        int row = table.getSelectedRow();
        String existing = (String) tableModel.getValueAt(row, 6);
        String patient  = (String) tableModel.getValueAt(row, 1);

        JTextArea area = new JTextArea(existing != null ? existing : "", 6, 35);
        area.setLineWrap(true); area.setWrapStyleWord(true);
        area.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(new JLabel("Medical notes for " + patient + ":"), BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(this, p, "Medical Notes", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            if (apptDAO.updateMedicalNotes(id, area.getText().trim())) {
                // Notify patient
                List<Appointment> all = apptDAO.getAppointmentsByDoctor(doctor.getId());
                all.stream().filter(a -> a.getId() == id).findFirst().ifPresent(a ->
                    notifDAO.send(a.getPatientId(),
                        "Dr. " + doctor.getFullName() + " added medical notes to your appointment on " + a.getAppointmentDate() + ".")
                );
                JOptionPane.showMessageDialog(this, "Notes saved successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            }
        }
    }

    private void viewPatientDetails() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int apptId = (int) tableModel.getValueAt(row, 0);
        List<Appointment> list = apptDAO.getAppointmentsByDoctor(doctor.getId());
        Appointment sel = list.stream().filter(a -> a.getId() == apptId).findFirst().orElse(null);
        if (sel == null) return;

        JPanel info = new JPanel(new GridLayout(0, 2, 10, 8));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addInfoRow(info, "Patient Name:",     sel.getPatientName());
        addInfoRow(info, "Appointment Date:", sel.getAppointmentDate());
        addInfoRow(info, "Time Slot:",        sel.getStartTime() + " - " + sel.getEndTime());
        addInfoRow(info, "Reason:",           sel.getReason() != null ? sel.getReason() : "—");
        addInfoRow(info, "Status:",           sel.getStatus());
        addInfoRow(info, "Medical Notes:",    sel.getMedicalNotes() != null ? sel.getMedicalNotes() : "—");

        JOptionPane.showMessageDialog(this, info, "Patient Appointment Details", JOptionPane.PLAIN_MESSAGE);
    }

    private void addInfoRow(JPanel p, String label, String value) {
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Arial", Font.BOLD, 12)); p.add(lbl);
        JLabel val = new JLabel(value); val.setFont(new Font("Arial", Font.PLAIN, 12)); p.add(val);
    }

    private JButton styledBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(150, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel makeBadge(String text, Color color) {
        JLabel lbl = new JLabel(text + ": 0");
        lbl.setForeground(color); lbl.setFont(new Font("Arial", Font.BOLD, 13));
        return lbl;
    }
}
