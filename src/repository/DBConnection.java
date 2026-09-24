package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "JDBC:mysql://localhost:3306/cricket_franchise_db";

    private static final String USER = "root";

    private static final String PASSWORD = "Your Password";

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}