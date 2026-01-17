package com.JDBC.Connection;

import java.sql.*;

public class DBConnection {
    public Connection getConnection() {
        try {
            String jdbcURl = System.getenv("JDBC_URl"); //
            String user = System.getenv("USER"); //mini_dish_db_manager
            String password = System.getenv("PASSWORD"); //123456
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/mini_dish_db", "postgres", "nekoko");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
