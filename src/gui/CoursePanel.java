package gui;

import dao.CourseDao;
import dao.DepartmentDao;
import model.Courses;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class CoursePanel extends JPanel {
    private final CourseDao courseDao;
    private final DepartmentDao departmentDao;
    private JTable courseTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField creditField;
    private JComboBox<DepartmentItem> departmentComboBox;
    private Long selectedCourseId;
    private static final Pattern CREDIT_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]+)?$");

    public CoursePanel() throws SQLException, ClassNotFoundException {
        courseDao = new CourseDao();
        departmentDao = new DepartmentDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Course Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main Content Panel with Split
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);

        // Form Panel (Left Side)
        JPanel formPanel = createFormPanel();
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBorder(BorderFactory.createTitledBorder("Course Details"));
        formContainer.add(formPanel, BorderLayout.NORTH);
        splitPane.setLeftComponent(formContainer);

        // Table Panel (Right Side)
        JPanel tablePanel = createTablePanel();
        tablePanel.setBorder(BorderFactory.createTitledBorder("Course List"));
        splitPane.setRightComponent(tablePanel);

        add(splitPane, BorderLayout.CENTER);

        // Load initial data
        loadDepartments();
        refreshTable();
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Course Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Credit Hours
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Credit Hours:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        creditField = new JTextField(20);
        formPanel.add(creditField, gbc);

        // Department
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Department:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        departmentComboBox = new JComboBox<>();
        formPanel.add(departmentComboBox, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Course");
        JButton updateButton = new JButton("Update Course");
        JButton deleteButton = new JButton("Delete Course");
        JButton clearButton = new JButton("Clear Form");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonsPanel, gbc);

        // Add listeners
        addButton.addActionListener(e -> addCourse());
        updateButton.addActionListener(e -> updateCourse());
        deleteButton.addActionListener(e -> deleteCourse());
        clearButton.addActionListener(e -> clearForm());

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));

        // Create table model
        String[] columnNames = {"ID", "Course Name", "Credit Hours", "Department"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table
        courseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(courseTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Add search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton refreshButton = new JButton("Refresh");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        tablePanel.add(searchPanel, BorderLayout.NORTH);

        // Add search functionality
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                searchCourses(searchTerm);
            }
        });

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            refreshTable();
        });

        // Add selection listener
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = courseTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedCourseId = Long.parseLong(courseTable.getValueAt(selectedRow, 0).toString());
                    nameField.setText(courseTable.getValueAt(selectedRow, 1).toString());
                    creditField.setText(courseTable.getValueAt(selectedRow, 2).toString());
                    String deptName = courseTable.getValueAt(selectedRow, 3).toString();
                    
                    for (int i = 0; i < departmentComboBox.getItemCount(); i++) {
                        if (departmentComboBox.getItemAt(i).toString().equals(deptName)) {
                            departmentComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

        return tablePanel;
    }

    private void loadDepartments() {
        try {
            String sql = "SELECT * FROM departments ORDER BY name";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            departmentComboBox.removeAllItems();
            while (rs.next()) {
                departmentComboBox.addItem(new DepartmentItem(
                    rs.getLong("id"),
                    rs.getString("name")
                ));
            }
            
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading departments: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT c.id, c.name, c.credit, d.name as dept_name " +
                        "FROM courses c " +
                        "JOIN departments d ON c.dept_id = d.id " +
                        "ORDER BY c.name";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("credit"),
                    rs.getString("dept_name")
                });
            }
            
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading courses: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchCourses(String searchTerm) {
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT c.id, c.name, c.credit, d.name as dept_name " +
                        "FROM courses c " +
                        "JOIN departments d ON c.dept_id = d.id " +
                        "WHERE LOWER(c.name) LIKE LOWER(?) OR LOWER(d.name) LIKE LOWER(?) " +
                        "ORDER BY c.name";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + searchTerm + "%");
            ps.setString(2, "%" + searchTerm + "%");
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("credit"),
                    rs.getString("dept_name")
                });
            }
            
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error searching courses: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addCourse() {
        if (!validateForm()) {
            return;
        }

        try {
            DepartmentItem selectedDept = (DepartmentItem) departmentComboBox.getSelectedItem();
            if (selectedDept == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a department",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Courses course = new Courses(
                nameField.getText().trim().toUpperCase(),
                creditField.getText().trim(),
                selectedDept.getId()
            );
            
            courseDao.addCourse(course);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Course added successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error adding course: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCourse() {
        if (selectedCourseId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            DepartmentItem selectedDept = (DepartmentItem) departmentComboBox.getSelectedItem();
            if (selectedDept == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a department",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Courses course = new Courses(
                selectedCourseId,
                nameField.getText().trim().toUpperCase(),
                creditField.getText().trim(),
                selectedDept.getId()
            );
            
            courseDao.updateCourse(course);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Course updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating course: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCourse() {
        if (selectedCourseId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a course to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this course?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                courseDao.deleteCourse(selectedCourseId);
                clearForm();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Course deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting course: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String credit = creditField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a course name",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (credit.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter credit hours",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!CREDIT_PATTERN.matcher(credit).matches()) {
            JOptionPane.showMessageDialog(this,
                "Credit hours must be a valid number",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (departmentComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a department",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        nameField.setText("");
        creditField.setText("");
        departmentComboBox.setSelectedIndex(-1);
        selectedCourseId = null;
        courseTable.clearSelection();
    }

    // Helper class for department combo box
    private static class DepartmentItem {
        private final Long id;
        private final String name;

        public DepartmentItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
} 