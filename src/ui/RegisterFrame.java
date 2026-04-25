package ui;

import dao.UserDAO;
import models.User;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private JTextField nameField, usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmField;
    private UserDAO userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("Patient Registration");
        setSize(440, 420);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(240, 248, 255));

        JPanel header = new JPanel();
        header.setBackground(new Color(60, 160, 60));
        JLabel title = new JLabel("Patient Registration");
        title.setFont(new Font("Arial", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        header.add(title);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(240, 248, 255));
        form.setBorder(BorderFactory.createEmptyBorder(15, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Full Name:", "Username:", "Email:", "Phone:", "Password:", "Confirm Password:"};
        int row = 0;
        for (String lbl : labels) {
            gbc.gridx = 0; gbc.gridy = row;
            form.add(new JLabel(lbl), gbc);
            gbc.gridx = 1;
            JComponent field;
            switch (lbl) {
                case "Password:":    field = passwordField = new JPasswordField(15); break;
                case "Confirm Password:": field = confirmField = new JPasswordField(15); break;
                case "Full Name:":   field = nameField = new JTextField(15); break;
                case "Username:":    field = usernameField = new JTextField(15); break;
                case "Email:":       field = emailField = new JTextField(15); break;
                default:             field = phoneField = new JTextField(15); break;
            }
            form.add(field, gbc);
            row++;
        }

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(new Color(240, 248, 255));

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(60, 160, 60));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setPreferredSize(new Dimension(110, 32));
        registerBtn.addActionListener(e -> doRegister());

        JButton backBtn = new JButton("Back to Login");
        backBtn.setFocusPainted(false);
        backBtn.setPreferredSize(new Dimension(120, 32));
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        form.add(btnPanel, gbc);

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        add(main);
    }

    private void doRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm = new String(confirmField.getPassword()).trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, username, and password are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.usernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username already taken.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setFullName(name);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("PATIENT");

        if (userDAO.register(user)) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
