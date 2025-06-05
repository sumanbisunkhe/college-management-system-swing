package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String url = "jdbc:postgresql://localhost:5432/college_db";
    private static final String username = "postgres";
    private static final String password = "#Aa1*****";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(url, username, password);
    }
}