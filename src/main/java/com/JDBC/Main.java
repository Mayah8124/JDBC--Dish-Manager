package com.JDBC;

import com.JDBC.Connection.DBConnection;
import com.JDBC.Data.DataRetriever;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {

        // test Dish findDishByID(integer id)
        DataRetriever dr = new DataRetriever();
        Dish dishA = new Dish(1,"poulet rotie",DishTypeEnum.START, 120.00);
        System.out.println(dr.findDishById(Integer.parseInt(args[0])));

        // test saveDish

        System.setProperty("JDBC_URL", "jdbc:postgresql://localhost:5432/mini_dish_db");
        System.setProperty("JDBC_USER", "mini_dish_db_manager");
        System.setProperty("PASSWORD", "123456");

        DBConnection db = new DBConnection();

        Connection connection = db.getConnection();

        if (connection != null) {
            System.out.println("Connection successful");
        }
        else {
            System.out.println("Connection failed");
        }

        db.closeConnection(connection);
    }
}