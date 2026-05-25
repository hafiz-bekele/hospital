package frontend.ui;

import backend.dao.UserDAO;
import backend.models.User;
import frontend.ui.admin.AdminDashboard;
import frontend.ui.doctor.DoctorDashboard;
import frontend.ui.patient.PatientDashboard;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {

    public static final Color PRIMARY      = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(29, 78, 216);
    public static final Color SUCCESS      = new Color(22, 163, 74);
    public static final Color BG           = new Color(241, 245, 249);
    public static final Color CARD         = Color.WHITE;
    public static final Color TEXT         = new Color(15, 23, 42);
    public static final Color MUTED        = new Color(100, 116, 139);
    public static final Color BORDER_CLR   = new Color(203, 213, 225);

    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private UserDAO        userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("Hospital System — Login");
        setSize(440, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(36, 36, 36, 36)
        ));

        JLabel icon = new JLabel("🏥", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Hospital System", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = styledField();
        passwordField = styledPassword();

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 38, 38));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginBtn    = roundButton("Sign In",       PRIMARY, Color.WHITE);
        JButton registerBtn = roundButton("Create Account", new Color(248, 250, 252), TEXT);
        registerBtn.setBorder(new LineBorder(BORDER_CLR, 1, true));

        loginBtn.addActionListener(e -> doLogin());
        registerBtn.addActionListener(e -> { new RegisterFrame().setVisible(true); dispose(); });
        passwordField.addActionListener(e -> doLogin());

        JLabel hint = new JLabel("Default admin: admin / admin123", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(icon);
        card.add(Box.createVerticalStrut(8));
        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));
        card.add(labelFor("Username"));
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(labelFor("Password"));
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(8));
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(loginBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(20));
        card.add(hint);

        root.add(card);
        add(root);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (username.isEmpty() || password.isEmpty()) { errorLabel.setText("Please enter username and password."); return; }
        errorLabel.setText(" ");
        User user = userDAO.login(username, password);
        if (user == null) { errorLabel.setText("Invalid username or password."); passwordField.setText(""); return; }
        dispose();
        switch (user.getRole()) {
            case "ADMIN":   new AdminDashboard(user).setVisible(true);   break;
            case "DOCTOR":  new DoctorDashboard(user).setVisible(true);  break;
            case "PATIENT": new PatientDashboard(user).setVisible(true); break;
        }
    }

    public static JLabel labelFor(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(51, 65, 85));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    public static JTextField styledField() {
        JTextField f = new JTextField();
        applyFieldStyle(f);
        return f;
    }

    public static JPasswordField styledPassword() {
        JPasswordField f = new JPasswordField();
        applyFieldStyle(f);
        return f;
    }

    private static void applyFieldStyle(JTextField f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setForeground(TEXT); f.setBackground(new Color(248, 250, 252));
        f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(8, 12, 8, 12)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(PRIMARY, 2, true), new EmptyBorder(7, 11, 7, 11)));
            }
            public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(new LineBorder(BORDER_CLR, 1, true), new EmptyBorder(8, 12, 8, 12)));
            }
        });
    }

    public static JButton roundButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg); btn.setBackground(bg);
        btn.setOpaque(false); btn.setContentAreaFilled(false);
        btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }
}
