package model;

public class Room {
    private Long id;
    private String roomNumber;
    private int capacity;

    public Room() {}

    public Room(Long id, String roomNumber, int capacity) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return roomNumber + " (Capacity: " + capacity + ")";
    }
} 