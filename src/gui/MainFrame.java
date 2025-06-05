package gui;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private static final String DASHBOARD = "Dashboard";
    private static final String DEPARTMENT = "Department";
    private static final String STUDENT = "Student";
    private static final String TEACHER = "Teacher";
    private static final String COURSE = "Course";
    private static final String ENROLLMENT = "Enrollment";
    private static final String CLASS_SCHEDULE = "Class Schedule";

    public MainFrame() {
        setTitle("College Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // Initialize card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Navigation");
        
        // Create menu items
        addMenuItem(menu, "Dashboard", DASHBOARD);
        addMenuItem(menu, "Department", DEPARTMENT);
        addMenuItem(menu, "Student", STUDENT);
        addMenuItem(menu, "Teacher", TEACHER);
        addMenuItem(menu, "Course", COURSE);
        addMenuItem(menu, "Enrollment", ENROLLMENT);
        addMenuItem(menu, "Class Schedule", CLASS_SCHEDULE);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        // Add panels
        try {
            mainPanel.add(new DashboardPanel(), DASHBOARD);
            mainPanel.add(new DepartmentPanel(), DEPARTMENT);
            mainPanel.add(new StudentPanel(), STUDENT);
            mainPanel.add(new TeacherPanel(), TEACHER);
            mainPanel.add(new CoursePanel(), COURSE);
            mainPanel.add(new EnrollmentPanel(), ENROLLMENT);
            mainPanel.add(new ClassSchedulePanel(), CLASS_SCHEDULE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error initializing panels: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        add(mainPanel);
        cardLayout.show(mainPanel, DASHBOARD);
    }

    private void addMenuItem(JMenu menu, String label, String cardName) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.addActionListener(e -> cardLayout.show(mainPanel, cardName));
        menu.add(menuItem);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
} 