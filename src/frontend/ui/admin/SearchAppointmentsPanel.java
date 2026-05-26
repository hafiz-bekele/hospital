package frontend.ui.admin;

import backend.dao.AppointmentDAO;
import backend.models.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SearchAppointmentsPanel extends JPanel {

    private JTextField patientField, doctorField, fromField, toField;
    private JComboBox<String> statusCombo;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel resultLbl;
    private AppointmentDAO apptDAO = new AppointmentDAO();

    public SearchAppointmentsPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    // Build the main UI with search form and results table
    private void buildUI() {
        // ── Search form ──────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 245, 255));
        form.setBorder(BorderFactory.createTitledBorder("Search Filters"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        patientField = new JTextField(14);
        doctorField  = new JTextField(14);
        fromField    = new JTextField("2026-01-01", 10);
        toField      = new JTextField("2026-12-31", 10);
        statusCombo  = new JComboBox<>(new String[]{"ALL", "PENDING", "APPROVED", "REJECTED", "CANCELLED"});

        int col = 0;
        addField(form, gbc, col++, 0, "Patient Name:", patientField);
        addField(form, gbc, col++, 0, "Doctor Name:",  doctorField);
        addField(form, gbc, col++, 0, "Status:",       statusCombo);
        addField(form, gbc, col++, 0, "From (YYYY-MM-DD):", fromField);
        addField(form, gbc, col,   0, "To (YYYY-MM-DD):",   toField);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5; gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        JButton searchBtn = new JButton("  🔍  Search  ");
        searchBtn.setBackground(new Color(30, 100, 180));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 13));
        searchBtn.setFocusPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.setBorderPainted(false);
        searchBtn.setPreferredSize(new Dimension(160, 36));
        searchBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> doSearch());
        form.add(searchBtn, gbc);

        // ── Results table ────────────────────────────────────────
        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col2) {
                Component c = super.prepareRenderer(r, row, col2);
                String status = (String) getValueAt(row, 6);
                if (!isRowSelected(row)) {
                    if ("APPROVED".equals(status))        c.setBackground(new Color(220, 255, 220));
                    else if ("REJECTED".equals(status))   c.setBackground(new Color(255, 220, 220));
                    else if ("CANCELLED".equals(status))  c.setBackground(new Color(220, 220, 220));
                    else                                  c.setBackground(new Color(255, 255, 210));
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

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(6).setCellRenderer(center);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Results"));

        resultLbl = new JLabel("Enter filters and click Search.");
        resultLbl.setFont(new Font("Arial", Font.ITALIC, 12));
        resultLbl.setForeground(Color.GRAY);
        resultLbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        add(form,      BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);
        add(resultLbl, BorderLayout.SOUTH);
    }

    // Add a label + field pair to the search form
    private void addField(JPanel p, GridBagConstraints gbc, int col, int row, String label, JComponent field) {
        gbc.gridx = col; gbc.gridy = row; gbc.gridwidth = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel fp = new JPanel(new BorderLayout(4, 2));
        fp.setBackground(new Color(245, 245, 255));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        fp.add(lbl, BorderLayout.NORTH);
        fp.add(field, BorderLayout.CENTER);
        p.add(fp, gbc);
    }

    // Run the search query and populate the results table
    private void doSearch() {
        String patient = patientField.getText().trim();
        String doctor  = doctorField.getText().trim();
        String status  = (String) statusCombo.getSelectedItem();
        String from    = fromField.getText().trim();
        String to      = toField.getText().trim();

        List<Appointment> list = apptDAO.searchAppointments(patient, doctor, status, from, to);
        tableModel.setRowCount(0);
        for (Appointment a : list) {
            tableModel.addRow(new Object[]{
                a.getId(), a.getPatientName(), "Dr. " + a.getDoctorName(),
                a.getAppointmentDate(), a.getStartTime() + " - " + a.getEndTime(),
                a.getReason() != null ? a.getReason() : "",
                a.getStatus(),
                a.getMedicalNotes() != null ? a.getMedicalNotes() : ""
            });
        }
        resultLbl.setText("Found " + list.size() + " appointment(s).");
        resultLbl.setForeground(list.isEmpty() ? Color.RED : new Color(0, 120, 0));
    }
}
