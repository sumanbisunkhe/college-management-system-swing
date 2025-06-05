package model;

import java.time.LocalTime;

public class TimeSlot {
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeSlot() {}

    public TimeSlot(Long id, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return startTime.toString() + " - " + endTime.toString();
    }
} 