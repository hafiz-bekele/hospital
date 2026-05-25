import frontend.ui.LoginFrame;
import javax.swing.*;

/**
 * Main Entry Point for Hospital Management System
 * 
 * This is where everything starts! When you run the application,
 * this class launches the login window and gets things rolling.
 * 
 * @author Hospital Management Team
 * @version 2.0
 */
public class Main {
    
    public static void main(String[] args) {
        // Make the app look native to the operating system (Windows/Mac/Linux)
        // This gives us nice native buttons and windows instead of the default Java look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If that doesn't work, no worries - we'll just use the default Java look
        }

        // Launch the login window on the UI thread (this is the proper way to start Swing apps)
        // SwingUtilities.invokeLater ensures thread safety for the UI
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginWindow = new LoginFrame();
            loginWindow.setVisible(true);
        });
    }
}
