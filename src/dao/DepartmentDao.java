package dao;

import database.DatabaseConnection;
import model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepartmentDao {
    public void save(Department department) throws SQLException, ClassNotFoundException {
        String sql = "insert into departments(name) values(?)";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, department.getName());
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public Department findDepartmentById(Long id) throws SQLException, ClassNotFoundException {
        String sql = "select * from departments where id =?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Department department = new Department();
            department.setId(rs.getLong("id"));
            department.setName(rs.getString("name"));
            return department;
        } else {
            return null;
        }

    }

    public Department findDepartmentByName(String name) throws SQLException, ClassNotFoundException {
        String sql = "select * from departments where name =?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            Department department = new Department();
            department.setId(rs.getLong("id"));
            department.setName(rs.getString("name"));
            return department;
        } else {
            return null;
        }
    }

    public void findAll() throws SQLException, ClassNotFoundException {
        String sql = "select * from departments";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        System.out.println("\nDepartment List");
        while (rs.next()) {
            System.out.println("\nID: " + rs.getLong("id"));
            System.out.println("Name: " + rs.getString("name"));

        }
        System.out.println("\n✔ Done");
        rs.close();
        ps.close();
        conn.close();

    }

    public void update(Long id, Department department) throws SQLException, ClassNotFoundException {
        String sql = "update departments set name = ? where id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, department.getName());
        ps.setLong(2, id);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }

    public  void delete(Long id) throws SQLException, ClassNotFoundException {
        String sql = "delete from departments where id = ?";
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, id);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }


}
