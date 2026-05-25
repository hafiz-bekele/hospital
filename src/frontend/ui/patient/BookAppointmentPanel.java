package frontend.ui.patient;

import backend.dao.AppointmentDAO;
import backend.dao.DoctorDAO;
import backend.dao.ScheduleDAO;
import backend.models.Appointment;
import backend.models.Doctor;
import backend.models.Schedule;
import backend.models.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BookAppointmentPanel extends JPanel {

    // ── Palette (matches LoginFrame) ─────────────────────────────
    private static final Color PRIMARY    = new Color(37, 99, 235);
    private static final Color BG         = new Color(248, 250, 252);
    private static final Color CARD       = Color.WHITE;
    private static final Color BORDER_CLR = new Color(203, 213, 225);
    private static final Color TEXT       = new Color(15, 23, 42);
    private static final Color MUTED      = new Color(100, 116, 139);
    private static final Color SUCCESS    = new Color(22, 163, 74);
    private static final Color HIGHLIGHT  = new Color(239, 246, 255);

    private User    patient;
    private List<Doctor> allDoctors = new ArrayList<>();

    // Search
    private JTextField   searchField;
    private JLabel       resultCount;

    // Doctor table
    private JTable            doctorTable;
    private DefaultTableModel tableModel;

    // Selected doctor card
    private JLabel cardName, cardSpec, cardQual, cardExp;
    private JPanel doctorCard;

    // Booking form
    private JComboBox<Schedule> scheduleCombo;
    private JTextArea           reasonArea;
    private JLabel              bookMsg;

    private DoctorDAO      doctorDAO  = new DoctorDAO();
    private ScheduleDAO    scheduleDAO = new ScheduleDAO();
    private AppointmentDAO apptDAO    = new AppointmentDAO();

    public BookAppointmentPanel(User patient) {
        this.patient = patient;
        setLayout(new BorderLayout(0, 0));
        setBackground(BG);
        buildUI();
    }

    private void buildUI() {
        // ── LEFT: search + doctor list ───────────────────────────
        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setBackground(BG);
        left.setBorder(new EmptyBorder(0, 0, 0, 10));
        left.setPreferredSize(new Dimension(420, 0));

        left.add(buildSearchBar(),   BorderLayout.NORTH);
        left.add(buildDoctorTable(), BorderLayout.CENTER);

        // ── RIGHT: doctor card + booking form ────────────────────
        JPanel right = new JPanel(new BorderLayout(0, 10));
        right.setBackground(BG);

        right.add(buildDoctorCard(),  BorderLayout.NORTH);
        right.add(buildBookingForm(), BorderLayout.CENTER);

        // ── Split ────────────────────────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setDividerLocation(430);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setBackground(BG);

        add(split, BorderLayout.CENTER);

        loadDoctors();
    }

    // ── Search bar ───────────────────────────────────────────────

    private JPanel buildSearchBar() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(0, 0, 4, 0));

        // Search icon + field
        JPanel fieldWrap = new JPanel(new BorderLayout(6, 0));
        fieldWrap.setBackground(CARD);
        fieldWrap.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBorder(null);
        searchField.setBackground(CARD);
        searchField.setForeground(TEXT);

        // Placeholder
        searchField.setText("Search by name or specialization...");
        searchField.setForeground(MUTED);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (searchField.getForeground().equals(MUTED)) {
                    searchField.setText("");
                    searchField.setForeground(TEXT);
                }
                fieldWrap.setBorder(new CompoundBorder(
                    new LineBorder(PRIMARY, 2, true),
                    new EmptyBorder(5, 9, 5, 9)
                ));
            }
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search by name or specialization...");
                    searchField.setForeground(MUTED);
                }
                fieldWrap.setBorder(new CompoundBorder(
                    new LineBorder(BORDER_CLR, 1, true),
                    new EmptyBorder(6, 10, 6, 10)
                ));
            }
        });

        // Live filter on every keystroke
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterDoctors(); }
            public void removeUpdate(DocumentEvent e)  { filterDoctors(); }
            public void changedUpdate(DocumentEvent e) { filterDoctors(); }
        });

        fieldWrap.add(searchIcon,   BorderLayout.WEST);
        fieldWrap.add(searchField,  BorderLayout.CENTER);

        // Clear button
        JButton clearBtn = new JButton("✕");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearBtn.setForeground(MUTED);
        clearBtn.setBackground(BG);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            searchField.requestFocus();
        });

        resultCount = new JLabel("", SwingConstants.RIGHT);
        resultCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        resultCount.setForeground(MUTED);

        panel.add(fieldWrap,  BorderLayout.CENTER);
        panel.add(clearBtn,   BorderLayout.EAST);
        panel.add(resultCount, BorderLayout.SOUTH);
        return panel;
    }

    // ── Doctor table ─────────────────────────────────────────────

    private JScrollPane buildDoctorTable() {
        String[] cols = {"", "Doctor", "Specialization", "Exp"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        doctorTable = new JTable(tableModel);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorTable.setRowHeight(36);
        doctorTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        doctorTable.setBackground(CARD);
        doctorTable.setGridColor(new Color(241, 245, 249));
        doctorTable.setShowVerticalLines(false);
        doctorTable.setIntercellSpacing(new Dimension(0, 1));
        doctorTable.setSelectionBackground(HIGHLIGHT);
        doctorTable.setSelectionForeground(TEXT);

        // Header style
        doctorTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        doctorTable.getTableHeader().setBackground(new Color(241, 245, 249));
        doctorTable.getTableHeader().setForeground(MUTED);
        doctorTable.getTableHeader().setBorder(new MatteBorder(0, 0, 1, 0, BORDER_CLR));

        // Column widths
        doctorTable.getColumnModel().getColumn(0).setMaxWidth(36);   // avatar col
        doctorTable.getColumnModel().getColumn(0).setMinWidth(36);
        doctorTable.getColumnModel().getColumn(3).setMaxWidth(50);

        // Avatar cell renderer — shows a colored circle with initials
        doctorTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = new JLabel((String) v, SwingConstants.CENTER) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(sel ? PRIMARY : new Color(219, 234, 254));
                        g2.fillOval(4, 4, getWidth()-8, getHeight()-8);
                        g2.setColor(sel ? Color.WHITE : PRIMARY);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        FontMetrics fm = g2.getFontMetrics();
                        String txt = getText();
                        g2.drawString(txt,
                            (getWidth() - fm.stringWidth(txt)) / 2,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                        g2.dispose();
                    }
                };
                lbl.setOpaque(true);
                lbl.setBackground(sel ? HIGHLIGHT : CARD);
                return lbl;
            }
        });

        // Row click → populate doctor card + schedules
        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });

        JScrollPane scroll = new JScrollPane(doctorTable);
        scroll.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(0, 0, 0, 0)
        ));
        scroll.getViewport().setBackground(CARD);
        return scroll;
    }

    // ── Doctor info card ─────────────────────────────────────────

    private JPanel buildDoctorCard() {
        doctorCard = new JPanel(new GridBagLayout());
        doctorCard.setBackground(CARD);
        doctorCard.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(2, 4, 2, 4);

        // Avatar circle (large)
        JPanel avatar = new JPanel() {
            @Override protected void paintComponent(Graphics g2d) {
                super.paintComponent(g2d);
                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(219, 234, 254));
                g2.fillOval(0, 0, 52, 52);
                g2.setColor(PRIMARY);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                String txt = cardName != null && !cardName.getText().equals("Select a doctor") ?
                    getInitials(cardName.getText()) : "?";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(txt, (52 - fm.stringWidth(txt)) / 2,
                    (52 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(52, 52));
        avatar.setOpaque(false);

        cardName = new JLabel("Select a doctor from the list");
        cardName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cardName.setForeground(TEXT);

        cardSpec = new JLabel(" ");
        cardSpec.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardSpec.setForeground(PRIMARY);

        cardQual = new JLabel(" ");
        cardQual.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardQual.setForeground(MUTED);

        cardExp = new JLabel(" ");
        cardExp.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cardExp.setForeground(MUTED);

        // Layout: avatar on left, info on right
        g.gridx = 0; g.gridy = 0; g.gridheight = 4; g.insets = new Insets(0, 0, 0, 16);
        doctorCard.add(avatar, g);

        g.gridheight = 1; g.gridx = 1; g.insets = new Insets(1, 0, 1, 0);
        g.gridy = 0; doctorCard.add(cardName, g);
        g.gridy = 1; doctorCard.add(cardSpec, g);
        g.gridy = 2; doctorCard.add(cardQual, g);
        g.gridy = 3; doctorCard.add(cardExp,  g);

        return doctorCard;
    }

    // ── Booking form ─────────────────────────────────────────────

    private JPanel buildBookingForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD);
        form.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(6, 0, 6, 0);
        g.weightx = 1;

        // Title
        g.gridx = 0; g.gridy = 0;
        JLabel title = new JLabel("Book Appointment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(TEXT);
        form.add(title, g);

        // Schedule
        g.gridy = 1;
        form.add(fieldLabel("Available Schedule"), g);
        g.gridy = 2;
        scheduleCombo = new JComboBox<>();
        scheduleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        scheduleCombo.setBackground(CARD);
        scheduleCombo.setBorder(new LineBorder(BORDER_CLR, 1));
        form.add(scheduleCombo, g);

        // Reason
        g.gridy = 3;
        form.add(fieldLabel("Reason for Visit"), g);
        g.gridy = 4;
        reasonArea = new JTextArea(4, 0);
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setBorder(new CompoundBorder(
            new LineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        form.add(new JScrollPane(reasonArea) {{ setBorder(null); }}, g);

        // Book button
        g.gridy = 5; g.insets = new Insets(14, 0, 4, 0);
        JButton bookBtn = new JButton("Confirm Booking") {
            @Override protected void paintComponent(Graphics g2d) {
                Graphics2D g2 = (Graphics2D) g2d.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? PRIMARY.darker() : PRIMARY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g2d);
            }
        };
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setOpaque(false);
        bookBtn.setContentAreaFilled(false);
        bookBtn.setBorderPainted(false);
        bookBtn.setFocusPainted(false);
        bookBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bookBtn.setPreferredSize(new Dimension(0, 42));
        bookBtn.addActionListener(e -> bookAppointment());
        form.add(bookBtn, g);

        // Status message
        g.gridy = 6; g.insets = new Insets(4, 0, 0, 0);
        bookMsg = new JLabel(" ");
        bookMsg.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        form.add(bookMsg, g);

        // Push everything up
        g.gridy = 7; g.weighty = 1;
        form.add(Box.createVerticalGlue(), g);

        return form;
    }

    // ── Data loading ─────────────────────────────────────────────

    private void loadDoctors() {
        allDoctors = doctorDAO.getAllDoctors();
        filterDoctors();
    }

    private void filterDoctors() {
        String query = searchField.getText().trim().toLowerCase();
        boolean isPlaceholder = searchField.getForeground().equals(MUTED);

        tableModel.setRowCount(0);
        int count = 0;

        for (Doctor d : allDoctors) {
            String name = d.getFullName().toLowerCase();
            String spec = d.getSpecialization() != null ? d.getSpecialization().toLowerCase() : "";
            String qual = d.getQualification()  != null ? d.getQualification().toLowerCase()  : "";

            boolean matches = isPlaceholder || query.isEmpty()
                || name.contains(query)
                || spec.contains(query)
                || qual.contains(query);

            if (matches) {
                String initials = getInitials(d.getFullName());
                tableModel.addRow(new Object[]{
                    initials,
                    "Dr. " + d.getFullName(),
                    d.getSpecialization(),
                    d.getExperienceYears() + " yr"
                });
                count++;
            }
        }

        // Update result count label
        if (isPlaceholder || query.isEmpty()) {
            resultCount.setText(count + " doctor" + (count != 1 ? "s" : "") + " available");
        } else {
            resultCount.setText(count + " result" + (count != 1 ? "s" : "") + " for \"" + query + "\"");
        }

        // Auto-select first row
        if (tableModel.getRowCount() > 0) {
            doctorTable.setRowSelectionInterval(0, 0);
        } else {
            clearDoctorCard();
        }
    }

    private void onRowSelected() {
        int row = doctorTable.getSelectedRow();
        if (row < 0) { clearDoctorCard(); return; }

        String displayName = (String) tableModel.getValueAt(row, 1); // "Dr. John"
        String rawName = displayName.startsWith("Dr. ") ? displayName.substring(4) : displayName;

        // Find matching doctor from allDoctors
        Doctor selected = allDoctors.stream()
            .filter(d -> d.getFullName().equalsIgnoreCase(rawName))
            .findFirst().orElse(null);

        if (selected == null) return;

        // Update card
        cardName.setText("Dr. " + selected.getFullName());
        cardSpec.setText("🩺  " + (selected.getSpecialization() != null ? selected.getSpecialization() : "—"));
        cardQual.setText("🎓  " + (selected.getQualification()  != null ? selected.getQualification()  : "—"));
        cardExp.setText( "⏱  " + selected.getExperienceYears() + " years experience");
        doctorCard.repaint();

        // Load schedules
        scheduleCombo.removeAllItems();
        List<Schedule> schedules = scheduleDAO.getSchedulesByDoctor(selected.getId());
        if (schedules.isEmpty()) {
            scheduleCombo.addItem(null);
            scheduleCombo.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, s, f);
                    lbl.setText("No schedules available");
                    lbl.setForeground(MUTED);
                    return lbl;
                }
            });
        } else {
            scheduleCombo.setRenderer(new DefaultListCellRenderer());
            for (Schedule s : schedules) scheduleCombo.addItem(s);
        }

        bookMsg.setText(" ");
    }

    private void clearDoctorCard() {
        cardName.setText("Select a doctor from the list");
        cardSpec.setText(" ");
        cardQual.setText(" ");
        cardExp.setText(" ");
        scheduleCombo.removeAllItems();
        doctorCard.repaint();
    }

    // ── Book action ──────────────────────────────────────────────

    private void bookAppointment() {
        int row = doctorTable.getSelectedRow();
        if (row < 0) {
            showMsg("Please select a doctor first.", false); return;
        }

        String displayName = (String) tableModel.getValueAt(row, 1);
        String rawName = displayName.startsWith("Dr. ") ? displayName.substring(4) : displayName;
        Doctor doctor = allDoctors.stream()
            .filter(d -> d.getFullName().equalsIgnoreCase(rawName))
            .findFirst().orElse(null);

        Schedule sched = (Schedule) scheduleCombo.getSelectedItem();

        if (doctor == null) { showMsg("Please select a doctor.", false); return; }
        if (sched  == null) { showMsg("No schedule available for this doctor.", false); return; }

        // Prevent duplicate bookings for the same schedule slot
        if (apptDAO.hasActiveBooking(patient.getId(), sched.getId())) {
            showMsg("✘  You already have an active booking for this slot.", false);
            JOptionPane.showMessageDialog(this,
                "You already have a pending or approved appointment for this schedule.\n" +
                "Please choose a different time slot or cancel your existing booking first.",
                "Duplicate Booking", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment a = new Appointment();
        a.setPatientId(patient.getId());
        a.setDoctorId(doctor.getId());
        a.setScheduleId(sched.getId());
        a.setAppointmentDate(sched.getAvailableDate());
        a.setReason(reasonArea.getText().trim());

        if (apptDAO.bookAppointment(a)) {
            showMsg("✔  Appointment booked — status: PENDING", true);
            reasonArea.setText("");
            JOptionPane.showMessageDialog(this,
                "Appointment booked successfully!\n\n" +
                "Doctor : Dr. " + doctor.getFullName() + "\n" +
                "Date   : " + sched.getAvailableDate() + "\n" +
                "Time   : " + sched.getStartTime() + " – " + sched.getEndTime() + "\n" +
                "Status : PENDING (waiting for doctor approval)",
                "Booked", JOptionPane.INFORMATION_MESSAGE);
        } else {
            showMsg("✘  Booking failed. Please try again.", false);
        }
    }

    private void showMsg(String text, boolean success) {
        bookMsg.setText(text);
        bookMsg.setForeground(success ? SUCCESS : new Color(220, 38, 38));
    }

    // ── Utilities ────────────────────────────────────────────────

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(51, 65, 85));
        return l;
    }
}
