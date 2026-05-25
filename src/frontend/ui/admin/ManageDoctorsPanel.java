package frontend.ui.admin;

import backend.dao.DoctorDAO;
import backend.dao.UserDAO;
import backend.models.Doctor;
import backend.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageDoctorsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private DoctorDAO doctorDAO = new DoctorDAO();
    private UserDAO userDAO = new UserDAO();

    public ManageDoctorsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        buildUI();
    }

    private void buildUI() {
        String[] cols = {"ID", "Full Name", "Specialization", "Qualification", "Experience (yrs)"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Doctors List"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton addBtn = new JButton("Add Doctor");
        styleBtn(addBtn, new Color(40, 140, 40));
        addBtn.addActionListener(e -> showAddDoctorDialog());

        JButton editBtn = new JButton("Edit Doctor");
        styleBtn(editBtn, new Color(30, 100, 180));
        editBtn.addActionListener(e -> showEditDoctorDialog());

        JButton credBtn = new JButton("View Credentials");
        styleBtn(credBtn, new Color(100, 60, 160));
        credBtn.addActionListener(e -> showCredentials());

        JButton deleteBtn = new JButton("Delete Doctor");
        styleBtn(deleteBtn, new Color(200, 50, 50));
        deleteBtn.addActionListener(e -> deleteDoctor());

        JButton refreshBtn = new JButton("Refresh");
        styleBtn(refreshBtn, new Color(70, 70, 70));
        refreshBtn.addActionListener(e -> loadDoctors());

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(credBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        add(btnPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        loadDoctors();
    }

    private void loadDoctors() {
        tableModel.setRowCount(0);
        for (Doctor d : doctorDAO.getAllDoctors()) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getFullName(), d.getSpecialization(),
                d.getQualification(), d.getExperienceYears()
            });
        }
    }

    // ── Add Doctor ────────────────────────────────────────────────

    private void showAddDoctorDialog() {
        JTextField nameField  = new JTextField(15);
        JTextField userField  = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JTextField specField  = new JTextField(15);
        JTextField qualField  = new JTextField(15);
        JTextField expField   = new JTextField("0", 5);

        JPanel panel = new JPanel(new GridLayout(8, 2, 8, 8));
        panel.add(new JLabel("Full Name:"));        panel.add(nameField);
        panel.add(new JLabel("Username:"));         panel.add(userField);
        panel.add(new JLabel("Password:"));         panel.add(passField);
        panel.add(new JLabel("Email:"));            panel.add(emailField);
        panel.add(new JLabel("Phone:"));            panel.add(phoneField);
        panel.add(new JLabel("Specialization:"));   panel.add(specField);
        panel.add(new JLabel("Qualification:"));    panel.add(qualField);
        panel.add(new JLabel("Experience (yrs):")); panel.add(expField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Doctor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name     = nameField.getText().trim();
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String spec     = specField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || spec.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, username, password, and specialization are required.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int exp = 0;
        try { exp = Integer.parseInt(expField.getText().trim()); } catch (NumberFormatException ignored) {}

        User user = new User();
        user.setFullName(name);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(emailField.getText().trim());
        user.setPhone(phoneField.getText().trim());

        if (doctorDAO.addDoctor(user, spec, qualField.getText().trim(), exp)) {
            JOptionPane.showMessageDialog(this, "Doctor added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDoctors();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add doctor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Edit Doctor ───────────────────────────────────────────────

    private void showEditDoctorDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(row, 0);
        User user    = doctorDAO.getUserByDoctorId(doctorId);
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        Doctor doctor = doctors.stream().filter(d -> d.getId() == doctorId).findFirst().orElse(null);

        if (user == null || doctor == null) {
            JOptionPane.showMessageDialog(this, "Could not load doctor data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nameField  = new JTextField(user.getFullName(), 15);
        JTextField userField  = new JTextField(user.getUsername(), 15);
        JPasswordField passField = new JPasswordField(user.getPassword(), 15);
        JTextField emailField = new JTextField(user.getEmail() != null ? user.getEmail() : "", 15);
        JTextField phoneField = new JTextField(user.getPhone() != null ? user.getPhone() : "", 15);
        JTextField specField  = new JTextField(doctor.getSpecialization(), 15);
        JTextField qualField  = new JTextField(doctor.getQualification() != null ? doctor.getQualification() : "", 15);
        JTextField expField   = new JTextField(String.valueOf(doctor.getExperienceYears()), 5);

        // Show/hide password toggle
        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(new Font("Arial", Font.PLAIN, 11));
        showPass.addActionListener(e ->
            passField.setEchoChar(showPass.isSelected() ? (char) 0 : '•')
        );

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        addRow(panel, gbc, r++, "Full Name:",        nameField);
        addRow(panel, gbc, r++, "Username:",         userField);
        addRow(panel, gbc, r++, "Password:",         passField);

        // Show password checkbox spans col 1
        gbc.gridx = 1; gbc.gridy = r++; gbc.gridwidth = 1;
        panel.add(showPass, gbc);

        addRow(panel, gbc, r++, "Email:",            emailField);
        addRow(panel, gbc, r++, "Phone:",            phoneField);
        addRow(panel, gbc, r++, "Specialization:",   specField);
        addRow(panel, gbc, r++, "Qualification:",    qualField);
        addRow(panel, gbc, r,   "Experience (yrs):", expField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Edit Doctor — Dr. " + user.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String newName     = nameField.getText().trim();
        String newUsername = userField.getText().trim();
        String newPassword = new String(passField.getPassword()).trim();
        String newSpec     = specField.getText().trim();

        if (newName.isEmpty() || newUsername.isEmpty() || newPassword.isEmpty() || newSpec.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, username, password, and specialization are required.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check username uniqueness only if it changed
        if (!newUsername.equals(user.getUsername()) && userDAO.usernameExists(newUsername)) {
            JOptionPane.showMessageDialog(this, "Username already taken by another user.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int exp = doctor.getExperienceYears();
        try { exp = Integer.parseInt(expField.getText().trim()); } catch (NumberFormatException ignored) {}

        if (doctorDAO.updateDoctor(doctorId, newName, newUsername, newPassword,
                emailField.getText().trim(), phoneField.getText().trim(),
                newSpec, qualField.getText().trim(), exp)) {
            JOptionPane.showMessageDialog(this, "Doctor updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDoctors();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update doctor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── View Credentials ──────────────────────────────────────────

    private void showCredentials() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to view credentials.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(row, 0);
        User user    = doctorDAO.getUserByDoctorId(doctorId);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "Could not load credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Password field with show/hide toggle
        JPasswordField passDisplay = new JPasswordField(user.getPassword(), 18);
        passDisplay.setEditable(false);
        passDisplay.setBackground(new Color(245, 245, 245));

        JCheckBox showPass = new JCheckBox("Show password");
        showPass.setFont(new Font("Arial", Font.PLAIN, 11));
        showPass.addActionListener(e ->
            passDisplay.setEchoChar(showPass.isSelected() ? (char) 0 : '•')
        );

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int r = 0;
        addInfoRow(panel, gbc, r++, "Full Name:", user.getFullName());
        addInfoRow(panel, gbc, r++, "Username:",  user.getUsername());

        // Password row
        gbc.gridx = 0; gbc.gridy = r; gbc.weightx = 0;
        JLabel pLbl = new JLabel("Password:");
        pLbl.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(pLbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(passDisplay, gbc);
        r++;

        // Show checkbox
        gbc.gridx = 1; gbc.gridy = r++;
        panel.add(showPass, gbc);

        addInfoRow(panel, gbc, r++, "Email:", user.getEmail() != null ? user.getEmail() : "—");
        addInfoRow(panel, gbc, r,   "Phone:", user.getPhone() != null ? user.getPhone() : "—");

        JOptionPane.showMessageDialog(this, panel,
                "Credentials — Dr. " + user.getFullName(), JOptionPane.PLAIN_MESSAGE);
    }

    // ── Delete Doctor ─────────────────────────────────────────────

    private void deleteDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Dr. " + name + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(id)) {
                JOptionPane.showMessageDialog(this, "Doctor deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctors();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(field, gbc);
    }

    private void addInfoRow(JPanel p, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        p.add(val, gbc);
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
