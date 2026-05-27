package frontend.ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class HomeFrame extends JFrame {

    private static final Color BG          = new Color(241, 245, 249);
    private static final Color PRIMARY     = new Color(37, 99, 235);
    private static final Color CARD        = Color.WHITE;
    private static final Color TEXT        = new Color(15, 23, 42);
    private static final Color MUTED       = new Color(100, 116, 139);
    private static final Color BORDER_CLR  = new Color(203, 213, 225);
    private static final Color ACCENT      = new Color(22, 163, 74);
    private static final Color HEADER_BG   = new Color(29, 78, 216);

    public HomeFrame() {
        setTitle("Kombolcha City Hospital");
        setSize(900, 620);
        setMinimumSize(new Dimension(800, 560));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildBody(),   BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        add(root);
    }

    // ── Top header bar ────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel icon = new JLabel("🏥");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icon.setForeground(Color.WHITE);

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel name = new JLabel("Kombolcha City Hospital");
        name.setFont(new Font("Segoe UI", Font.BOLD, 18));
        name.setForeground(Color.WHITE);

        JLabel tagline = new JLabel("Caring for your health, every day");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tagline.setForeground(new Color(186, 230, 253));

        namePanel.add(name);
        namePanel.add(tagline);

        left.add(icon);
        left.add(namePanel);

        // Right side buttons
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        right.setOpaque(false);

        JButton loginBtn = headerButton("Login");
        JButton registerBtn = headerButton("Register");

        loginBtn.addActionListener(e -> openLogin());
        registerBtn.addActionListener(e -> openRegister());

        right.add(loginBtn);
        right.add(registerBtn);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ── Main body ─────────────────────────────────────────────────

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(30, 40, 20, 40));

        // Hero section
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(BG);
        hero.setBorder(new EmptyBorder(10, 0, 30, 0));

        JLabel heroTitle = new JLabel("Welcome to Kombolcha City Hospital");
        heroTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heroTitle.setForeground(TEXT);
        heroTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heroSub = new JLabel("Your trusted partner in healthcare management");
        heroSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        heroSub.setForeground(MUTED);
        heroSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Big action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setOpaque(false);

        JButton bigLogin    = bigButton("🔐  Sign In",       PRIMARY,     Color.WHITE);
        JButton bigRegister = bigButton("📝  Create Account", ACCENT, Color.WHITE);

        bigLogin.addActionListener(e -> openLogin());
        bigRegister.addActionListener(e -> openRegister());

        btnPanel.add(bigLogin);
        btnPanel.add(bigRegister);

        hero.add(heroTitle);
        hero.add(Box.createVerticalStrut(8));
        hero.add(heroSub);
        hero.add(Box.createVerticalStrut(24));
        hero.add(btnPanel);

        // Feature cards row
        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);

        cards.add(featureCard("👨‍⚕️", "Expert Doctors",
                "Browse our team of qualified specialists across all medical departments."));
        cards.add(featureCard("📅", "Easy Appointments",
                "Book, track and manage your appointments online with ease."));
        cards.add(featureCard("📋", "Medical Records",
                "Access your medical notes and appointment history anytime."));

        body.add(hero,  BorderLayout.NORTH);
        body.add(cards, BorderLayout.CENTER);

        return body;
    }

    // ── Footer ────────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(226, 232, 240));
        footer.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel text = new JLabel("© 2025 Kombolcha City Hospital  •  All rights reserved");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        text.setForeground(MUTED);

        footer.add(text);
        return footer;
    }

    // ── Helpers ───────────────────────────────────────────────────

    private JPanel featureCard(String emoji, String title, String desc) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel ico = new JLabel(emoji, SwingConstants.CENTER);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        ico.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ttl = new JLabel(title, SwingConstants.CENTER);
        ttl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ttl.setForeground(TEXT);
        ttl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea dsc = new JTextArea(desc);
        dsc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dsc.setForeground(MUTED);
        dsc.setBackground(CARD);
        dsc.setLineWrap(true);
        dsc.setWrapStyleWord(true);
        dsc.setEditable(false);
        dsc.setFocusable(false);
        dsc.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(ico);
        card.add(Box.createVerticalStrut(10));
        card.add(ttl);
        card.add(Box.createVerticalStrut(8));
        card.add(dsc);

        return card;
    }

    private JButton bigButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.darker() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 46));
        return btn;
    }

    private JButton headerButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 255, 255, 40));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(new LineBorder(new Color(255, 255, 255, 120), 1, true));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 34));
        return btn;
    }

    private void openLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }

    private void openRegister() {
        new RegisterFrame().setVisible(true);
        dispose();
    }
}
