package com.JDBC.Data;

import com.JDBC.*;
import com.JDBC.Connection.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection = new DBConnection();

    public Dish findDishById(int id) {
        String sql = """
                SELECT d.id AS dish_id,
                       d.name AS dish_name,
                       d.dish_type AS dish_type,
                       i.id AS ingredient_id,
                       i.name AS ingredient_name,
                       i.category AS ingredient_category
                FROM Dish d
                         LEFT JOIN Ingredient i ON d.id = i.dish_id
                        WHERE d.id = ?;
                
                """;

        Dish dish = null;

        try (Connection conn = dbConnection.getConnection())  {
            PreparedStatement ps = conn.prepareStatement(sql);{
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int dish_id = rs.getInt("dish_id");
                        String dish_name = rs.getString("dish_name");
                        String dish_type = rs.getString("dish_type");
                        int ingredient_id = rs.getInt("ingredient_id");
                        String ingredient_nom = rs.getString("ingredient_nom");
                        int ingredient_category = rs.getInt("ingredient_category");
                    }
                }
            };
        } catch (SQLException e) {
            System.err.println("Error while finding dish by id " + e.getMessage());
            e.printStackTrace();
        }
        return dish;
    }

    List<Ingredient> findIngredients(int page, int size) {
        throw new RuntimeException("Not implemented yet");
    }

    List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        throw new RuntimeException("Not implemented yet");
    }

    Dish saveDish(Dish dishToSave) {
        throw new RuntimeException("Not implemented yet");
    }

    List<Dish> findDishsByIngredientName(String ingredientName) {
        throw new RuntimeException("Not implemented yet");
    }

    List<Ingredient> findIngredientsByCriteria(String IngredientName, CategoryEnum Category, String dishName, int page, int size) {
        throw new RuntimeException("Not implemented yet");
    }
}
