package dao;

import database.DatabaseConnection;
import model.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherDao {
    public void addTeacher(Teacher teacher) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO teachers(name,email) VALUES(?,?)";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, teacher.getName());
        ps.setString(2, teacher.getEmail());
        ps.executeUpdate();
        ps.close();
        conn.close();

    }

    public Teacher getTeacherById(Long id) throws SQLException, ClassNotFoundException {
        String sql = "select * from teachers where id=?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setLong(1, id);
        return getTeacher(preparedStatement);
    }

    public Teacher getTeacherByEmail(String email) throws SQLException, ClassNotFoundException {
        String sql = "select * from teachers where email=?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, email);
        return getTeacher(preparedStatement);
    }

    public void getAllTeachers() throws SQLException, ClassNotFoundException {

        String sql = "select * from teachers";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();
        System.out.println("Teachers List");
        while (rs.next()) {
            System.out.println("\nID: "+rs.getLong("id"));
            System.out.println("Name: "+rs.getString("name"));
            System.out.println("Email: "+rs.getString("email"));
        }
        rs.close();
        preparedStatement.close();
        conn.close();

    }

    public void updateTeacher(Teacher teacher) throws SQLException, ClassNotFoundException {
        String sql = "update teachers set name=?,email=? where id=?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, teacher.getName());
        preparedStatement.setString(2, teacher.getEmail());
        preparedStatement.setLong(3, teacher.getId());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        conn.close();
    }

    public void deleteTeacher(Long id) throws SQLException, ClassNotFoundException {
        String sql = "delete from teachers where id=?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setLong(1, id);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        conn.close();
    }


    private Teacher getTeacher(PreparedStatement preparedStatement) throws SQLException {
        ResultSet rs = preparedStatement.executeQuery();
        if (rs.next()) {
            Teacher teacher = new Teacher();
            teacher.setId(rs.getLong("id"));
            teacher.setName(rs.getString("name"));
            teacher.setEmail(rs.getString("email"));
            return teacher;
        } else {
            return null;
        }
    }


}
