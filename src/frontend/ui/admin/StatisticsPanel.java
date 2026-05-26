package frontend.ui.admin;

import backend.dao.AppointmentDAO;
import backend.dao.DoctorDAO;
import backend.dao.UserDAO;

import javax.swing.*;
import java.awt.*;

public class StatisticsPanel extends JPanel {

    private JLabel totalPatientsLbl, totalDoctorsLbl, totalAppointmentsLbl;
    private JLabel pendingLbl, approvedLbl, rejectedLbl;

    private UserDAO userDAO = new UserDAO();
    private DoctorDAO doctorDAO = new DoctorDAO();
    private AppointmentDAO appointmentDAO = new AppointmentDAO();

    public StatisticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 255));
        buildUI();
        refresh();
    }

    // Build the statistics dashboard with 6 stat cards
    private void buildUI() {
        JLabel title = new JLabel("Hospital Statistics Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(80, 0, 120));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel cards = new JPanel(new GridLayout(2, 3, 20, 20));
        cards.setBackground(new Color(245, 245, 255));

        totalPatientsLbl   = new JLabel("0", SwingConstants.CENTER);
        totalDoctorsLbl    = new JLabel("0", SwingConstants.CENTER);
        totalAppointmentsLbl = new JLabel("0", SwingConstants.CENTER);
        pendingLbl         = new JLabel("0", SwingConstants.CENTER);
        approvedLbl        = new JLabel("0", SwingConstants.CENTER);
        rejectedLbl        = new JLabel("0", SwingConstants.CENTER);

        cards.add(createCard("Total Patients", totalPatientsLbl, new Color(30, 100, 180)));
        cards.add(createCard("Total Doctors", totalDoctorsLbl, new Color(0, 120, 120)));
        cards.add(createCard("Total Appointments", totalAppointmentsLbl, new Color(80, 0, 120)));
        cards.add(createCard("Pending", pendingLbl, new Color(200, 150, 0)));
        cards.add(createCard("Approved", approvedLbl, new Color(60, 160, 60)));
        cards.add(createCard("Rejected", rejectedLbl, new Color(200, 50, 50)));

        JButton refreshBtn = new JButton("Refresh Statistics");
        refreshBtn.setBackground(new Color(70, 70, 70));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 13));
        refreshBtn.setFocusPainted(false);
        refreshBtn.setOpaque(true);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setPreferredSize(new Dimension(180, 34));
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refresh());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(new Color(245, 245, 255));
        btnPanel.add(refreshBtn);

        add(title, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    // Create a colored stat card with a label and a big number
    private JPanel createCard(String label, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        card.setPreferredSize(new Dimension(180, 120));

        JLabel lbl = new JLabel(label, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));

        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));

        card.add(lbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // Reload all counts from DB and update the stat cards
    public void refresh() {
        totalPatientsLbl.setText(String.valueOf(userDAO.getTotalPatients()));
        totalDoctorsLbl.setText(String.valueOf(doctorDAO.getTotalDoctors()));
        totalAppointmentsLbl.setText(String.valueOf(appointmentDAO.getTotalAppointments()));
        pendingLbl.setText(String.valueOf(appointmentDAO.countByStatus("PENDING")));
        approvedLbl.setText(String.valueOf(appointmentDAO.countByStatus("APPROVED")));
        rejectedLbl.setText(String.valueOf(appointmentDAO.countByStatus("REJECTED")));
    }
}
