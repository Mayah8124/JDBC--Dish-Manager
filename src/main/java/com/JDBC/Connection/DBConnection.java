package com.JDBC.Connection;

import java.sql.*;

public class DBConnection {
    private final String USERNAME = "mini_dish_db_manager";
    private final String PASSWORD = "123456";
    private final String JDBC_URL = "jdbc:postgresql://localhost:5432/mini_dish_db";

    Connection getDBConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
