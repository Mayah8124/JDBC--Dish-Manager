package com.JDBC.Connection;

import java.sql.*;

public class DBConnection {
    public Connection getConnection() throws SQLException {

        Connection connection = null;

        String url = System.getenv("JDBC_URL");
        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Error while connecting : " + e.getMessage());
        }

        return connection;
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed successfully");
            } catch (SQLException e) {
                System.err.println("Error while closing connexion : " + e.getMessage());
            }
        }
    }
}
