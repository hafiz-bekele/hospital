package frontend.ui.admin;

import backend.models.User;
import frontend.ui.LoginFrame;
import frontend.ui.common.ChangePasswordPanel;
import frontend.ui.common.NotificationsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Admin Dashboard — main window after admin logs in.
 *
 * Layout:
 *   ┌─────────────────────────────────────────┐
 *   │              Header bar                 │
 *   ├──────────┬──────────────────────────────┤
 *   │          │                              │
 *   │ Sidebar  │       Content panel          │
 *   │  (nav)   │                              │
 *   │          │                              │
 *   └──────────┴──────────────────────────────┘
 *
 * The sidebar replaces JTabbedPane.LEFT so emoji icons render
 * properly on Windows and the nav looks clean and modern.
 */
public class AdminDashboard extends JFrame {

    // ── Colour palette ────────────────────────────────────────────
    private static final Color SIDEBAR_BG      = new Color(45,  27,  78);   // deep purple
    private static final Color SIDEBAR_HOVER   = new Color(88,  28, 135);   // lighter purple
    private static final Color SIDEBAR_ACTIVE  = new Color(124, 58, 237);   // vivid purple
    private static final Color SIDEBAR_TEXT    = new Color(220, 210, 255);  // soft lavender
    private static final Color SIDEBAR_ACTIVE_TEXT = Color.WHITE;
    private static final Color HEADER_BG       = new Color(88,  28, 135);
    private static final Color CONTENT_BG      = new Color(241, 245, 249);

    // ── Nav item definitions: { label, icon } ────────────────────
    private static final String[][] NAV_ITEMS = {
        { "Dashboard",       "📊" },
        { "Manage Doctors",  "👨‍⚕️" },
        { "Schedules",       "📅" },
        { "Appointments",    "📋" },
        { "Search",          "🔍" },
        { "Reports",         "📈" },
        { "Patients",        "👥" },
        { "Notifications",   "🔔" },
        { "Change Password", "🔑" },
    };

    private User admin;

    // Keep references to panels that need refreshing
    private StatisticsPanel         statsPanel;
    private ManageAppointmentsPanel apptPanel;
    private ViewPatientsPanel       patientsPanel;
    private NotificationsPanel      notifPanel;

    // Sidebar nav buttons (one per tab)
    private JButton[] navButtons;

    // The card layout swaps content panels
    private JPanel     contentArea;
    private CardLayout cardLayout;

    // Track which nav item is active
    private int activeIndex = 0;

    public AdminDashboard(User admin) {
        this.admin = admin;
        setTitle("Admin Portal — " + admin.getFullName());
        setSize(1200, 760);
        setMinimumSize(new Dimension(900, 600));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildUI();
    }

