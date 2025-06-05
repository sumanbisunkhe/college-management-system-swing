package gui;

import dao.DepartmentDao;
import model.Department;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class DepartmentPanel extends JPanel {
    private final DepartmentDao departmentDao;
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private Long selectedDepartmentId;

    public DepartmentPanel() throws SQLException, ClassNotFoundException {
        departmentDao = new DepartmentDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Department Management", SwingConstants.CENTER);
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
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonsPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Name"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        departmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(departmentTable);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        refreshTable();

        // Add listeners
        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        clearButton.addActionListener(e -> clearForm());

        departmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = departmentTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedDepartmentId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                }
            }
        });
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            departmentDao.findAll();
            // Since findAll() doesn't return a list, we need to query each department individually
            // This is not efficient but we'll work with the existing DAO
            String sql = "select * from departments";
            java.sql.Connection conn = database.DatabaseConnection.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getLong("id"),
                    rs.getString("name")
                });
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

    private void addDepartment() {
        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in the name field",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Department department = new Department(name);
            departmentDao.save(department);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Department added successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error adding department: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDepartment() {
        if (selectedDepartmentId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a department to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in the name field",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Department department = new Department(name);
            departmentDao.update(selectedDepartmentId, department);
            clearForm();
            refreshTable();
            JOptionPane.showMessageDialog(this,
                "Department updated successfully",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating department: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDepartment() {
        if (selectedDepartmentId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a department to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this department?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                departmentDao.delete(selectedDepartmentId);
                clearForm();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Department deleted successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting department: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        selectedDepartmentId = null;
        departmentTable.clearSelection();
    }
} 