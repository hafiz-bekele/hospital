package ui.admin;

import models.User;
import ui.LoginFrame;
import ui.common.ChangePasswordPanel;
import ui.common.NotificationsPanel;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private User admin;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getFullName());
        setSize(1100, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout());

        // ── Header ──────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(80, 0, 120));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel welcome = new JLabel("Admin: " + admin.getFullName() + "  |  Administrator Portal");
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

        StatisticsPanel          statsPanel    = new StatisticsPanel();
        ManageAppointmentsPanel  apptPanel     = new ManageAppointmentsPanel();
        ViewPatientsPanel        patientsPanel = new ViewPatientsPanel();
        NotificationsPanel       notifPanel    = new NotificationsPanel(admin);

        tabs.addTab("📊  Dashboard",          statsPanel);
        tabs.addTab("👨‍⚕️  Manage Doctors",     new ManageDoctorsPanel());
        tabs.addTab("📅  Manage Schedules",    new ManageSchedulesPanel());
        tabs.addTab("📋  Appointments",        apptPanel);
        tabs.addTab("🔍  Search",              new SearchAppointmentsPanel());
        tabs.addTab("📈  Reports",             new ReportsPanel());
        tabs.addTab("🧑‍🤝‍🧑  Patients",           patientsPanel);
        tabs.addTab("🔔  Notifications",       notifPanel);
        tabs.addTab("🔑  Change Password",     new ChangePasswordPanel(admin));

        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i == 0) statsPanel.refresh();
            if (i == 3) apptPanel.refresh();
            if (i == 6) patientsPanel.refresh();
            if (i == 7) { notifPanel.refresh(); updateNotifBadge(tabs, notifPanel, 7); }
            updateNotifBadge(tabs, notifPanel, 7);
        });
        updateNotifBadge(tabs, notifPanel, 7);

        main.add(header, BorderLayout.NORTH);
        main.add(tabs,   BorderLayout.CENTER);
        add(main);
    }

    private void updateNotifBadge(JTabbedPane tabs, NotificationsPanel notifPanel, int tabIndex) {
        int unread = notifPanel.getUnreadCount();
        tabs.setTitleAt(tabIndex, unread > 0 ? "🔔  Notifications (" + unread + ")" : "🔔  Notifications");
    }
}
