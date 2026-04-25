package ui.admin;

import dao.DoctorDAO;
import dao.UserDAO;
import models.Doctor;
import models.User;

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
        table.setRowHeight(22);
        table.getColumnModel().getColumn(0).setMaxWidth(40);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Doctors List"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton addBtn = new JButton("Add Doctor");
        addBtn.setBackground(new Color(60, 160, 60));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddDoctorDialog());

        JButton deleteBtn = new JButton("Delete Doctor");
        deleteBtn.setBackground(new Color(200, 50, 50));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.addActionListener(e -> deleteDoctor());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadDoctors());

        btnPanel.add(addBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);

        add(btnPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        loadDoctors();
    }

    private void loadDoctors() {
        tableModel.setRowCount(0);
        for (Doctor d : doctorDAO.getAllDoctors()) {
            tableModel.addRow(new Object[]{d.getId(), d.getFullName(), d.getSpecialization(), d.getQualification(), d.getExperienceYears()});
        }
    }

    private void showAddDoctorDialog() {
        JTextField nameField = new JTextField(15);
        JTextField usernameField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JTextField emailField = new JTextField(15);
        JTextField phoneField = new JTextField(15);
        JTextField specField = new JTextField(15);
        JTextField qualField = new JTextField(15);
        JTextField expField = new JTextField("0", 5);

        JPanel panel = new JPanel(new GridLayout(8, 2, 8, 8));
        panel.add(new JLabel("Full Name:")); panel.add(nameField);
        panel.add(new JLabel("Username:")); panel.add(usernameField);
        panel.add(new JLabel("Password:")); panel.add(passField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Specialization:")); panel.add(specField);
        panel.add(new JLabel("Qualification:")); panel.add(qualField);
        panel.add(new JLabel("Experience (yrs):")); panel.add(expField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Doctor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        String spec = specField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || spec.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, username, password, and specialization are required.", "Warning", JOptionPane.WARNING_MESSAGE);
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

    private void deleteDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete Dr. " + name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(id)) {
                JOptionPane.showMessageDialog(this, "Doctor deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctors();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete doctor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
