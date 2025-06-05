package dao;

import database.DatabaseConnection;
import model.Student;

import java.sql.*;
import java.time.LocalDate;

public class StudentDao {

    public void save(Student student) {
        String sql = "INSERT INTO students(name, dob, email) VALUES(?,?,?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);


            stmt.setString(1, student.getName());
            stmt.setDate(2, Date.valueOf(student.getDob()));
            stmt.setString(3, student.getEmail());
            stmt.executeUpdate();
            System.out.println("\n✔ Done");


        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Student findById(Long id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM students WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Student student = new Student();
            student.setId(rs.getLong("id"));
            student.setName(rs.getString("name"));
            student.setDob(LocalDate.parse(rs.getString("dob")));
            student.setEmail(rs.getString("email"));
            return student;

        } else {
            return null;
        }


    }

    public void findAll() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM students";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        System.out.println("Students List");
        while (rs.next()) {
            System.out.println("\nID: " + rs.getLong("id"));
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("DOB: " + rs.getString("dob"));
            System.out.println("Email: " + rs.getString("email"));
        }
        System.out.println("\n✔ Done");
    }

    public void update(Long id, Student student) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE students SET name = ?, dob = ?,email=? WHERE id = ?";

        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, student.getName());
        stmt.setDate(2, Date.valueOf(student.getDob()));
        stmt.setString(3, student.getEmail());
        stmt.setLong(4, id);
        int i = stmt.executeUpdate();
        if (i > 0) {
            System.out.println("\n✔ Done");
        }

    }

    public void delete(Long id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM students WHERE id = ?";
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setLong(1, id);
        int i = stmt.executeUpdate();
        if (i > 0) {
            System.out.println("\n✔ Done");

        }
    }

    public boolean existsByEmail(String email) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM students WHERE email = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }

}
