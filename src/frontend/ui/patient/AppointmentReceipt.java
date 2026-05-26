package frontend.ui.patient;

import java.awt.*;
import java.awt.print.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

/**
 * AppointmentReceipt — prints a clean one-page receipt for an appointment.
 *
 * Uses Java's built-in PrinterJob API (no extra libraries needed).
 *
 * How to use:
 *   AppointmentReceipt.print(parent,
 *       patientName, doctorName, date, timeSlot, reason, status, notes);
 *
 * The receipt looks like:
 *
 *   ┌─────────────────────────────────────┐
 *   │         CITY HOSPITAL               │
 *   │     Appointment Receipt             │
 *   ├─────────────────────────────────────┤
 *   │  Appointment ID : #42               │
 *   │  Patient        : John Doe          │
 *   │  Doctor         : Dr. Smith         │
 *   │  Date           : 2026-06-01        │
 *   │  Time           : 09:00 - 10:00     │
 *   │  Reason         : Headache          │
 *   │  Status         : APPROVED          │
 *   │  Medical Notes  : Take rest...      │
 *   ├─────────────────────────────────────┤
 *   │  Printed: 2026-05-26 14:30          │
 *   └─────────────────────────────────────┘
 */
public class AppointmentReceipt implements Printable {

    // ── Receipt data fields ───────────────────────────────────────
    private final int    appointmentId;
    private final String patientName;
    private final String doctorName;
    private final String date;
    private final String timeSlot;
    private final String reason;
    private final String status;
    private final String medicalNotes;

    // Timestamp of when the receipt was printed
    private final String printedAt;

    // ── Colours used on screen preview (not on paper) ────────────
    private static final Color PURPLE     = new Color(88, 28, 135);
    private static final Color LIGHT_GRAY = new Color(245, 245, 245);
    private static final Color BORDER     = new Color(200, 200, 200);

    // ── Constructor ───────────────────────────────────────────────

    private AppointmentReceipt(int appointmentId, String patientName, String doctorName,
                                String date, String timeSlot, String reason,
                                String status, String medicalNotes) {
        this.appointmentId = appointmentId;
        this.patientName   = patientName;
        this.doctorName    = doctorName;
        this.date          = date;
        this.timeSlot      = timeSlot;
        this.reason        = reason  != null && !reason.isEmpty()       ? reason       : "—";
        this.status        = status  != null                            ? status       : "PENDING";
        this.medicalNotes  = medicalNotes != null && !medicalNotes.isEmpty() ? medicalNotes : "No notes yet.";
        this.printedAt     = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm"));
    }

    // ── Public entry point ────────────────────────────────────────

    /**
     * Shows the system print dialog and prints the receipt.
     *
     * @param parent        parent component for dialogs (can be null)
     * @param appointmentId appointment ID number
     * @param patientName   patient's full name
     * @param doctorName    doctor's full name (without "Dr." prefix)
     * @param date          appointment date string
     * @param timeSlot      time slot string e.g. "09:00 - 10:00"
     * @param reason        reason for visit
     * @param status        appointment status
     * @param medicalNotes  doctor's medical notes
     */
    public static void print(Component parent,
                             int appointmentId, String patientName, String doctorName,
                             String date, String timeSlot, String reason,
                             String status, String medicalNotes) {

        AppointmentReceipt receipt = new AppointmentReceipt(
            appointmentId, patientName, doctorName,
            date, timeSlot, reason, status, medicalNotes
        );

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Appointment Receipt #" + appointmentId);
        job.setPrintable(receipt);

        // Show the system print dialog — user picks printer, copies, etc.
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(parent,
                    "Printing failed: " + ex.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Printable implementation ──────────────────────────────────

    /**
     * Called by the printer for each page.
     * We only have one page, so we return NO_SUCH_PAGE for page > 0.
     * NOTE: rowCount must be reset each time print() is called because
     * the printer may call this method more than once (e.g. preview + print).
     */
    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;
        rowCount = 0;  // reset so alternating row shading is consistent on every pass

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Translate to the printable area (respects page margins)
        g2.translate(pf.getImageableX(), pf.getImageableY());

        int pageWidth = (int) pf.getImageableWidth();
        int y = 0;   // current Y position as we draw downward

        // ── Hospital name (big, centred) ──────────────────────────
        g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2.setColor(Color.BLACK);
        y += 28;
        drawCentred(g2, "CITY HOSPITAL", pageWidth, y);

        // ── Subtitle ──────────────────────────────────────────────
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g2.setColor(new Color(80, 80, 80));
        y += 18;
        drawCentred(g2, "Appointment Receipt", pageWidth, y);

        // ── Top divider ───────────────────────────────────────────
        y += 14;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, y, pageWidth, y);

        // ── Appointment ID badge ──────────────────────────────────
        y += 20;
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(new Color(80, 80, 80));
        drawCentred(g2, "Appointment  #" + appointmentId, pageWidth, y);

        // ── Details rows ──────────────────────────────────────────
        y += 22;
        g2.setStroke(new BasicStroke(0.5f));
        g2.setColor(new Color(230, 230, 230));
        g2.drawLine(0, y, pageWidth, y);   // thin separator before rows

        y = drawRow(g2, pageWidth, y, "Patient",       patientName);
        y = drawRow(g2, pageWidth, y, "Doctor",        "Dr. " + doctorName);
        y = drawRow(g2, pageWidth, y, "Date",          date);
        y = drawRow(g2, pageWidth, y, "Time Slot",     timeSlot);
        y = drawRow(g2, pageWidth, y, "Reason",        reason);
        y = drawRow(g2, pageWidth, y, "Status",        status);

        // Medical notes may be long — wrap it
        y = drawRowWrapped(g2, pageWidth, y, "Medical Notes", medicalNotes);

        // ── Bottom divider ────────────────────────────────────────
        y += 10;
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawLine(0, y, pageWidth, y);

        // ── Printed timestamp ─────────────────────────────────────
        y += 16;
        g2.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        g2.setColor(new Color(120, 120, 120));
        drawCentred(g2, "Printed: " + printedAt, pageWidth, y);

        // ── Footer note ───────────────────────────────────────────
        y += 14;
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        drawCentred(g2, "Please bring this receipt to your appointment.", pageWidth, y);

        return PAGE_EXISTS;
    }

