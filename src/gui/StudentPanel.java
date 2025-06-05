package gui;

import dao.StudentDao;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StudentPanel extends JPanel {
    private final StudentDao studentDao;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField dobField;
    private JTextField emailField;
    private Long selectedStudentId;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public StudentPanel() throws SQLException, ClassNotFoundException {
        studentDao = new StudentDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Student Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // DOB field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        dobField = new JTextField(20);
        formPanel.add(dobField, gbc);

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonsPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Name", "Date of Birth", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        refreshTable();

        // Add listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearForm());

        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = studentTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedStudentId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    dobField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    emailField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            // Since findAll() doesn't return a list, we need to query directly
            String sql = "select * from students";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDate("dob").toLocalDate(),
                    rs.getString("email")
                });
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

    private void addStudent() {
        if (!validateForm()) {
            return;
        }

        try {
            LocalDate dob = LocalDate.parse(dobField.getText(), dateFormatter);
            Student student = new Student(nameField.getText(), dob, emailField.getText());
            
            if (studentDao.existsByEmail(emailField.getText())) {
                JOptionPane.showMessageDialog(this,
                    "A student with this email already exists",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            studentDao.save(student);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Student added successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error adding student: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        if (selectedStudentId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            LocalDate dob = LocalDate.parse(dobField.getText(), dateFormatter);
            Student student = new Student(selectedStudentId, nameField.getText(), dob, emailField.getText());
            studentDao.update(selectedStudentId, student);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Student updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating student: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a student to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentDao.delete(selectedStudentId);
                clearForm();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Student deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting student: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String dob = dobField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || dob.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            LocalDate.parse(dob, dateFormatter);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Please use yyyy-MM-dd",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Invalid email format",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void clearForm() {
        nameField.setText("");
        dobField.setText("");
        emailField.setText("");
        selectedStudentId = null;
        studentTable.clearSelection();
    }
} 