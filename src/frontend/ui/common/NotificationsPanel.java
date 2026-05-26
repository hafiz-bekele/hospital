package frontend.ui.common;

import backend.dao.NotificationDAO;
import backend.models.Notification;
import backend.models.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class NotificationsPanel extends JPanel {

    private User user;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel unreadLbl;
    private NotificationDAO notifDAO = new NotificationDAO();

    public NotificationsPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        buildUI();
    }

    // Build the main UI with header, notifications table, and action buttons
    private void buildUI() {
        // ── Header bar ───────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(255, 248, 220));
        topBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        unreadLbl = new JLabel("Unread: 0");
        unreadLbl.setFont(new Font("Arial", Font.BOLD, 13));
        unreadLbl.setForeground(new Color(180, 100, 0));
        topBar.add(unreadLbl, BorderLayout.WEST);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"", "Message", "Date & Time"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                String status = (String) getValueAt(row, 0);
                if (!isRowSelected(row)) {
                    c.setBackground("●".equals(status) ? new Color(255, 248, 220) : Color.WHITE);
                    c.setForeground("●".equals(status) ? Color.BLACK : Color.GRAY);
                } else {
                    c.setBackground(new Color(173, 216, 230));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.setRowHeight(26);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.getColumnModel().getColumn(0).setMaxWidth(30);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setMaxWidth(200);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Notifications"));

        // ── Buttons ──────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        btnPanel.setBackground(new Color(245, 245, 245));

        JButton markReadBtn  = new JButton("✔  Mark All Read");
        JButton clearBtn     = new JButton("🗑  Clear All");
        JButton refreshBtn   = new JButton("↻  Refresh");
        styleBtn(markReadBtn,  new Color(40, 140, 40));
        styleBtn(clearBtn,     new Color(180, 40, 40));
        styleBtn(refreshBtn,   new Color(70, 70, 70));

        markReadBtn.addActionListener(e -> { notifDAO.markAllRead(user.getId()); refresh(); });
        clearBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this, "Clear all notifications?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) { notifDAO.deleteAll(user.getId()); refresh(); }
        });
        refreshBtn.addActionListener(e -> refresh());

        btnPanel.add(markReadBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(refreshBtn);

        add(topBar,   BorderLayout.NORTH);
        add(scroll,   BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        refresh();
    }

    // Reload notifications from DB and update the unread count label
    public void refresh() {
        tableModel.setRowCount(0);
        List<Notification> list = notifDAO.getForUser(user.getId());
        int unread = 0;
        for (Notification n : list) {
            tableModel.addRow(new Object[]{
                n.isRead() ? "" : "●",
                n.getMessage(),
                n.getCreatedAt()
            });
            if (!n.isRead()) unread++;
        }
        unreadLbl.setText("Unread: " + unread);
    }

    /** Returns unread count — used to update tab title badge */
    public int getUnreadCount() {
        return notifDAO.countUnread(user.getId());
    }

    // Create a styled action button with a colored background
    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(170, 34));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
