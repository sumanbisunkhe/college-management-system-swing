package model;

public class ClassSchedule {
    private Long id;
    private Long courseId;
    private Long teacherId;
    private Long TimeSlotId;
    private Long RoomId;

    public ClassSchedule() {
    }

    public ClassSchedule(Long id, Long courseId, Long teacherId, Long timeSlotId, Long roomId) {
        this.id = id;
        this.courseId = courseId;
        this.teacherId = teacherId;
        TimeSlotId = timeSlotId;
        RoomId = roomId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getTimeSlotId() {
        return TimeSlotId;
    }

    public void setTimeSlotId(Long timeSlotId) {
        TimeSlotId = timeSlotId;
    }

    public Long getRoomId() {
        return RoomId;
    }

    public void setRoomId(Long roomId) {
        RoomId = roomId;
    }
}
