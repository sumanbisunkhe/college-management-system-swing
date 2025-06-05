package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() throws SQLException, ClassNotFoundException {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Department Table
            String createDepartmentTable = """
                    CREATE TABLE IF NOT EXISTS departments (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE
                    );
                    """;

            // Student Table
            String createStudentTable = """
                    CREATE TABLE IF NOT EXISTS students (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        dob DATE NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE
                    );
                    """;

            // Teacher Table
            String createTeacherTable = """
                    CREATE TABLE IF NOT EXISTS teachers (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE
                    );
                    """;

            // Course Table
            String createCourseTable = """
                    CREATE TABLE IF NOT EXISTS courses (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        credit VARCHAR(20) NOT NULL,
                        dept_id INTEGER REFERENCES departments(id)
                    );
                    """;

            // Drop existing enrollments table if it exists
            stmt.execute("DROP TABLE IF EXISTS enrollments");

            // Create new enrollments table
            String createEnrollmentTable = """
                    CREATE TABLE enrollments (
                        id SERIAL PRIMARY KEY,
                        student_id INTEGER REFERENCES students(id),
                        course_id INTEGER REFERENCES courses(id),
                        semester VARCHAR(20) NOT NULL,
                        grade VARCHAR(2) NOT NULL,
                        UNIQUE(student_id, course_id, semester)
                    );
                    """;

            // Drop existing tables if they exist to avoid conflicts
            stmt.execute("DROP TABLE IF EXISTS class_schedules");
            stmt.execute("DROP TABLE IF EXISTS timeslots");
            stmt.execute("DROP TABLE IF EXISTS rooms");

            // TimeSlot Table
            String createTimeSlotTable = """
                    CREATE TABLE timeslots (
                        id SERIAL PRIMARY KEY,
                        start_time TIME NOT NULL,
                        end_time TIME NOT NULL,
                        UNIQUE(start_time, end_time)
                    );
                    """;

            // Room Table
            String createRoomTable = """
                    CREATE TABLE rooms (
                        id SERIAL PRIMARY KEY,
                        room_number VARCHAR(10) NOT NULL UNIQUE,
                        capacity INT NOT NULL DEFAULT 30
                    );
                    """;

            // ClassSchedule Table
            String createClassScheduleTable = """
                    CREATE TABLE class_schedules (
                        id SERIAL PRIMARY KEY,
                        course_id INTEGER NOT NULL,
                        teacher_id INTEGER NOT NULL,
                        timeslot_id INTEGER NOT NULL,
                        room_id INTEGER NOT NULL,
                        FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
                        FOREIGN KEY (teacher_id) REFERENCES teachers(id) ON DELETE CASCADE,
                        FOREIGN KEY (timeslot_id) REFERENCES timeslots(id) ON DELETE CASCADE,
                        FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
                        UNIQUE (timeslot_id, room_id),
                        UNIQUE (teacher_id, timeslot_id)
                    );
                    """;

            // Execute table creation in proper order
            stmt.execute(createDepartmentTable);
            stmt.execute(createStudentTable);
            stmt.execute(createTeacherTable);
            stmt.execute(createCourseTable);
            stmt.execute(createEnrollmentTable);
            stmt.execute(createTimeSlotTable);
            stmt.execute(createRoomTable);
            stmt.execute(createClassScheduleTable);

            // Insert default data after all tables are created
            stmt.execute("""
                INSERT INTO timeslots (start_time, end_time)
                SELECT time_slot.start_time, time_slot.end_time
                FROM (VALUES 
                    ('08:00'::time, '09:30'::time),
                    ('09:45'::time, '11:15'::time),
                    ('11:30'::time, '13:00'::time),
                    ('14:00'::time, '15:30'::time),
                    ('15:45'::time, '17:15'::time)
                ) AS time_slot(start_time, end_time)
                WHERE NOT EXISTS (
                    SELECT 1 FROM timeslots 
                    WHERE timeslots.start_time = time_slot.start_time 
                    AND timeslots.end_time = time_slot.end_time
                )
            """);

            stmt.execute("""
                INSERT INTO rooms (room_number, capacity)
                SELECT room_data.room_number, room_data.capacity
                FROM (VALUES 
                    ('101', 30),
                    ('102', 35),
                    ('103', 25),
                    ('201', 40),
                    ('202', 30),
                    ('203', 35)
                ) AS room_data(room_number, capacity)
                WHERE NOT EXISTS (
                    SELECT 1 FROM rooms 
                    WHERE rooms.room_number = room_data.room_number
                )
            """);
        }
    }
}