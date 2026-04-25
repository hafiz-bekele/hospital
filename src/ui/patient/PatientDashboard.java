package ui.patient;

import dao.NotificationDAO;
import models.User;
import ui.LoginFrame;
import ui.common.ChangePasswordPanel;
import ui.common.NotificationsPanel;

import javax.swing.*;
import java.awt.*;

public class PatientDashboard extends JFrame {

    private User patient;
    private NotificationDAO notifDAO = new NotificationDAO();

    public PatientDashboard(User patient) {
        this.patient = patient;
        setTitle("Patient Dashboard - " + patient.getFullName());
        setSize(1050, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());

        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 100, 180));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel welcome = new JLabel("Welcome, " + patient.getFullName() + "  |  Patient Portal");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("Arial", Font.BOLD, 14));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        header.add(welcome, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        // ── Tabs ─────────────────────────────────────────────────
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));

        BookAppointmentPanel bookPanel   = new BookAppointmentPanel(patient);
        MyAppointmentsPanel  myApptPanel = new MyAppointmentsPanel(patient);
        NotificationsPanel   notifPanel  = new NotificationsPanel(patient);

        tabs.addTab("🏥  Book Appointment", bookPanel);
        tabs.addTab("📋  My Appointments",  myApptPanel);
        tabs.addTab("🔔  Notifications",    notifPanel);
        tabs.addTab("👤  My Profile",       new PatientProfilePanel(patient));
        tabs.addTab("🔑  Change Password",  new ChangePasswordPanel(patient));

        // Update notification badge and refresh data on tab switch
        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i == 1) myApptPanel.refresh();
            if (i == 2) notifPanel.refresh();
            updateNotifBadge(tabs, notifPanel, 2);
        });

        updateNotifBadge(tabs, notifPanel, 2);

        main.add(header, BorderLayout.NORTH);
        main.add(tabs,   BorderLayout.CENTER);
        add(main);
    }

    private void updateNotifBadge(JTabbedPane tabs, NotificationsPanel notifPanel, int tabIndex) {
        int unread = notifPanel.getUnreadCount();
        tabs.setTitleAt(tabIndex, unread > 0 ? "🔔  Notifications (" + unread + ")" : "🔔  Notifications");
    }
}
