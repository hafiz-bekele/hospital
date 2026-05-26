package frontend.ui.admin;

import backend.dao.AppointmentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * ReportsPanel — shows two side-by-side reports:
 *   Left  : Appointments per Doctor (table)
 *   Right : Monthly Appointment Trend (table + bar chart)
 *
 * Data is loaded once on refresh, NOT on every repaint.
 * The chart caches its data so paintComponent() never hits the DB.
 */
public class ReportsPanel extends JPanel {

    private AppointmentDAO apptDAO = new AppointmentDAO();

    public ReportsPanel() {
        setLayout(new GridLayout(1, 2, 16, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setBackground(new Color(245, 245, 255));
        buildUI();
    }

    // Build both report panels side by side
    private void buildUI() {
        add(buildDoctorReport());
        add(buildMonthlyReport());
    }

    // ── Left panel: appointments per doctor ───────────────────────

    // Build the left panel showing appointment count per doctor
    private JPanel buildDoctorReport() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Appointments per Doctor"));

        String[] cols = {"Doctor", "Total Appointments"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(219, 234, 254));

        JButton refreshBtn = makeRefreshBtn();
        refreshBtn.addActionListener(e -> loadDoctorReport(model));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        loadDoctorReport(model);
        return panel;
    }

    // Load doctor appointment counts into the table
    private void loadDoctorReport(DefaultTableModel model) {
        model.setRowCount(0);
        for (String[] row : apptDAO.getAppointmentsPerDoctor()) {
            model.addRow(new Object[]{ "Dr. " + row[0], row[1] });
        }
    }

    // ── Right panel: monthly trend ────────────────────────────────

    // Build the right panel showing monthly appointment trend with a bar chart
    private JPanel buildMonthlyReport() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Monthly Appointment Trend"));

        String[] cols = {"Month", "Total Appointments"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setSelectionBackground(new Color(219, 234, 254));

        // Chart caches its own data — never queries DB inside paintComponent
        BarChartPanel chart = new BarChartPanel();
        chart.setPreferredSize(new Dimension(0, 150));

        JButton refreshBtn = makeRefreshBtn();
        refreshBtn.addActionListener(e -> {
            loadMonthlyReport(model);
            chart.setData(apptDAO.getMonthlyStats());  // update chart data once
        });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), chart);
        split.setResizeWeight(0.6);
        split.setDividerSize(4);

        panel.add(split, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        // Initial load
        List<String[]> stats = apptDAO.getMonthlyStats();
        loadMonthlyReport(model, stats);
        chart.setData(stats);

        return panel;
    }

    // Load monthly stats into the table (overload that fetches fresh data)
    private void loadMonthlyReport(DefaultTableModel model) {
        loadMonthlyReport(model, apptDAO.getMonthlyStats());
    }

    // Load monthly stats into the table from already-fetched data
    private void loadMonthlyReport(DefaultTableModel model, List<String[]> data) {
        model.setRowCount(0);
        for (String[] row : data) {
            model.addRow(new Object[]{ row[0], row[1] });
        }
    }

    // ── Shared button factory ─────────────────────────────────────

    // Create a shared refresh button used by both report panels
    private JButton makeRefreshBtn() {
        JButton btn = new JButton("↻  Refresh");
        btn.setBackground(new Color(70, 70, 70));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ── Bar chart panel (data-driven, no DB calls inside paint) ───

    /**
     * A simple bar chart that renders from a cached data list.
     * Call setData() to update — paintComponent() only reads the cache.
     */
    private static class BarChartPanel extends JPanel {

        private List<String[]> data = List.of();

        void setData(List<String[]> data) {
            this.data = data != null ? data : List.of();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int maxVal = data.stream()
                    .mapToInt(r -> Integer.parseInt(r[1]))
                    .max().orElse(1);

            int barW = Math.max(10, (w - 40) / data.size() - 6);
            int x = 20;

            for (String[] row : data) {
                int val    = Integer.parseInt(row[1]);
                int barH   = (int) ((double) val / maxVal * (h - 40));
                String mon = row[0].length() >= 7 ? row[0].substring(5) : row[0]; // "MM"

                // Bar
                g2.setColor(new Color(80, 0, 120));
                g2.fillRoundRect(x, h - barH - 20, barW, barH, 4, 4);

                // Value label above bar
                g2.setColor(new Color(60, 60, 60));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.drawString(row[1], x + (barW - g2.getFontMetrics().stringWidth(row[1])) / 2,
                        h - barH - 23);

                // Month label below bar
                g2.setColor(Color.DARK_GRAY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                g2.drawString(mon, x + (barW - g2.getFontMetrics().stringWidth(mon)) / 2, h - 5);

                x += barW + 6;
            }
        }
    }
}
