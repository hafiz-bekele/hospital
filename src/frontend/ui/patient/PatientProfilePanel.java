package frontend.ui.patient;

import backend.models.User;

import javax.swing.*;
import java.awt.*;

public class PatientProfilePanel extends JPanel {

    public PatientProfilePanel(User patient) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 245, 255));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 100, 30, 100),
            BorderFactory.createLineBorder(new Color(30, 100, 180), 2, true)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 20, 10, 20);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel avatar = new JLabel(initials(patient.getFullName()), SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 32));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(30, 100, 180));
        avatar.setPreferredSize(new Dimension(80, 80));

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.anchor = GridBagConstraints.CENTER;
        card.add(avatar, g);
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;

        addRow(card, g, 1, "Full Name:", patient.getFullName());
        addRow(card, g, 2, "Username:",  patient.getUsername());
        addRow(card, g, 3, "Email:",     patient.getEmail() != null ? patient.getEmail() : "—");
        addRow(card, g, 4, "Phone:",     patient.getPhone() != null ? patient.getPhone() : "—");
        addRow(card, g, 5, "Role:",      "Patient");

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(240, 245, 255));
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, String value) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 50, 80));
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        p.add(val, g);
    }

    private String initials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) if (!p.isEmpty()) sb.append(p.charAt(0));
        return sb.toString().toUpperCase().substring(0, Math.min(2, sb.length()));
    }
}
