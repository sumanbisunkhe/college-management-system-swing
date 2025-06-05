package gui;

import dao.RoomDao;
import model.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class RoomPanel extends JPanel {
    private final RoomDao roomDao;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField roomNumberField;
    private JSpinner capacitySpinner;
    private Long selectedRoomId;

    public RoomPanel() throws SQLException, ClassNotFoundException {
        roomDao = new RoomDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Room Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Room Number field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Room Number:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        roomNumberField = new JTextField(20);
        formPanel.add(roomNumberField, gbc);

        // Capacity spinner
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Capacity:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(30, 1, 500, 1);
        capacitySpinner = new JSpinner(spinnerModel);
        formPanel.add(capacitySpinner, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Room");
        JButton updateButton = new JButton("Update Room");
        JButton deleteButton = new JButton("Delete Room");
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
        String[] columnNames = {"ID", "Room Number", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        roomTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomTable);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        refreshTable();

        // Add listeners
        addButton.addActionListener(e -> addRoom());
        updateButton.addActionListener(e -> updateRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        clearButton.addActionListener(e -> clearForm());

        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = roomTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedRoomId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    roomNumberField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    capacitySpinner.setValue(Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString()));
                }
            }
        });
    }

    private void addRoom() {
        try {
            Room room = new Room();
            room.setRoomNumber(roomNumberField.getText().trim());
            room.setCapacity((Integer) capacitySpinner.getValue());
            
            roomDao.add(room);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Room added successfully!");
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error adding room: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom() {
        if (selectedRoomId == null) {
            JOptionPane.showMessageDialog(this, "Please select a room to update!");
            return;
        }

        try {
            Room room = new Room();
            room.setId(selectedRoomId);
            room.setRoomNumber(roomNumberField.getText().trim());
            room.setCapacity((Integer) capacitySpinner.getValue());
            
            roomDao.update(room);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Room updated successfully!");
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error updating room: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        if (selectedRoomId == null) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this room?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                roomDao.delete(selectedRoomId);
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Room deleted successfully!");
            } catch (SQLException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting room: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        selectedRoomId = null;
        roomNumberField.setText("");
        capacitySpinner.setValue(30);
        roomTable.clearSelection();
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            for (Room room : roomDao.getAll()) {
                tableModel.addRow(new Object[]{
                    room.getId(),
                    room.getRoomNumber(),
                    room.getCapacity()
                });
            }
        } catch (SQLException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "Error loading rooms: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 