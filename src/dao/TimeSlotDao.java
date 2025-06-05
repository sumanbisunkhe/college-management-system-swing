package dao;

import database.DatabaseConnection;
import model.TimeSlot;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDao {
    public void add(TimeSlot timeSlot) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO timeslots (start_time, end_time) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setTime(1, Time.valueOf(timeSlot.getStartTime()));
            stmt.setTime(2, Time.valueOf(timeSlot.getEndTime()));
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    timeSlot.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public void update(TimeSlot timeSlot) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE timeslots SET start_time = ?, end_time = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTime(1, Time.valueOf(timeSlot.getStartTime()));
            stmt.setTime(2, Time.valueOf(timeSlot.getEndTime()));
            stmt.setLong(3, timeSlot.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM timeslots WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public TimeSlot getById(Long id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM timeslots WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<TimeSlot> getAll() throws SQLException, ClassNotFoundException {
        List<TimeSlot> timeSlots = new ArrayList<>();
        String sql = "SELECT * FROM timeslots ORDER BY start_time";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                timeSlots.add(mapRow(rs));
            }
        }
        return timeSlots;
    }

    private TimeSlot mapRow(ResultSet rs) throws SQLException {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(rs.getLong("id"));
        Time startTime = rs.getTime("start_time");
        Time endTime = rs.getTime("end_time");
        timeSlot.setStartTime(startTime.toLocalTime());
        timeSlot.setEndTime(endTime.toLocalTime());
        return timeSlot;
    }
} 