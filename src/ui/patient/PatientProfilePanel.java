package ui.patient;

import models.User;

import javax.swing.*;
import java.awt.*;

public class PatientProfilePanel extends JPanel {

    private User patient;

    public PatientProfilePanel(User patient) {
        this.patient = patient;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 255));
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 100, 30, 100),
            BorderFactory.createLineBorder(new Color(30, 100, 180), 2, true)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Avatar
        JLabel avatar = new JLabel(getInitials(patient.getFullName()), SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 32));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(30, 100, 180));
        avatar.setPreferredSize(new Dimension(80, 80));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        card.add(avatar, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        addRow(card, gbc, 1, "Full Name:", patient.getFullName());
        addRow(card, gbc, 2, "Username:",  patient.getUsername());
        addRow(card, gbc, 3, "Email:",     patient.getEmail()  != null ? patient.getEmail()  : "—");
        addRow(card, gbc, 4, "Phone:",     patient.getPhone()  != null ? patient.getPhone()  : "—");
        addRow(card, gbc, 5, "Role:",      "Patient");

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(240, 245, 255));
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 50, 80));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(val, gbc);
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) if (!p.isEmpty()) sb.append(p.charAt(0));
        return sb.toString().toUpperCase().substring(0, Math.min(2, sb.length()));
    }
}
