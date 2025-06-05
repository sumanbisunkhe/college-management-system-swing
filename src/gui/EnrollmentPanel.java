package gui;

import dao.EnrollmentDao;
import dao.StudentDao;
import dao.CourseDao;
import model.Enrollment;
import model.Courses;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.Year;

public class EnrollmentPanel extends JPanel {
    private final EnrollmentDao enrollmentDao;
    private final StudentDao studentDao;
    private final CourseDao courseDao;
    private JTable enrollmentTable;
    private JTable studentListTable;
    private DefaultTableModel enrollmentTableModel;
    private DefaultTableModel studentListTableModel;
    private JComboBox<StudentItem> studentComboBox;
    private JComboBox<CourseItem> courseComboBox;
    private JComboBox<String> semesterComboBox;
    private JComboBox<String> gradeComboBox;
    private Long selectedEnrollmentId;
    private static final String[] GRADES = {"A", "B", "C", "D", "F", "I", "W"};

    public EnrollmentPanel() throws SQLException, ClassNotFoundException {
        enrollmentDao = new EnrollmentDao();
        studentDao = new StudentDao();
        courseDao = new CourseDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Split the panel into two sections
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        add(splitPane, BorderLayout.CENTER);

        // Left panel - Student List
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Available Students"));
        
        // Student List Table
        String[] studentColumns = {"ID", "Name", "Email", "Date of Birth"};
        studentListTableModel = new DefaultTableModel(studentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentListTable = new JTable(studentListTableModel);
        JScrollPane studentScrollPane = new JScrollPane(studentListTable);
        leftPanel.add(studentScrollPane, BorderLayout.CENTER);

        // Add refresh button for student list
        JButton refreshStudentsButton = new JButton("Refresh Student List");
        refreshStudentsButton.addActionListener(e -> refreshStudentList());
        leftPanel.add(refreshStudentsButton, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // Right panel - Enrollment Form and Table
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Management"));

        // Title
        JLabel titleLabel = new JLabel("New Enrollment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Student ComboBox
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Student:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        studentComboBox = new JComboBox<>();
        loadStudents();
        formPanel.add(studentComboBox, gbc);

        // Course ComboBox
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Course:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        courseComboBox = new JComboBox<>();
        loadCourses();
        formPanel.add(courseComboBox, gbc);

        // Semester ComboBox
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Semester:"), gbc);

        gbc.gridx = 1;
        semesterComboBox = new JComboBox<>();
        loadSemesters();
        formPanel.add(semesterComboBox, gbc);

        // Grade ComboBox
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Grade:"), gbc);

        gbc.gridx = 1;
        gradeComboBox = new JComboBox<>(GRADES);
        formPanel.add(gradeComboBox, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update Grade");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonsPanel, gbc);

        // Enrollment Table
        String[] enrollmentColumns = {"ID", "Student", "Course", "Semester", "Grade"};
        enrollmentTableModel = new DefaultTableModel(enrollmentColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        enrollmentTable = new JTable(enrollmentTableModel);
        JScrollPane enrollmentScrollPane = new JScrollPane(enrollmentTable);

        // Right Panel Layout
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(formPanel, BorderLayout.NORTH);
        formWrapper.add(enrollmentScrollPane, BorderLayout.CENTER);
        rightPanel.add(formWrapper, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);

        // Load initial data
        refreshStudentList();
        refreshTable();

        // Add listeners
        addButton.addActionListener(e -> addEnrollment());
        updateButton.addActionListener(e -> updateGrade());
        deleteButton.addActionListener(e -> deleteEnrollment());
        clearButton.addActionListener(e -> clearForm());

        // Double-click on student list to select student
        studentListTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = studentListTable.getSelectedRow();
                    if (row != -1) {
                        String studentName = studentListTable.getValueAt(row, 1).toString();
                        for (int i = 0; i < studentComboBox.getItemCount(); i++) {
                            if (studentComboBox.getItemAt(i).toString().equals(studentName)) {
                                studentComboBox.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                }
            }
        });

        enrollmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = enrollmentTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedEnrollmentId = Long.parseLong(enrollmentTable.getValueAt(selectedRow, 0).toString());
                    String studentName = enrollmentTable.getValueAt(selectedRow, 1).toString();
                    String courseName = enrollmentTable.getValueAt(selectedRow, 2).toString();
                    String semester = enrollmentTable.getValueAt(selectedRow, 3).toString();
                    String grade = enrollmentTable.getValueAt(selectedRow, 4).toString();

                    selectComboBoxItem(studentComboBox, studentName);
                    selectComboBoxItem(courseComboBox, courseName);
                    selectComboBoxItem(semesterComboBox, semester);
                    selectComboBoxItem(gradeComboBox, grade);
                }
            }
        });
    }

