package gui;

import dao.CourseDao;
import dao.TeacherDao;
import database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClassSchedulePanel extends JPanel {
    private JTabbedPane tabbedPane;
    private RoomPanel roomPanel;
    private TimeSlotPanel timeSlotPanel;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private JComboBox<CourseItem> courseComboBox;
    private JComboBox<TeacherItem> teacherComboBox;
    private JComboBox<TimeSlotItem> timeSlotComboBox;
    private JComboBox<RoomItem> roomComboBox;
    private Long selectedScheduleId;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ClassSchedulePanel() throws SQLException, ClassNotFoundException {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create and add Room Management panel
        roomPanel = new RoomPanel();
        tabbedPane.addTab("Room Management", new ImageIcon(), roomPanel, "Manage Rooms");
        
        // Create and add Time Slot Management panel
        timeSlotPanel = new TimeSlotPanel();
        tabbedPane.addTab("Time Slot Management", new ImageIcon(), timeSlotPanel, "Manage Time Slots");
        
        // Create and add Class Schedule Management panel
        JPanel schedulePanel = createSchedulePanel();
        tabbedPane.addTab("Class Schedule", new ImageIcon(), schedulePanel, "Manage Class Schedules");

        // Add tab change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 2) { // Class Schedule tab
                refreshAllData();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void refreshAllData() {
        loadRooms();
        loadTimeSlots();
        loadCourses();
        loadTeachers();
        refreshTable();
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize combo boxes
        courseComboBox = new JComboBox<>();
        teacherComboBox = new JComboBox<>();
        timeSlotComboBox = new JComboBox<>();
        roomComboBox = new JComboBox<>();

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Course Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Course:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(courseComboBox, gbc);

        // Teacher Selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Teacher:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(teacherComboBox, gbc);

        // Time Slot Selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Time Slot:"), gbc);

        gbc.gridx = 1;
        formPanel.add(timeSlotComboBox, gbc);

        // Room Selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Room:"), gbc);

        gbc.gridx = 1;
        formPanel.add(roomComboBox, gbc);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addButton = new JButton("Add Schedule");
        JButton updateButton = new JButton("Update Schedule");
        JButton deleteButton = new JButton("Delete Schedule");
        JButton clearButton = new JButton("Clear Form");

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonsPanel, gbc);

        // Table
        String[] columnNames = {"ID", "Course", "Teacher", "Time Slot", "Room"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scheduleTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadCourses();
        loadTeachers();
        loadTimeSlots();
        loadRooms();
        refreshTable();

        // Add listeners
        addButton.addActionListener(e -> addSchedule());
        updateButton.addActionListener(e -> updateSchedule());
        deleteButton.addActionListener(e -> deleteSchedule());
        clearButton.addActionListener(e -> clearForm());

        scheduleTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = scheduleTable.getSelectedRow();
                if (selectedRow != -1) {
                    selectedScheduleId = Long.parseLong(tableModel.getValueAt(selectedRow, 0).toString());
                    String courseName = tableModel.getValueAt(selectedRow, 1).toString();
                    String teacherName = tableModel.getValueAt(selectedRow, 2).toString();
                    String timeSlot = tableModel.getValueAt(selectedRow, 3).toString();
                    String room = tableModel.getValueAt(selectedRow, 4).toString();

                    selectComboBoxItem(courseComboBox, courseName);
                    selectComboBoxItem(teacherComboBox, teacherName);
                    selectComboBoxItem(timeSlotComboBox, timeSlot);
                    selectComboBoxItem(roomComboBox, room);
                }
            }
        });

        return panel;
    }

    private void loadCourses() {
        try {
            String sql = "SELECT c.*, d.name as dept_name FROM courses c " +
                        "JOIN departments d ON c.dept_id = d.id ORDER BY c.name";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                // Store the previously selected item
                CourseItem selectedItem = (CourseItem) courseComboBox.getSelectedItem();
                
                courseComboBox.removeAllItems();
                while (rs.next()) {
                    CourseItem item = new CourseItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("dept_name")
                    );
                    courseComboBox.addItem(item);
                    
                    // Restore the selection if it still exists
                    if (selectedItem != null && selectedItem.getId().equals(item.getId())) {
                        courseComboBox.setSelectedItem(item);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading courses: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTeachers() {
        try {
            String sql = "SELECT * FROM teachers ORDER BY name";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                // Store the previously selected item
                TeacherItem selectedItem = (TeacherItem) teacherComboBox.getSelectedItem();
                
                teacherComboBox.removeAllItems();
                while (rs.next()) {
                    TeacherItem item = new TeacherItem(
                        rs.getLong("id"),
                        rs.getString("name")
                    );
                    teacherComboBox.addItem(item);
                    
                    // Restore the selection if it still exists
                    if (selectedItem != null && selectedItem.getId().equals(item.getId())) {
                        teacherComboBox.setSelectedItem(item);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading teachers: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTimeSlots() {
        try {
            String sql = "SELECT * FROM timeslots ORDER BY start_time";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                // Store the previously selected item
                TimeSlotItem selectedItem = (TimeSlotItem) timeSlotComboBox.getSelectedItem();
                
                timeSlotComboBox.removeAllItems();
                while (rs.next()) {
                    Time startTime = rs.getTime("start_time");
                    Time endTime = rs.getTime("end_time");
                    TimeSlotItem item = new TimeSlotItem(
                        rs.getLong("id"),
                        LocalTime.parse(startTime.toString()),
                        LocalTime.parse(endTime.toString())
                    );
                    timeSlotComboBox.addItem(item);
                    
                    // Restore the selection if it still exists
                    if (selectedItem != null && selectedItem.getId().equals(item.getId())) {
                        timeSlotComboBox.setSelectedItem(item);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading time slots: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRooms() {
        try {
            String sql = "SELECT * FROM rooms ORDER BY room_number";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                // Store the previously selected item
                RoomItem selectedItem = (RoomItem) roomComboBox.getSelectedItem();
                
                roomComboBox.removeAllItems();
                while (rs.next()) {
                    RoomItem item = new RoomItem(
                        rs.getLong("id"),
                        rs.getString("room_number")
                    );
                    roomComboBox.addItem(item);
                    
                    // Restore the selection if it still exists
                    if (selectedItem != null && selectedItem.getId().equals(item.getId())) {
                        roomComboBox.setSelectedItem(item);
                    }
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading rooms: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            String sql = "SELECT cs.id, c.name as course_name, t.name as teacher_name, " +
                        "ts.start_time, ts.end_time, r.room_number " +
                        "FROM class_schedules cs " +
                        "JOIN courses c ON cs.course_id = c.id " +
                        "JOIN teachers t ON cs.teacher_id = t.id " +
                        "JOIN timeslots ts ON cs.timeslot_id = ts.id " +
                        "JOIN rooms r ON cs.room_id = r.id " +
                        "ORDER BY ts.start_time, c.name";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    String timeSlot = formatTimeSlot(
                        rs.getTime("start_time"),
                        rs.getTime("end_time")
                    );
                    
                    tableModel.addRow(new Object[]{
                        rs.getLong("id"),
                        rs.getString("course_name"),
                        rs.getString("teacher_name"),
                        timeSlot,
                        rs.getString("room_number")
                    });
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading schedules: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatTimeSlot(Time startTime, Time endTime) {
        return LocalTime.parse(startTime.toString()).format(timeFormatter) +
               " - " +
               LocalTime.parse(endTime.toString()).format(timeFormatter);
    }

    // Helper classes for combo boxes
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

    private static class TeacherItem {
        private final Long id;
        private final String name;

        public TeacherItem(Long id, String name) {
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

    private static class TimeSlotItem {
        private final Long id;
        private final LocalTime startTime;
        private final LocalTime endTime;
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        public TimeSlotItem(Long id, LocalTime startTime, LocalTime endTime) {
            this.id = id;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return startTime.format(formatter) + " - " + endTime.format(formatter);
        }
    }

    private static class RoomItem {
        private final Long id;
        private final String roomNumber;

        public RoomItem(Long id, String roomNumber) {
            this.id = id;
            this.roomNumber = roomNumber;
        }

        public Long getId() {
            return id;
        }

        @Override
        public String toString() {
            return roomNumber;
        }
    }

    private void addSchedule() {
        if (!validateForm()) {
            return;
        }

        try {
            CourseItem selectedCourse = (CourseItem) courseComboBox.getSelectedItem();
            TeacherItem selectedTeacher = (TeacherItem) teacherComboBox.getSelectedItem();
            TimeSlotItem selectedTimeSlot = (TimeSlotItem) timeSlotComboBox.getSelectedItem();
            RoomItem selectedRoom = (RoomItem) roomComboBox.getSelectedItem();

            // Check for schedule conflicts
            if (hasScheduleConflict(selectedTimeSlot.getId(), selectedRoom.getId(), null)) {
                JOptionPane.showMessageDialog(this,
                    "There is already a class scheduled in this room at this time",
                    "Schedule Conflict",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (hasTeacherConflict(selectedTeacher.getId(), selectedTimeSlot.getId(), null)) {
                JOptionPane.showMessageDialog(this,
                    "The selected teacher is already scheduled for another class at this time",
                    "Schedule Conflict",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO class_schedules (course_id, teacher_id, timeslot_id, room_id) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setLong(1, selectedCourse.getId());
                ps.setLong(2, selectedTeacher.getId());
                ps.setLong(3, selectedTimeSlot.getId());
                ps.setLong(4, selectedRoom.getId());
                ps.executeUpdate();
                
                clearForm();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Class schedule added successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error adding schedule: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSchedule() {
        if (selectedScheduleId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a schedule to update",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            CourseItem selectedCourse = (CourseItem) courseComboBox.getSelectedItem();
            TeacherItem selectedTeacher = (TeacherItem) teacherComboBox.getSelectedItem();
            TimeSlotItem selectedTimeSlot = (TimeSlotItem) timeSlotComboBox.getSelectedItem();
            RoomItem selectedRoom = (RoomItem) roomComboBox.getSelectedItem();

            // Check for schedule conflicts
            if (hasScheduleConflict(selectedTimeSlot.getId(), selectedRoom.getId(), selectedScheduleId)) {
                JOptionPane.showMessageDialog(this,
                    "There is already a class scheduled in this room at this time",
                    "Schedule Conflict",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (hasTeacherConflict(selectedTeacher.getId(), selectedTimeSlot.getId(), selectedScheduleId)) {
                JOptionPane.showMessageDialog(this,
                    "The selected teacher is already scheduled for another class at this time",
                    "Schedule Conflict",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "UPDATE class_schedules SET course_id = ?, teacher_id = ?, timeslot_id = ?, room_id = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setLong(1, selectedCourse.getId());
                ps.setLong(2, selectedTeacher.getId());
                ps.setLong(3, selectedTimeSlot.getId());
                ps.setLong(4, selectedRoom.getId());
                ps.setLong(5, selectedScheduleId);
                ps.executeUpdate();
                
                clearForm();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                    "Class schedule updated successfully",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "Error updating schedule: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSchedule() {
        if (selectedScheduleId == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a schedule to delete",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this schedule?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM class_schedules WHERE id = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setLong(1, selectedScheduleId);
                    ps.executeUpdate();
                    
                    clearForm();
                    refreshTable();
                    JOptionPane.showMessageDialog(this,
                        "Class schedule deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting schedule: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        courseComboBox.setSelectedIndex(-1);
        teacherComboBox.setSelectedIndex(-1);
        timeSlotComboBox.setSelectedIndex(-1);
        roomComboBox.setSelectedIndex(-1);
        selectedScheduleId = null;
        scheduleTable.clearSelection();
    }

    private boolean validateForm() {
        if (courseComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a course",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (teacherComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a teacher",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (timeSlotComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a time slot",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (roomComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a room",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean hasScheduleConflict(Long timeSlotId, Long roomId, Long excludeScheduleId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM class_schedules " +
                    "WHERE timeslot_id = ? AND room_id = ? " +
                    (excludeScheduleId != null ? "AND id != ?" : "");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, timeSlotId);
            ps.setLong(2, roomId);
            if (excludeScheduleId != null) {
                ps.setLong(3, excludeScheduleId);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private boolean hasTeacherConflict(Long teacherId, Long timeSlotId, Long excludeScheduleId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM class_schedules " +
                    "WHERE teacher_id = ? AND timeslot_id = ? " +
                    (excludeScheduleId != null ? "AND id != ?" : "");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setLong(1, teacherId);
            ps.setLong(2, timeSlotId);
            if (excludeScheduleId != null) {
                ps.setLong(3, excludeScheduleId);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private void selectComboBoxItem(JComboBox<?> comboBox, String value) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).toString().equals(value)) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }
} 