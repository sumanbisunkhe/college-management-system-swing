package dao;

import database.DatabaseConnection;
import model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDao {

//    Enroll Student in Course:
//    Assign students to specific courses.

    public void save(Enrollment enrollment) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO enrollments (student_id, course_id, semester, grade) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, enrollment.getStudentId());
            ps.setLong(2, enrollment.getCourseId());
            ps.setString(3, enrollment.getSemester());
            ps.setString(4, enrollment.getGrade());
            ps.executeUpdate();
        }
    }

//    View Student Enrollments

    public void getAll() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM enrollments";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                System.out.println("\nID: " + rs.getLong("id"));
                System.out.println("Student ID: " + rs.getLong("student_id"));
                System.out.println("Course ID: " + rs.getLong("course_id"));
                System.out.println("Semester: " + rs.getString("semester"));
                System.out.println("Grade: " + rs.getString("grade"));
            }
        }
    }

    public Enrollment getEnrollmentById(long id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Enrollment(
                    rs.getLong("id"),
                    rs.getLong("student_id"),
                    rs.getLong("course_id"),
                    rs.getString("semester"),
                    rs.getString("grade")
                );
            }
        }
        return null;
    }

//    Update Enrollment:
//    Modify course enrollments, including grades.

//    Delete Enrollment:
//    Unenroll a student from a course.
//
//    Record Grades:
//    Update or assign grades for completed courses.

    public Enrollment getEnrollmentByStudentId(long studentId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Enrollment(
                    rs.getLong("id"),
                    rs.getLong("student_id"),
                    rs.getLong("course_id"),
                    rs.getString("semester"),
                    rs.getString("grade")
                );
            }
        }
        return null;
    }

    public void update(Long id, Enrollment enrollment) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE enrollments SET student_id = ?, course_id = ?, semester = ?, grade = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, enrollment.getStudentId());
            ps.setLong(2, enrollment.getCourseId());
            ps.setString(3, enrollment.getSemester());
            ps.setString(4, enrollment.getGrade());
            ps.setLong(5, id);
            ps.executeUpdate();
        }
    }

    public void updateGrade(Long id, String grade) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE enrollments SET grade = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, grade);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(Long id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM enrollments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    public boolean existsEnrollment(Long studentId, Long courseId, String semester) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND course_id = ? AND semester = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ps.setLong(2, courseId);
            ps.setString(3, semester);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Enrollment> getEnrollmentsByStudentId(Long studentId) throws SQLException, ClassNotFoundException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                enrollments.add(new Enrollment(
                    rs.getLong("id"),
                    rs.getLong("student_id"),
                    rs.getLong("course_id"),
                    rs.getString("semester"),
                    rs.getString("grade")
                ));
            }
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByCourseId(Long courseId) throws SQLException, ClassNotFoundException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, courseId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                enrollments.add(new Enrollment(
                    rs.getLong("id"),
                    rs.getLong("student_id"),
                    rs.getLong("course_id"),
                    rs.getString("semester"),
                    rs.getString("grade")
                ));
            }
        }
        return enrollments;
    }
}
