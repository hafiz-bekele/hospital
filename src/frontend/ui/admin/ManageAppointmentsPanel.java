package frontend.ui.admin;

import backend.dao.AppointmentDAO;
import backend.dao.NotificationDAO;
import backend.models.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageAppointmentsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalLbl, pendingLbl, approvedLbl, rejectedLbl;
    private AppointmentDAO apptDAO = new AppointmentDAO();
    private NotificationDAO notifDAO = new NotificationDAO();

    public ManageAppointmentsPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    private void buildUI() {
        // ── Stats bar ────────────────────────────────────────────
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        statsBar.setBackground(new Color(240, 230, 255));
        statsBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        totalLbl    = badge("Total",    Color.DARK_GRAY);
        pendingLbl  = badge("Pending",  new Color(180, 120, 0));
        approvedLbl = badge("Approved", new Color(40, 140, 40));
        rejectedLbl = badge("Rejected", new Color(180, 40, 40));

        statsBar.add(totalLbl);
        statsBar.add(pendingLbl);
        statsBar.add(approvedLbl);
        statsBar.add(rejectedLbl);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time Slot", "Reason", "Status", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String status = (String) getValueAt(row, 6);
                if (!isRowSelected(row)) {
                    if ("APPROVED".equals(status))      c.setBackground(new Color(220, 255, 220));
                    else if ("REJECTED".equals(status)) c.setBackground(new Color(255, 220, 220));
                    else                                c.setBackground(new Color(255, 255, 210));
                } else {
                    c.setBackground(new Color(200, 180, 255));
                }
                return c;
            }
        };
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(90);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("All Appointments"));

        // ── Buttons ──────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton approveBtn = styledBtn("✔  Approve",    new Color(40, 140, 40));
        JButton rejectBtn  = styledBtn("✘  Reject",     new Color(180, 40, 40));
        JButton pendingBtn = styledBtn("⟳  Set Pending", new Color(100, 100, 100));
        JButton refreshBtn = new JButton("↻  Refresh");
        styleBtn(refreshBtn, new Color(70, 70, 70));

        approveBtn.addActionListener(e -> updateStatus("APPROVED"));
        rejectBtn.addActionListener(e  -> updateStatus("REJECTED"));
        pendingBtn.addActionListener(e -> updateStatus("PENDING"));
        refreshBtn.addActionListener(e -> refresh());

        btnPanel.add(approveBtn);
        btnPanel.add(rejectBtn);
        btnPanel.add(pendingBtn);
        btnPanel.add(refreshBtn);

        add(statsBar,  BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(btnPanel,  BorderLayout.SOUTH);

        refresh();
    }

    public void refresh() {
        tableModel.setRowCount(0);
        int total = 0, pending = 0, approved = 0, rejected = 0;
        List<Appointment> list = apptDAO.getAllAppointments();
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                a.getId(),
                a.getPatientName(),
                "Dr. " + a.getDoctorName(),
                a.getAppointmentDate(),
                a.getStartTime() + " - " + a.getEndTime(),
                a.getReason() != null ? a.getReason() : "",
                a.getStatus(),
                a.getMedicalNotes() != null ? a.getMedicalNotes() : ""
            });
            total++;
            if ("PENDING".equals(a.getStatus()))       pending++;
            else if ("APPROVED".equals(a.getStatus())) approved++;
            else if ("REJECTED".equals(a.getStatus())) rejected++;
        }
        totalLbl.setText("Total: " + total);
        pendingLbl.setText("Pending: " + pending);
        approvedLbl.setText("Approved: " + approved);
        rejectedLbl.setText("Rejected: " + rejected);
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an appointment.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String patient = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Set appointment for " + patient + " to " + status + "?",
            "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        if (apptDAO.updateStatus(id, status)) {
            // Notify patient
            List<Appointment> all = apptDAO.getAllAppointments();
            all.stream().filter(a -> a.getId() == id).findFirst().ifPresent(a ->
                notifDAO.send(a.getPatientId(),
                    "Your appointment with Dr. " + a.getDoctorName() + " on " + a.getAppointmentDate() +
                    " has been " + status.toLowerCase() + " by the admin.")
            );
            JOptionPane.showMessageDialog(this, "Status updated to " + status + ".", "Done", JOptionPane.INFORMATION_MESSAGE);
            refresh();
        }
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

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(120, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JLabel badge(String text, Color color) {
        JLabel l = new JLabel(text + ": 0");
        l.setForeground(color);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        return l;
    }
}
