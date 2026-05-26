package frontend.ui.admin;

import backend.dao.AppointmentDAO;
import backend.dao.UserDAO;
import backend.models.Appointment;
import backend.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Window;
import java.util.List;

public class ViewPatientsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel totalLbl;
    private UserDAO userDAO = new UserDAO();
    private AppointmentDAO apptDAO = new AppointmentDAO();

    public ViewPatientsPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    // Build the main UI with stats bar, patient table, and action buttons
    private void buildUI() {
        // ── Stats bar ────────────────────────────────────────────
        JPanel statsBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        statsBar.setBackground(new Color(230, 245, 230));
        statsBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        totalLbl = new JLabel("Total Patients: 0");
        totalLbl.setFont(new Font("Arial", Font.BOLD, 13));
        totalLbl.setForeground(new Color(30, 100, 180));
        statsBar.add(totalLbl);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"ID", "Full Name", "Username", "Email", "Phone"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(45);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Registered Patients"));

        // ── Buttons ──────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton viewApptBtn = new JButton("📋  View Appointments");
        JButton refreshBtn  = new JButton("↻  Refresh");
        styleBtn(viewApptBtn, new Color(30, 100, 180));
        styleBtn(refreshBtn,  new Color(70, 70, 70));

        viewApptBtn.addActionListener(e -> viewPatientAppointments());
        refreshBtn.addActionListener(e  -> refresh());

        btnPanel.add(viewApptBtn);
        btnPanel.add(refreshBtn);

        add(statsBar,  BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(btnPanel,  BorderLayout.SOUTH);

        refresh();
    }

    // Reload all patients from DB and update the table
    public void refresh() {
        tableModel.setRowCount(0);
        List<User> patients = userDAO.getAllPatients();
        for (User u : patients) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getFullName(),
                u.getUsername(),
                u.getEmail()  != null ? u.getEmail()  : "—",
                u.getPhone()  != null ? u.getPhone()  : "—"
            });
        }
        totalLbl.setText("Total Patients: " + patients.size());
    }

    // Show a popup with all appointments for the selected patient
    private void viewPatientAppointments() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a patient.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int patientId   = (int)    tableModel.getValueAt(row, 0);
        String patName  = (String) tableModel.getValueAt(row, 1);

        List<Appointment> list = apptDAO.getAppointmentsByPatient(patientId);

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, patName + " has no appointments yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] cols = {"ID", "Doctor", "Date", "Time", "Reason", "Status", "Notes"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Appointment a : list) {
            m.addRow(new Object[]{
                a.getId(),
                "Dr. " + a.getDoctorName(),
                a.getAppointmentDate(),
                a.getStartTime() + " - " + a.getEndTime(),
                a.getReason() != null ? a.getReason() : "",
                a.getStatus(),
                a.getMedicalNotes() != null ? a.getMedicalNotes() : ""
            });
        }

        JTable apptTable = new JTable(m) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int rr, int cc) {
                Component c = super.prepareRenderer(r, rr, cc);
                String status = (String) getValueAt(rr, 5);
                if (!isRowSelected(rr)) {
                    if ("APPROVED".equals(status))      c.setBackground(new Color(220, 255, 220));
                    else if ("REJECTED".equals(status)) c.setBackground(new Color(255, 220, 220));
                    else                                c.setBackground(new Color(255, 255, 210));
                }
                return c;
            }
        };
        apptTable.setRowHeight(22);
        apptTable.setFont(new Font("Arial", Font.PLAIN, 12));
        apptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane sp = new JScrollPane(apptTable);
        sp.setPreferredSize(new Dimension(750, 280));

        JOptionPane.showMessageDialog(this, sp,
            "Appointments for " + patName + "  (" + list.size() + " total)",
            JOptionPane.PLAIN_MESSAGE);
    }

    // Create a styled action button with a colored background
    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(180, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
