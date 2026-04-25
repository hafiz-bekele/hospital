package ui.patient;

import dao.AppointmentDAO;
import dao.NotificationDAO;
import models.Appointment;
import models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyAppointmentsPanel extends JPanel {

    private User patient;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalLbl, pendingLbl, approvedLbl, rejectedLbl, cancelledLbl;
    private AppointmentDAO apptDAO   = new AppointmentDAO();
    private NotificationDAO notifDAO = new NotificationDAO();

    public MyAppointmentsPanel(User patient) {
        this.patient = patient;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        // ── Stats bar ────────────────────────────────────────────
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        statsBar.setBackground(new Color(230, 238, 255));
        statsBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        totalLbl     = badge("Total",     Color.DARK_GRAY);
        pendingLbl   = badge("Pending",   new Color(180, 120, 0));
        approvedLbl  = badge("Approved",  new Color(40, 140, 40));
        rejectedLbl  = badge("Rejected",  new Color(180, 40, 40));
        cancelledLbl = badge("Cancelled", new Color(100, 100, 100));

        statsBar.add(totalLbl); statsBar.add(pendingLbl);
        statsBar.add(approvedLbl); statsBar.add(rejectedLbl); statsBar.add(cancelledLbl);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"ID", "Doctor", "Date", "Time Slot", "Reason", "Status", "Medical Notes"};
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
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("My Appointments"));

        // ── Buttons ──────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton viewBtn    = new JButton("👁  View Details");
        JButton cancelBtn  = new JButton("✘  Cancel Appointment");
        JButton refreshBtn = new JButton("↻  Refresh");

        cancelBtn.setBackground(new Color(160, 60, 60));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 12));
        cancelBtn.setFocusPainted(false);
        viewBtn.setFocusPainted(false);
        refreshBtn.setFocusPainted(false);

        viewBtn.addActionListener(e    -> viewDetails());
        cancelBtn.addActionListener(e  -> cancelAppointment());
        refreshBtn.addActionListener(e -> refresh());

        btnPanel.add(viewBtn);
        btnPanel.add(cancelBtn);
        btnPanel.add(refreshBtn);

        add(statsBar,  BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(btnPanel,  BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        int total = 0, pending = 0, approved = 0, rejected = 0, cancelled = 0;
        for (Appointment a : apptDAO.getAppointmentsByPatient(patient.getId())) {
            tableModel.addRow(new Object[]{
                a.getId(), "Dr. " + a.getDoctorName(),
                a.getAppointmentDate(), a.getStartTime() + " - " + a.getEndTime(),
                a.getReason() != null ? a.getReason() : "",
                a.getStatus(),
                a.getMedicalNotes() != null ? a.getMedicalNotes() : ""
            });
            total++;
            switch (a.getStatus()) {
                case "PENDING":   pending++;   break;
                case "APPROVED":  approved++;  break;
                case "REJECTED":  rejected++;  break;
                case "CANCELLED": cancelled++; break;
            }
        }
        totalLbl.setText("Total: " + total);
        pendingLbl.setText("Pending: " + pending);
        approvedLbl.setText("Approved: " + approved);
        rejectedLbl.setText("Rejected: " + rejected);
        cancelledLbl.setText("Cancelled: " + cancelled);
    }

    private void cancelAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Please select an appointment to cancel."); return; }
        String status = (String) tableModel.getValueAt(row, 5);
        if (!"PENDING".equals(status)) {
            warn("Only PENDING appointments can be cancelled."); return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Cancel this appointment with " + tableModel.getValueAt(row, 1) + "?",
            "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (apptDAO.cancelAppointment(id)) {
            notifDAO.send(patient.getId(), "Your appointment with " + tableModel.getValueAt(row, 1) +
                " on " + tableModel.getValueAt(row, 2) + " has been cancelled.");
            JOptionPane.showMessageDialog(this, "Appointment cancelled.", "Done", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
    }

    private void viewDetails() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Please select an appointment to view."); return; }

        String status = (String) tableModel.getValueAt(row, 5);
        String notes  = (String) tableModel.getValueAt(row, 6);
        Color statusColor = "APPROVED".equals(status) ? new Color(40, 140, 40)
                          : "REJECTED".equals(status) ? new Color(180, 40, 40)
                          : "CANCELLED".equals(status) ? Color.GRAY
                          : new Color(180, 120, 0);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addDetailRow(panel, gbc, 0, "Doctor:",    (String) tableModel.getValueAt(row, 1));
        addDetailRow(panel, gbc, 1, "Date:",      (String) tableModel.getValueAt(row, 2));
        addDetailRow(panel, gbc, 2, "Time Slot:", (String) tableModel.getValueAt(row, 3));
        addDetailRow(panel, gbc, 3, "Reason:",    (String) tableModel.getValueAt(row, 4));

        gbc.gridx = 0; gbc.gridy = 4;
        JLabel sLbl = new JLabel("Status:"); sLbl.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(sLbl, gbc);
        gbc.gridx = 1;
        JLabel sVal = new JLabel(status); sVal.setFont(new Font("Arial", Font.BOLD, 13)); sVal.setForeground(statusColor);
        panel.add(sVal, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        JLabel nLbl = new JLabel("Medical Notes:"); nLbl.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(nLbl, gbc);
        gbc.gridx = 1;
        JTextArea notesArea = new JTextArea(notes == null || notes.isEmpty() ? "No notes yet." : notes, 4, 25);
        notesArea.setEditable(false); notesArea.setLineWrap(true); notesArea.setWrapStyleWord(true);
        notesArea.setBackground(new Color(250, 250, 250)); notesArea.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(new JScrollPane(notesArea), gbc);

        JOptionPane.showMessageDialog(this, panel, "Appointment Details", JOptionPane.PLAIN_MESSAGE);
    }

    private void addDetailRow(JPanel p, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row;
        JLabel lbl = new JLabel(label); lbl.setFont(new Font("Arial", Font.BOLD, 13)); p.add(lbl, gbc);
        gbc.gridx = 1;
        JLabel val = new JLabel(value != null ? value : "—"); val.setFont(new Font("Arial", Font.PLAIN, 13)); p.add(val, gbc);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }

    private JLabel badge(String text, Color color) {
        JLabel l = new JLabel(text + ": 0"); l.setForeground(color); l.setFont(new Font("Arial", Font.BOLD, 13)); return l;
    }
}
