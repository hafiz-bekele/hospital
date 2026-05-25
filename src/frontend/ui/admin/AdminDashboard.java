package frontend.ui.admin;

import backend.models.User;
import frontend.ui.LoginFrame;
import frontend.ui.common.ChangePasswordPanel;
import frontend.ui.common.NotificationsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private User admin;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("Admin Portal — " + admin.getFullName());
        setSize(1150, 740);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(241, 245, 249));

        // ── Header ──────────────────────────────────────────────
        root.add(buildHeader(), BorderLayout.NORTH);

        // ── Tabs ─────────────────────────────────────────────────
        JTabbedPane tabs = buildTabs();
        root.add(tabs, BorderLayout.CENTER);

        add(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(88, 28, 135));   // deep purple
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Left: icon + name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        JPanel accent = new JPanel();
        accent.setBackground(new Color(124, 58, 237));
        accent.setPreferredSize(new Dimension(6, 56));
        left.add(accent);

        JLabel icon = new JLabel("  🛡  ");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(Color.WHITE);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(new EmptyBorder(10, 8, 10, 0));

        JLabel name = new JLabel(admin.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(Color.WHITE);

        JLabel role = new JLabel("Administrator");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(196, 181, 253));

        namePanel.add(name);
        namePanel.add(role);
        left.add(icon);
        left.add(namePanel);

        // Right: logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        right.setOpaque(false);
        JButton logout = headerButton("⏻  Logout", new Color(220, 38, 38));
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        right.add(logout);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBackground(new Color(248, 250, 252));
        tabs.setForeground(new Color(51, 65, 85));

        StatisticsPanel         statsPanel    = new StatisticsPanel();
        ManageAppointmentsPanel apptPanel     = new ManageAppointmentsPanel();
        ViewPatientsPanel       patientsPanel = new ViewPatientsPanel();
        NotificationsPanel      notifPanel    = new NotificationsPanel(admin);

        tabs.addTab("📊   Dashboard",         pad(statsPanel));
        tabs.addTab("👨‍⚕️   Doctors",            pad(new ManageDoctorsPanel()));
        tabs.addTab("📅   Schedules",          pad(new ManageSchedulesPanel()));
        tabs.addTab("📋   Appointments",       pad(apptPanel));
        tabs.addTab("🔍   Search",             pad(new SearchAppointmentsPanel()));
        tabs.addTab("📈   Reports",            pad(new ReportsPanel()));
        tabs.addTab("🧑‍🤝‍🧑   Patients",           pad(patientsPanel));
        tabs.addTab("🔔   Notifications",      pad(notifPanel));
        tabs.addTab("🔑   Change Password",    pad(new ChangePasswordPanel(admin)));

        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i == 0) statsPanel.refresh();
            if (i == 3) apptPanel.refresh();
            if (i == 6) patientsPanel.refresh();
            if (i == 7) { notifPanel.refresh(); updateBadge(tabs, notifPanel, 7); }
            updateBadge(tabs, notifPanel, 7);
        });
        updateBadge(tabs, notifPanel, 7);
        return tabs;
    }

    private JPanel pad(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    private void updateBadge(JTabbedPane tabs, NotificationsPanel np, int idx) {
        int n = np.getUnreadCount();
        tabs.setTitleAt(idx, n > 0 ? "🔔   Notifications (" + n + ")" : "🔔   Notifications");
    }

    private JButton headerButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }
}