    // ── Main layout ───────────────────────────────────────────────

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);

        add(root);
    }

    // ── Header bar ────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 58));

        // Left: accent stripe + icon + name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);

        // Thin accent stripe on the very left edge
        JPanel stripe = new JPanel();
        stripe.setBackground(SIDEBAR_ACTIVE);
        stripe.setPreferredSize(new Dimension(5, 58));
        left.add(stripe);

        JLabel icon = new JLabel("  🛡  ");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(Color.WHITE);

        JPanel nameBlock = new JPanel();
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        nameBlock.setOpaque(false);
        nameBlock.setBorder(new EmptyBorder(11, 6, 11, 0));

        JLabel nameLabel = new JLabel(admin.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);

        JLabel roleLabel = new JLabel("Administrator");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(196, 181, 253));

        nameBlock.add(nameLabel);
        nameBlock.add(roleLabel);

        left.add(icon);
        left.add(nameBlock);

        // Right: logout button
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 12));
        right.setOpaque(false);
        JButton logoutBtn = makeHeaderButton("⏻  Logout", new Color(220, 38, 38));
        logoutBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        right.add(logoutBtn);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ── Sidebar navigation ────────────────────────────────────────

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new MatteBorder(0, 0, 0, 1, new Color(70, 40, 110)));

        // Small top spacer
        sidebar.add(Box.createVerticalStrut(12));

        navButtons = new JButton[NAV_ITEMS.length];

        for (int i = 0; i < NAV_ITEMS.length; i++) {
            JButton btn = makeNavButton(NAV_ITEMS[i][0], NAV_ITEMS[i][1], i);
            navButtons[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        // Push everything to the top
        sidebar.add(Box.createVerticalGlue());

        // Mark first item active
        setActive(0);

        return sidebar;
    }

    /**
     * Creates a single sidebar nav button.
     * Uses plain text + a separate emoji label so the font renders correctly
     * on Windows regardless of system font support.
     */
    private JButton makeNavButton(String label, String emoji, int index) {
        // We build the button with a custom layout so the icon and text
        // are separate components — this avoids broken emoji rendering.
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                // Draw background
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Active indicator bar on the left edge
                if (index == activeIndex) {
                    g2.setColor(SIDEBAR_ACTIVE);
                    g2.fillRect(0, 0, 4, getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 0));
        btn.setOpaque(true);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);   // we paint manually above
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(200, 44));
        btn.setPreferredSize(new Dimension(200, 44));

        // Emoji icon label (separate font so it renders on Windows)
        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        iconLabel.setForeground(SIDEBAR_TEXT);
        iconLabel.setOpaque(false);

        // Text label
        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textLabel.setForeground(SIDEBAR_TEXT);
        textLabel.setOpaque(false);

        btn.add(iconLabel);
        btn.add(textLabel);

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (index != activeIndex) {
                    btn.setBackground(SIDEBAR_HOVER);
                    btn.repaint();
                }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (index != activeIndex) {
                    btn.setBackground(SIDEBAR_BG);
                    btn.repaint();
                }
            }
        });

        // Click: switch panel
        btn.addActionListener(e -> switchTo(index));

        return btn;
    }

    /** Highlights the active nav button and dims all others. */
    private void setActive(int index) {
        activeIndex = index;
        for (int i = 0; i < navButtons.length; i++) {
            JButton btn = navButtons[i];
            boolean active = (i == index);
            btn.setBackground(active ? SIDEBAR_HOVER : SIDEBAR_BG);

            // Update child label colours
            for (Component child : btn.getComponents()) {
                if (child instanceof JLabel lbl) {
                    lbl.setForeground(active ? SIDEBAR_ACTIVE_TEXT : SIDEBAR_TEXT);
                    if (active) lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                    else        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN));
                }
            }
            btn.repaint();
        }
    }

    // ── Content area (CardLayout) ─────────────────────────────────

    private JPanel buildContent() {
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(CONTENT_BG);

        // Build all panels once
        statsPanel    = new StatisticsPanel();
        apptPanel     = new ManageAppointmentsPanel();
        patientsPanel = new ViewPatientsPanel();
        notifPanel    = new NotificationsPanel(admin);

        contentArea.add(wrap(statsPanel),                    "Dashboard");
        contentArea.add(wrap(new ManageDoctorsPanel()),      "Manage Doctors");
        contentArea.add(wrap(new ManageSchedulesPanel()),    "Schedules");
        contentArea.add(wrap(apptPanel),                     "Appointments");
        contentArea.add(wrap(new SearchAppointmentsPanel()), "Search");
        contentArea.add(wrap(new ReportsPanel()),            "Reports");
        contentArea.add(wrap(patientsPanel),                 "Patients");
        contentArea.add(wrap(notifPanel),                    "Notifications");
        contentArea.add(wrap(new ChangePasswordPanel(admin)),"Change Password");

        // Show dashboard first
        cardLayout.show(contentArea, "Dashboard");
        updateNotifBadge();

        return contentArea;
    }

    /** Wraps a panel with consistent padding and white background. */
    private JPanel wrap(JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        p.add(c, BorderLayout.CENTER);
        return p;
    }

    /** Switches the content area to the panel at the given index. */
    private void switchTo(int index) {
        setActive(index);
        String name = NAV_ITEMS[index][0];
        cardLayout.show(contentArea, name);

        // Refresh panels that need live data
        switch (index) {
            case 0 -> statsPanel.refresh();
            case 3 -> apptPanel.refresh();
            case 6 -> patientsPanel.refresh();
            case 7 -> { notifPanel.refresh(); updateNotifBadge(); }
        }
        updateNotifBadge();
    }

    /** Updates the Notifications nav button label with unread count badge. */
    private void updateNotifBadge() {
        int unread = notifPanel.getUnreadCount();
        int notifIndex = 7;  // "Notifications" is at index 7

        // Find the text label inside the nav button and update it
        JButton btn = navButtons[notifIndex];
        for (Component child : btn.getComponents()) {
            if (child instanceof JLabel lbl && !lbl.getText().startsWith("🔔")) {
                // This is the text label (not the emoji icon)
                lbl.setText(unread > 0 ? "Notifications (" + unread + ")" : "Notifications");
                btn.revalidate();
                btn.repaint();
                break;
            }
        }
    }

    // ── Shared button factory ─────────────────────────────────────

    private JButton makeHeaderButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        return btn;
    }
}
