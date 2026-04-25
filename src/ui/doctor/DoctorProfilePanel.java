package ui.doctor;

import models.Doctor;
import models.User;

import javax.swing.*;
import java.awt.*;

public class DoctorProfilePanel extends JPanel {

    private User user;
    private Doctor doctor;

    public DoctorProfilePanel(User user, Doctor doctor) {
        this.user = user;
        this.doctor = doctor;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 250, 250));
        buildUI();
    }

    private void buildUI() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 80, 30, 80),
            BorderFactory.createLineBorder(new Color(0, 120, 120), 2, true)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Avatar circle label
        JLabel avatar = new JLabel("Dr", SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 36));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0, 120, 120));
        avatar.setPreferredSize(new Dimension(80, 80));
        avatar.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 120), 3));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        card.add(avatar, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;

        addRow(card, gbc, 1, "Full Name:",       "Dr. " + user.getFullName());
        addRow(card, gbc, 2, "Username:",         user.getUsername());
        addRow(card, gbc, 3, "Email:",            user.getEmail() != null ? user.getEmail() : "—");
        addRow(card, gbc, 4, "Phone:",            user.getPhone() != null ? user.getPhone() : "—");
        addRow(card, gbc, 5, "Specialization:",   doctor.getSpecialization());
        addRow(card, gbc, 6, "Qualification:",    doctor.getQualification() != null ? doctor.getQualification() : "—");
        addRow(card, gbc, 7, "Experience:",       doctor.getExperienceYears() + " years");

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(240, 250, 250));
        wrapper.add(card);

        add(wrapper, BorderLayout.CENTER);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 60, 60));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(val, gbc);
    }
}
