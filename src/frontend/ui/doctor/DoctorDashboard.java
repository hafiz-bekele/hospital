package frontend.ui.doctor;

import backend.dao.DoctorDAO;
import backend.models.Doctor;
import backend.models.User;
import frontend.ui.LoginFrame;
import frontend.ui.common.ChangePasswordPanel;
import frontend.ui.common.NotificationsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DoctorDashboard extends JFrame {

    private User   user;
    private Doctor doctor;

    public DoctorDashboard(User user) {
        this.user   = user;
        this.doctor = new DoctorDAO().getDoctorByUserId(user.getId());
        setTitle("Doctor Portal — Dr. " + user.getFullName());
        setSize(1100, 720);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    // Create the doctor dashboard UI with header and tabs
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(241, 245, 249));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(),   BorderLayout.CENTER);
        add(root);
    }

    // Create the top header bar with doctor name and logout button
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(6, 95, 70));
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        JPanel accent = new JPanel();
        accent.setBackground(new Color(16, 185, 129));
        accent.setPreferredSize(new Dimension(6, 56));
        left.add(accent);

        JLabel icon = new JLabel("  🩺  ");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(Color.WHITE);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(new EmptyBorder(10, 8, 10, 0));

        JLabel name = new JLabel("Dr. " + user.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(Color.WHITE);

        JLabel role = new JLabel(doctor != null ? doctor.getSpecialization() : "Doctor");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(167, 243, 208));

        namePanel.add(name);
        namePanel.add(role);
        left.add(icon);
        left.add(namePanel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        right.setOpaque(false);
        JButton logout = makeBtn("⏻  Logout", new Color(220, 38, 38));
        logout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        right.add(logout);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // Create tabs for appointments, notifications, profile, and password
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBackground(new Color(248, 250, 252));

        if (doctor == null) {
            tabs.addTab("Appointments", new JLabel("Doctor profile not found. Contact admin.", SwingConstants.CENTER));
            return tabs;
        }

        AppointmentsPanel  apptPanel  = new AppointmentsPanel(doctor);
        NotificationsPanel notifPanel = new NotificationsPanel(user);

        tabs.addTab("📋   Appointments",    pad(apptPanel));
        tabs.addTab("🔔   Notifications",   pad(notifPanel));
        tabs.addTab("👤   My Profile",      pad(new DoctorProfilePanel(user, doctor)));
        tabs.addTab("🔑   Change Password", pad(new ChangePasswordPanel(user)));

        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i == 0) apptPanel.refresh();
            if (i == 1) { notifPanel.refresh(); updateBadge(tabs, notifPanel, 1); }
            updateBadge(tabs, notifPanel, 1);
        });
        updateBadge(tabs, notifPanel, 1);
        return tabs;
    }

    // Wrap a component in a padded panel
    private JPanel pad(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(16, 16, 16, 16));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    // Update notification badge count on tab title
    private void updateBadge(JTabbedPane tabs, NotificationsPanel np, int idx) {
        int n = np.getUnreadCount();
        tabs.setTitleAt(idx, n > 0 ? "🔔   Notifications (" + n + ")" : "🔔   Notifications");
    }

    // Create a styled button with custom background color
    private JButton makeBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setOpaque(true); btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        return btn;
    }
}
