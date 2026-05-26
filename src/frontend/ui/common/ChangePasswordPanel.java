package frontend.ui.common;

import backend.dao.UserDAO;
import backend.models.User;

import javax.swing.*;
import java.awt.*;

public class ChangePasswordPanel extends JPanel {

    private User user;
    private JPasswordField oldPassField, newPassField, confirmField;
    private UserDAO userDAO = new UserDAO();

    public ChangePasswordPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 255));
        buildUI();
    }

    // Build the change password form UI
    private void buildUI() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(40, 120, 40, 120),
            BorderFactory.createLineBorder(new Color(100, 100, 200), 2, true)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JLabel title = new JLabel("Change Password", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(60, 60, 160));
        card.add(title, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        // Fields
        oldPassField  = new JPasswordField(18);
        newPassField  = new JPasswordField(18);
        confirmField  = new JPasswordField(18);

        addRow(card, gbc, 1, "Current Password:", oldPassField);
        addRow(card, gbc, 2, "New Password:",     newPassField);
        addRow(card, gbc, 3, "Confirm New:",      confirmField);

        // Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        JButton saveBtn = new JButton("  Update Password  ");
        saveBtn.setBackground(new Color(60, 60, 160));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 13));
        saveBtn.setFocusPainted(false);
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setPreferredSize(new Dimension(200, 38));
        saveBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> doChange());
        card.add(saveBtn, gbc);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(245, 245, 255));
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    // Add a label + field row to the form card
    private void addRow(JPanel p, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        p.add(field, gbc);
    }

    // Handle the "Update Password" button — validate and save the new password
    private void doChange() {
        String oldPass  = new String(oldPassField.getPassword()).trim();
        String newPass  = new String(newPassField.getPassword()).trim();
        String confirm  = new String(confirmField.getPassword()).trim();

        if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!newPass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (newPass.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (userDAO.changePassword(user.getId(), oldPass, newPass)) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            oldPassField.setText(""); newPassField.setText(""); confirmField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Current password is incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
