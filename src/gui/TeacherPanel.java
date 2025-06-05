package gui;

import dao.TeacherDao;
import model.Teacher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class TeacherPanel extends JPanel {
    private final TeacherDao teacherDao;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JTextField emailField;
    private Long selectedTeacherId;

    public TeacherPanel() throws SQLException, ClassNotFoundException {
        teacherDao = new TeacherDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Teacher Management", SwingConstants.CENTER);
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

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 1;
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
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonsPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Name", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(teacherTable);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        refreshTable();

        // Add listeners
        addButton.addActionListener(e -> addTeacher());
        updateButton.addActionListener(e -> updateTeacher());
        deleteButton.addActionListener(e -> deleteTeacher());
        clearButton.addActionListener(e -> clearForm());

        teacherTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = teacherTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedTeacherId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    emailField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                }
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            // Since findAll() doesn't return a list, we need to query directly
            String sql = "select * from teachers";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("email")
                });
            }
            
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading teachers: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTeacher() {
        if (!validateForm()) {
            return;
        }

        try {
            Teacher teacher = new Teacher(nameField.getText(), emailField.getText());
            teacherDao.addTeacher(teacher);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Teacher added successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error adding teacher: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTeacher() {
        if (selectedTeacherId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a teacher to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            Teacher teacher = new Teacher(selectedTeacherId, nameField.getText(), emailField.getText());
            teacherDao.updateTeacher(teacher);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Teacher updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating teacher: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTeacher() {
        if (selectedTeacherId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a teacher to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this teacher?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                teacherDao.deleteTeacher(selectedTeacherId);
                clearForm();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Teacher deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting teacher: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields",
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
        emailField.setText("");
        selectedTeacherId = null;
        teacherTable.clearSelection();
    }
} 