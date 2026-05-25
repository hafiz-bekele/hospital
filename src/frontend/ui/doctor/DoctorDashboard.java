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
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(241, 245, 249));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(),   BorderLayout.CENTER);
        add(root);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(6, 95, 70));    // deep teal
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

        String spec = doctor != null ? doctor.getSpecialization() : "Doctor";
        JLabel name = new JLabel("Dr. " + user.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(Color.WHITE);

        JLabel role = new JLabel(spec);
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(167, 243, 208));

        namePanel.add(name);
        namePanel.add(role);
        left.add(icon);
        left.add(namePanel);

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

        if (doctor != null) {
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
        } else {
            JLabel err = new JLabel("Doctor profile not found. Contact admin.", SwingConstants.CENTER);
            err.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            err.setForeground(new Color(100, 116, 139));
            tabs.addTab("Appointments", err);
        }
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