    // ── Drawing helpers ───────────────────────────────────────────

    /**
     * Draws one label + value row with a light background on alternating rows.
     * Returns the new Y position after the row.
     */
    private int rowCount = 0;   // used for alternating row shading

    private int drawRow(Graphics2D g2, int pageWidth, int y, String label, String value) {
        int rowH = 22;
        int labelX = 10;
        int valueX = 130;

        // Alternating row background
        if (rowCount % 2 == 0) {
            g2.setColor(new Color(248, 248, 248));
            g2.fillRect(0, y, pageWidth, rowH);
        }
        rowCount++;

        // Label (bold)
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(new Color(60, 60, 60));
        g2.drawString(label, labelX, y + 15);

        // Colon separator
        g2.drawString(":", valueX - 14, y + 15);

        // Value (plain)
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(Color.BLACK);
        g2.drawString(value, valueX, y + 15);

        // Bottom border of row
        g2.setColor(new Color(235, 235, 235));
        g2.setStroke(new BasicStroke(0.5f));
        g2.drawLine(0, y + rowH, pageWidth, y + rowH);

        return y + rowH;
    }

    /**
     * Like drawRow but wraps long text across multiple lines.
     * Used for Medical Notes which can be lengthy.
     */
    private int drawRowWrapped(Graphics2D g2, int pageWidth, int y,
                                String label, String value) {
        int lineH   = 16;
        int labelX  = 10;
        int valueX  = 130;
        int maxW    = pageWidth - valueX - 10;  // available width for value text

        // Split value into lines that fit within maxW
        java.util.List<String> lines = wrapText(g2, value,
            new Font("Segoe UI", Font.PLAIN, 11), maxW);

        int totalH = Math.max(22, lines.size() * lineH + 8);

        // Row background
        if (rowCount % 2 == 0) {
            g2.setColor(new Color(248, 248, 248));
            g2.fillRect(0, y, pageWidth, totalH);
        }
        rowCount++;

        // Label
        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(new Color(60, 60, 60));
        g2.drawString(label, labelX, y + 15);
        g2.drawString(":", valueX - 14, y + 15);

        // Value lines
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(Color.BLACK);
        int lineY = y + 15;
        for (String line : lines) {
            g2.drawString(line, valueX, lineY);
            lineY += lineH;
        }

        // Bottom border
        g2.setColor(new Color(235, 235, 235));
        g2.setStroke(new BasicStroke(0.5f));
        g2.drawLine(0, y + totalH, pageWidth, y + totalH);

        return y + totalH;
    }

    /** Draws a string horizontally centred within the given width. */
    private void drawCentred(Graphics2D g2, String text, int pageWidth, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (pageWidth - fm.stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

    /**
     * Breaks a string into lines that each fit within maxWidth pixels
     * using the given font.
     */
    private java.util.List<String> wrapText(Graphics2D g2, String text,
                                             Font font, int maxWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String test = current.length() == 0 ? word : current + " " + word;
            if (fm.stringWidth(test) <= maxWidth) {
                current = new StringBuilder(test);
            } else {
                if (current.length() > 0) lines.add(current.toString());
                current = new StringBuilder(word);
            }
        }
        if (current.length() > 0) lines.add(current.toString());
        if (lines.isEmpty()) lines.add("—");

        return lines;
    }
}
