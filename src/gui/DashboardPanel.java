package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class DashboardPanel extends JPanel {
    public DashboardPanel() throws SQLException, ClassNotFoundException {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Welcome to College Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Welcome message
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel welcomeMessage = new JLabel("Please use the navigation menu to manage the system.");
        welcomeMessage.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomePanel.add(welcomeMessage, gbc);

        JLabel instructionLabel = new JLabel("You can manage departments, students, teachers, courses, and enrollments.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomePanel.add(instructionLabel, gbc);

        add(welcomePanel, BorderLayout.CENTER);
    }
} 