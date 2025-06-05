package gui;

import dao.TimeSlotDao;
import model.TimeSlot;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeSlotPanel extends JPanel {
    private final TimeSlotDao timeSlotDao;
    private JTable timeSlotTable;
    private DefaultTableModel tableModel;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private Long selectedTimeSlotId;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public TimeSlotPanel() throws SQLException, ClassNotFoundException {
        timeSlotDao = new TimeSlotDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Time Slot Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Start Time field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Start Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        startTimeField = new JTextField(20);
        formPanel.add(startTimeField, gbc);

        // End Time field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("End Time (HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        endTimeField = new JTextField(20);
        formPanel.add(endTimeField, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Time Slot");
        JButton updateButton = new JButton("Update Time Slot");
        JButton deleteButton = new JButton("Delete Time Slot");
        JButton clearButton = new JButton("Clear Form");

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
        String[] columnNames = {"ID", "Start Time", "End Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        timeSlotTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(timeSlotTable);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        refreshTable();

        // Add listeners
        addButton.addActionListener(e -> addTimeSlot());
        updateButton.addActionListener(e -> updateTimeSlot());
        deleteButton.addActionListener(e -> deleteTimeSlot());
        clearButton.addActionListener(e -> clearForm());

        timeSlotTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = timeSlotTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedTimeSlotId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    startTimeField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    endTimeField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                }
            }
        });
    }

    private void addTimeSlot() {
        try {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setStartTime(LocalTime.parse(startTimeField.getText(), timeFormatter));
            timeSlot.setEndTime(LocalTime.parse(endTimeField.getText(), timeFormatter));
            
            timeSlotDao.add(timeSlot);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Time slot added successfully!");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:mm format.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error adding time slot: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTimeSlot() {
        if (selectedTimeSlotId == null) {
            JOptionPane.showMessageDialog(this, "Please select a time slot to update!");
            return;
        }

        try {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setId(selectedTimeSlotId);
            timeSlot.setStartTime(LocalTime.parse(startTimeField.getText(), timeFormatter));
            timeSlot.setEndTime(LocalTime.parse(endTimeField.getText(), timeFormatter));
            
            timeSlotDao.update(timeSlot);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Time slot updated successfully!");
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:mm format.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error updating time slot: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTimeSlot() {
        if (selectedTimeSlotId == null) {
            JOptionPane.showMessageDialog(this, "Please select a time slot to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this time slot?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                timeSlotDao.delete(selectedTimeSlotId);
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Time slot deleted successfully!");
            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting time slot: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedTimeSlotId = null;
        startTimeField.setText("");
        endTimeField.setText("");
        timeSlotTable.clearSelection();
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            for (TimeSlot timeSlot : timeSlotDao.getAll()) {
                tableModel.addRow(new Object[]{
                    timeSlot.getId(),
                    timeSlot.getStartTime().format(timeFormatter),
                    timeSlot.getEndTime().format(timeFormatter)
                });
            }
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading time slots: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 