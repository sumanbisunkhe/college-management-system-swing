package dao;

import database.DatabaseConnection;
import model.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDao {
    public void add(Room room) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO rooms (room_number, capacity) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public void update(Room room) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE rooms SET room_number = ?, capacity = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setLong(3, room.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public Room getById(Long id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM rooms WHERE id = ?";
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

    public List<Room> getAll() throws SQLException, ClassNotFoundException {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(mapRow(rs));
            }
        }
        return rooms;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setCapacity(rs.getInt("capacity"));
        return room;
    }
} 