package ui.doctor;

import dao.DoctorDAO;
import models.Doctor;
import models.User;
import ui.LoginFrame;
import ui.common.ChangePasswordPanel;
import ui.common.NotificationsPanel;

import javax.swing.*;
import java.awt.*;

public class DoctorDashboard extends JFrame {

    private User user;
    private Doctor doctor;

    public DoctorDashboard(User user) {
        this.user = user;
        this.doctor = new DoctorDAO().getDoctorByUserId(user.getId());
        setTitle("Doctor Dashboard - Dr. " + user.getFullName());
        setSize(1050, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());

        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 120, 120));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        String spec = doctor != null ? "  |  " + doctor.getSpecialization() : "";
        JLabel welcome = new JLabel("Dr. " + user.getFullName() + spec + "  |  Doctor Portal");
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

        if (doctor != null) {
            AppointmentsPanel apptPanel  = new AppointmentsPanel(doctor);
            NotificationsPanel notifPanel = new NotificationsPanel(user);

            tabs.addTab("📋  Appointments",   apptPanel);
            tabs.addTab("🔔  Notifications",  notifPanel);
            tabs.addTab("👤  My Profile",     new DoctorProfilePanel(user, doctor));
            tabs.addTab("🔑  Change Password", new ChangePasswordPanel(user));

            tabs.addChangeListener(e -> {
                int i = tabs.getSelectedIndex();
                if (i == 0) apptPanel.refresh();
                if (i == 1) { notifPanel.refresh(); updateNotifBadge(tabs, notifPanel, 1); }
                updateNotifBadge(tabs, notifPanel, 1);
            });
            updateNotifBadge(tabs, notifPanel, 1);
        } else {
            tabs.addTab("Appointments", new JLabel("Doctor profile not found. Contact admin.", SwingConstants.CENTER));
        }

        main.add(header, BorderLayout.NORTH);
        main.add(tabs,   BorderLayout.CENTER);
        add(main);
    }

    private void updateNotifBadge(JTabbedPane tabs, NotificationsPanel notifPanel, int tabIndex) {
        int unread = notifPanel.getUnreadCount();
        tabs.setTitleAt(tabIndex, unread > 0 ? "🔔  Notifications (" + unread + ")" : "🔔  Notifications");
    }
}
