package frontend.ui;

import backend.dao.UserDAO;
import backend.models.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import static frontend.ui.LoginFrame.*;

public class RegisterFrame extends JFrame {

    private JTextField     nameField, usernameField, emailField, phoneField;
    private JPasswordField passwordField, confirmField;
    private JLabel         usernameHint, passwordStrengthLbl, confirmHint;
    private JProgressBar   strengthBar;
    private UserDAO        userDAO = new UserDAO();

    public RegisterFrame() {
        setTitle("Patient Registration");
        setSize(480, 680);
        setMinimumSize(new Dimension(400, 580));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        buildUI();
    }

    // Create the registration form UI
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // ── Header bar ───────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SUCCESS);
        header.setBorder(new EmptyBorder(16, 24, 16, 24));
        JLabel title = new JLabel("🩺  Create Patient Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        // ── Scrollable form card ─────────────────────────────────
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(24, 32, 24, 32));

        // Fields
        nameField     = styledField();
        usernameField = styledField();
        emailField    = styledField();
        phoneField    = styledField();
        passwordField = styledPassword();
        confirmField  = styledPassword();

        // Hints
        usernameHint       = hintLabel("Must start with @ and be at least 3 characters");
        passwordStrengthLbl = hintLabel("Enter a strong password");
        confirmHint        = hintLabel(" ");

        // Strength bar
        strengthBar = new JProgressBar(0, 5);
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        strengthBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        strengthBar.setBorderPainted(false);
        strengthBar.setStringPainted(false);
        strengthBar.setBackground(new Color(226, 232, 240));

        // Live listeners
        usernameField.getDocument().addDocumentListener(docListener(this::validateUsername));
        passwordField.getDocument().addDocumentListener(docListener(() -> { updateStrengthBar(); validateConfirm(); }));
        confirmField.getDocument().addDocumentListener(docListener(this::validateConfirm));

        // Buttons
        JButton registerBtn = roundButton("Create Account", SUCCESS, Color.WHITE);
        JButton backBtn     = roundButton("Back to Login",  new Color(248,250,252), TEXT);
        backBtn.setBorder(new LineBorder(BORDER_CLR, 1, true));
        registerBtn.addActionListener(e -> doRegister());
        backBtn.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });

        // Assemble
        addRow(card, "Full Name",         nameField,     null);
        addRow(card, "Username",          usernameField, usernameHint);
        addRow(card, "Email",             emailField,    null);
        addRow(card, "Phone",             phoneField,    null);
        addRow(card, "Password",          passwordField, null);
        card.add(strengthBar);
        card.add(Box.createVerticalStrut(2));
        card.add(passwordStrengthLbl);
        card.add(Box.createVerticalStrut(14));
        addRow(card, "Confirm Password",  confirmField,  confirmHint);
        card.add(Box.createVerticalStrut(20));
        card.add(registerBtn);
        card.add(Box.createVerticalStrut(10));
        card.add(backBtn);

        JScrollPane scroll = new JScrollPane(card);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        root.add(header, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        add(root);
    }

    // ── Helpers ──────────────────────────────────────────────────

    // Add a form row (label + field + optional hint)
    private void addRow(JPanel card, String label, JComponent field, JLabel hint) {
        card.add(labelFor(label));
        card.add(Box.createVerticalStrut(4));
        card.add(field);
        if (hint != null) {
            card.add(Box.createVerticalStrut(2));
            card.add(hint);
        }
        card.add(Box.createVerticalStrut(14));
    }

    // Create a hint label (small, italic, muted text)
    private JLabel hintLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lbl.setForeground(MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // Create a document listener that runs a function when text changes
    private DocumentListener docListener(Runnable r) {
        return new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { r.run(); }
            public void removeUpdate(DocumentEvent e)  { r.run(); }
            public void changedUpdate(DocumentEvent e) { r.run(); }
        };
    }

    // ── Live validation ──────────────────────────────────────────

    // Check if username is valid (starts with @ and at least 3 chars)
    private void validateUsername() {
        String val = usernameField.getText().trim();
        if (val.isEmpty()) {
            usernameHint.setText("Must start with @ and be at least 3 characters");
            usernameHint.setForeground(MUTED);
        } else if (!val.startsWith("@")) {
            usernameHint.setText("✘  Must start with @  (e.g. @john)");
            usernameHint.setForeground(new Color(220, 38, 38));
        } else if (val.length() < 3) {
            usernameHint.setText("✘  Too short — try @john");
            usernameHint.setForeground(new Color(220, 38, 38));
        } else {
            usernameHint.setText("✔  Valid username");
            usernameHint.setForeground(SUCCESS);
        }
    }

    // Update password strength bar based on password complexity
    private void updateStrengthBar() {
        String pass = new String(passwordField.getPassword());
        if (pass.isEmpty()) {
            strengthBar.setValue(0);
            strengthBar.setForeground(BORDER_CLR);
            passwordStrengthLbl.setText("Enter a strong password");
            passwordStrengthLbl.setForeground(MUTED);
            return;
        }
        int score = passwordScore(pass);
        strengthBar.setValue(score);
        Color[] colors = { BORDER_CLR, new Color(220,38,38), new Color(234,88,12),
                           new Color(202,138,4), new Color(101,163,13), SUCCESS };
        String[] labels = { "", "✘  Very weak", "⚠  Weak", "⚠  Medium", "✔  Good", "✔  Strong" };
        strengthBar.setForeground(colors[score]);
        passwordStrengthLbl.setText(labels[score]);
        passwordStrengthLbl.setForeground(colors[score]);
    }

    // Check if password and confirm password match
    private void validateConfirm() {
        String pass    = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        if (confirm.isEmpty()) {
            confirmHint.setText(" ");
        } else if (pass.equals(confirm)) {
            confirmHint.setText("✔  Passwords match");
            confirmHint.setForeground(SUCCESS);
        } else {
            confirmHint.setText("✘  Passwords do not match");
            confirmHint.setForeground(new Color(220, 38, 38));
        }
    }

    // Calculate password strength score (0-5 based on complexity)
    private int passwordScore(String p) {
        int s = 0;
        if (p.length() >= 8)                                          s++;
        if (p.chars().anyMatch(Character::isUpperCase))               s++;
        if (p.chars().anyMatch(Character::isLowerCase))               s++;
        if (p.chars().anyMatch(Character::isDigit))                   s++;
        if (p.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0)) s++;
        return s;
    }

    // ── Register action ──────────────────────────────────────────

    // Handle register button click - validate and create new patient account
    private void doRegister() {
        String name     = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String phone    = phoneField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirm  = new String(confirmField.getPassword()).trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            showError("Full Name, Username, and Password are required."); return;
        }
        if (!username.startsWith("@") || username.length() < 3) {
            showError("Username must start with @ and be at least 3 characters.\nExample: @john"); return;
        }
        if (passwordScore(password) < 5) {
            showError("Password must have:\n• 8+ characters\n• Uppercase & lowercase\n• A number\n• A special character"); return;
        }
        if (!password.equals(confirm)) {
            showError("Passwords do not match."); return;
        }
        if (userDAO.usernameExists(username)) {
            showError("Username already taken. Choose another."); return;
        }

        User user = new User();
        user.setFullName(name);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole("PATIENT");

        if (userDAO.register(user)) {
            JOptionPane.showMessageDialog(this, "Account created! Please sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            showError("Registration failed. Please try again.");
        }
    }

    // Show error message dialog
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.WARNING_MESSAGE);
    }
}
