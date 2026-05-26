package frontend.ui.patient;

import backend.models.User;
import frontend.ui.LoginFrame;
import frontend.ui.common.ChangePasswordPanel;
import frontend.ui.common.NotificationsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PatientDashboard extends JFrame {

    private User patient;

    public PatientDashboard(User patient) {
        this.patient = patient;
        setTitle("Patient Portal — " + patient.getFullName());
        setSize(1100, 720);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    // Create the patient dashboard UI with header and tabs
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(241, 245, 249));
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildTabs(),   BorderLayout.CENTER);
        add(root);
    }

    // Create the top header bar with patient name and logout button
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 64, 175));
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        JPanel accent = new JPanel();
        accent.setBackground(new Color(59, 130, 246));
        accent.setPreferredSize(new Dimension(6, 56));
        left.add(accent);

        JLabel icon = new JLabel("  🏥  ");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(Color.WHITE);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);
        namePanel.setBorder(new EmptyBorder(10, 8, 10, 0));

        JLabel name = new JLabel(patient.getFullName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        name.setForeground(Color.WHITE);

        JLabel role = new JLabel("Patient");
        role.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        role.setForeground(new Color(147, 197, 253));

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

    // Create tabs for booking, appointments, notifications, profile, and password
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBackground(new Color(248, 250, 252));

        BookAppointmentPanel bookPanel   = new BookAppointmentPanel(patient);
        MyAppointmentsPanel  myApptPanel = new MyAppointmentsPanel(patient);
        NotificationsPanel   notifPanel  = new NotificationsPanel(patient);

        tabs.addTab("🏥   Book Appointment", pad(bookPanel));
        tabs.addTab("📋   My Appointments",  pad(myApptPanel));
        tabs.addTab("🔔   Notifications",    pad(notifPanel));
        tabs.addTab("👤   My Profile",       pad(new PatientProfilePanel(patient)));
        tabs.addTab("🔑   Change Password",  pad(new ChangePasswordPanel(patient)));

        tabs.addChangeListener(e -> {
            int i = tabs.getSelectedIndex();
            if (i == 1) myApptPanel.refresh();
            if (i == 2) { notifPanel.refresh(); updateBadge(tabs, notifPanel, 2); }
            updateBadge(tabs, notifPanel, 2);
        });
        updateBadge(tabs, notifPanel, 2);
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
