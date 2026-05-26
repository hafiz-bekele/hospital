package frontend.ui.doctor;

import backend.models.Doctor;
import backend.models.User;

import javax.swing.*;
import java.awt.*;

public class DoctorProfilePanel extends JPanel {

    public DoctorProfilePanel(User user, Doctor doctor) {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 250, 250));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(30, 80, 30, 80),
            BorderFactory.createLineBorder(new Color(0, 120, 120), 2, true)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 20, 10, 20);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel avatar = new JLabel("Dr", SwingConstants.CENTER);
        avatar.setFont(new Font("Arial", Font.BOLD, 36));
        avatar.setForeground(Color.WHITE);
        avatar.setOpaque(true);
        avatar.setBackground(new Color(0, 120, 120));
        avatar.setPreferredSize(new Dimension(80, 80));

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2; g.anchor = GridBagConstraints.CENTER;
        card.add(avatar, g);
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;

        addRow(card, g, 1, "Full Name:",     "Dr. " + user.getFullName());
        addRow(card, g, 2, "Username:",      user.getUsername());
        addRow(card, g, 3, "Email:",         user.getEmail()  != null ? user.getEmail()  : "—");
        addRow(card, g, 4, "Phone:",         user.getPhone()  != null ? user.getPhone()  : "—");
        addRow(card, g, 5, "Specialization:", doctor.getSpecialization());
        addRow(card, g, 6, "Qualification:", doctor.getQualification() != null ? doctor.getQualification() : "—");
        addRow(card, g, 7, "Experience:",    doctor.getExperienceYears() + " years");

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(240, 250, 250));
        wrapper.add(card);
        add(wrapper, BorderLayout.CENTER);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, String value) {
        g.gridx = 0; g.gridy = row; g.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 60, 60));
        p.add(lbl, g);
        g.gridx = 1; g.weightx = 1;
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        p.add(val, g);
    }
}
