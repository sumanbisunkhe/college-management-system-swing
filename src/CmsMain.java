import database.DatabaseConnection;
import database.DatabaseInitializer;
import gui.MainFrame;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class CmsMain {
    public static void main(String[] args) {
        try {
            // Database Connection
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("\nDatabase Connection successful...");
            }

            DatabaseInitializer.initialize();
            System.out.println("Database initialization successful...");
            if (conn != null) {
                conn.close();
            }

            // Set system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Create the main frame
            MainFrame frame = new MainFrame();

            // Start the Swing application
            SwingUtilities.invokeLater(() -> {
                frame.setVisible(true);
            });
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Database Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}