    private void refreshStudentList() {
        studentListTableModel.setRowCount(0);
        try {
            String sql = "SELECT id, name, email, dob FROM students ORDER BY name";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                studentListTableModel.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getDate("dob")
                });
            }
            
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading student list: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStudents() {
        try {
            String sql = "SELECT * FROM students ORDER BY name";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            studentComboBox.removeAllItems();
            while (rs.next()) {
                studentComboBox.addItem(new StudentItem(
                    rs.getLong("id"),
                    rs.getString("name")
                ));
            }
            
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading students: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCourses() {
        try {
            String sql = "SELECT c.*, d.name as dept_name FROM courses c JOIN departments d ON c.dept_id = d.id ORDER BY c.name";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            courseComboBox.removeAllItems();
            while (rs.next()) {
                courseComboBox.addItem(new CourseItem(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("dept_name")
                ));
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

    private void loadSemesters() {
        
            semesterComboBox.addItem("First Semester");
            semesterComboBox.addItem("Second Semester");
            semesterComboBox.addItem("Third Semester");
            semesterComboBox.addItem("Fourth Semester");
            semesterComboBox.addItem("Fifth Semester");
            semesterComboBox.addItem("Sixth Semester");
            semesterComboBox.addItem("Seventh Semester");
            semesterComboBox.addItem("Eighth Semester");
        
    }

    private void refreshTable() {
        enrollmentTableModel.setRowCount(0);
        try {
            String sql = "SELECT e.id, s.name as student_name, c.name as course_name, e.semester, e.grade " +
                        "FROM enrollments e " +
                        "JOIN students s ON e.student_id = s.id " +
                        "JOIN courses c ON e.course_id = c.id " +
                        "ORDER BY e.semester DESC, s.name";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                enrollmentTableModel.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("student_name"),
                    rs.getString("course_name"),
                    rs.getString("semester"),
                    rs.getString("grade")
                });
            }
            
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading enrollments: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addEnrollment() {
        if (!validateForm()) {
            return;
        }

        try {
            StudentItem selectedStudent = (StudentItem) studentComboBox.getSelectedItem();
            CourseItem selectedCourse = (CourseItem) courseComboBox.getSelectedItem();
            String semester = (String) semesterComboBox.getSelectedItem();
            String grade = (String) gradeComboBox.getSelectedItem();
            
            if (selectedStudent == null || selectedCourse == null || semester == null || grade == null) {
                JOptionPane.showMessageDialog(this,
                    "Please fill in all fields",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (enrollmentDao.existsEnrollment(selectedStudent.getId(), selectedCourse.getId(), semester)) {
                JOptionPane.showMessageDialog(this,
                    "This student is already enrolled in this course for the selected semester",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            Enrollment enrollment = new Enrollment(
                selectedStudent.getId(),
                selectedCourse.getId(),
                semester,
                grade
            );
            
            enrollmentDao.save(enrollment);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Enrollment added successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error adding enrollment: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateGrade() {
        if (selectedEnrollmentId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an enrollment to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String grade = (String) gradeComboBox.getSelectedItem();
        if (grade == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a grade",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            enrollmentDao.updateGrade(selectedEnrollmentId, grade);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Grade updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating grade: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEnrollment() {
        if (selectedEnrollmentId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an enrollment to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this enrollment?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                enrollmentDao.delete(selectedEnrollmentId);
                clearForm();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Enrollment deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting enrollment: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        if (studentComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a student",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (courseComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a course",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (semesterComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a semester",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (gradeComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a grade",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        studentComboBox.setSelectedIndex(-1);
        courseComboBox.setSelectedIndex(-1);
        semesterComboBox.setSelectedIndex(-1);
        gradeComboBox.setSelectedIndex(-1);
        selectedEnrollmentId = null;
        enrollmentTable.clearSelection();
    }

    private void selectComboBoxItem(JComboBox<?> comboBox, String value) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).toString().equals(value)) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    // Helper class for student combo box
    private static class StudentItem {
        private final Long id;
        private final String name;

        public StudentItem(Long id, String name) {
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

    // Helper class for course combo box
    private static class CourseItem {
        private final Long id;
        private final String name;
        private final String department;

        public CourseItem(Long id, String name, String department) {
            this.id = id;
            this.name = name;
            this.department = department;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return name + " (" + department + ")";
        }
    }
} 