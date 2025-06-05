package dao;

import database.DatabaseConnection;
import model.Courses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseDao {

    public void addCourse(Courses courses) throws SQLException, ClassNotFoundException {
        String sql = "insert into courses(name,credit,dept_id) values(?,?,?)";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, courses.getName());
        ps.setString(2, courses.getCredit());
        ps.setLong(3, courses.getDeptId());
        ps.executeUpdate();
        conn.close();
        ps.close();
    }

    public Courses getCourseById(Long id) throws SQLException, ClassNotFoundException {
        String sql = "select * from courses where id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        return getCourses(rs);
    }

    public Courses getCourseByName(String name) throws SQLException, ClassNotFoundException {
        String sql = "select * from courses where name = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        return getCourses(rs);
    }

    public Courses getCourses(ResultSet rs) throws SQLException {
        if (rs.next()) {
            Courses courses = new Courses();
            courses.setId(rs.getLong("id"));
            courses.setName(rs.getString("name"));
            courses.setCredit(rs.getString("credit"));
            courses.setDeptId(rs.getLong("dept_id"));
            return courses;
        } else {
            return null;
        }
    }

    public void getAllCourses() throws SQLException, ClassNotFoundException {
        String sql = "select * from courses";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            System.out.println("\nID: " + rs.getLong("id"));
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Credit: " + rs.getString("credit"));
            System.out.println("DeptId: " + rs.getLong("dept_id"));
        }
        conn.close();
        ps.close();
    }

    public void updateCourse(Courses courses) throws SQLException, ClassNotFoundException {
        String sql = "update courses set name = ?, credit = ?, dept_id = ? where id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, courses.getName());
        ps.setString(2, courses.getCredit());
        ps.setLong(3, courses.getDeptId());
        ps.setLong(4, courses.getId());
        ps.executeUpdate();
        conn.close();
        ps.close();
    }
    public void deleteCourse(Long id) throws SQLException, ClassNotFoundException {
        String sql = "delete from courses where id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
        conn.close();
        ps.close();
    }

}
