package ui.admin;

import dao.AppointmentDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {

    private AppointmentDAO apptDAO = new AppointmentDAO();

    public ReportsPanel() {
        setLayout(new GridLayout(1, 2, 16, 0));
        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        setBackground(new Color(245, 245, 255));
        buildUI();
    }

    private void buildUI() {
        add(buildDoctorReport());
        add(buildMonthlyReport());
    }

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
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JButton refreshBtn = new JButton("↻  Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadDoctorReport(model));

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        loadDoctorReport(model);
        return panel;
    }

    private void loadDoctorReport(DefaultTableModel model) {
        model.setRowCount(0);
        List<String[]> data = apptDAO.getAppointmentsPerDoctor();
        int max = data.stream().mapToInt(r -> Integer.parseInt(r[1])).max().orElse(1);
        for (String[] row : data) {
            model.addRow(new Object[]{"Dr. " + row[0], row[1]});
        }
    }

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
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Simple bar chart panel below the table
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                List<String[]> data = apptDAO.getMonthlyStats();
                if (data.isEmpty()) return;
                int maxVal = data.stream().mapToInt(r -> Integer.parseInt(r[1])).max().orElse(1);
                int w = getWidth(), h = getHeight();
                int barW = Math.max(10, (w - 40) / data.size() - 6);
                int x = 20;
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (String[] row : data) {
                    int val = Integer.parseInt(row[1]);
                    int barH = (int) ((double) val / maxVal * (h - 40));
                    g2.setColor(new Color(80, 0, 120));
                    g2.fillRect(x, h - barH - 20, barW, barH);
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("Arial", Font.PLAIN, 9));
                    g2.drawString(row[0].substring(5), x, h - 5); // show MM
                    g2.drawString(row[1], x, h - barH - 22);
                    x += barW + 6;
                }
            }
        };
        chartPanel.setBackground(new Color(248, 245, 255));
        chartPanel.setPreferredSize(new Dimension(0, 140));

        JButton refreshBtn = new JButton("↻  Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> { loadMonthlyReport(model); chartPanel.repaint(); });

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), chartPanel);
        split.setResizeWeight(0.6);
        split.setDividerSize(4);

        panel.add(split, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        loadMonthlyReport(model);
        return panel;
    }

    private void loadMonthlyReport(DefaultTableModel model) {
        model.setRowCount(0);
        for (String[] row : apptDAO.getMonthlyStats()) {
            model.addRow(new Object[]{row[0], row[1]});
        }
    }
}
