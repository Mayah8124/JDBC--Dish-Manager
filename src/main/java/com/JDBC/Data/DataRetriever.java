package com.JDBC.Data;

import com.JDBC.*;
import com.JDBC.Connection.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbConnection = new DBConnection();

    public Dish findDishById(int id) {
        String sql = """
                SELECT d.id AS dish_id,
                       d.name AS dish_name,
                       d.dish_type AS dish_type,
                       d.price AS dish_price,
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
                        Number dish_price = rs.getBigDecimal("dish_price");
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

    public List<Ingredient> findIngredients(int page, int size) {
        List<Ingredient> ingredientList = new ArrayList<>();

        String sql = """
                SELECT
                    i.id AS ingredient_id,
                    i.name AS ingredient_name,
                    i.price AS ingredient_price,
                    i.category AS ingredient_category
                FROM Ingredient i ORDER BY i.id LIMIT ? OFFSET ?;
                """;

        try (Connection conn = dbConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, page);
            ps.setInt(2, size);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int ingredient_id = rs.getInt("ingredient_id");
                    String ingredient_nom = rs.getString("ingredient_nom");
                    Double ingredient_price = rs.getDouble("ingredient_price");
                    int ingredient_category = rs.getInt("ingredient_category");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error while  " + e.getMessage());
            e.printStackTrace();
        }

        return ingredientList;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        String checkIngredientSQL = """
                    SELECT COUNT(*)
                        FROM Ingredient
                        Where name = ? AND dish_name = ?;
                """;

        String insertNewIngredientSQL = """
                    INSERT INTO Ingredient(id , name , price , category) 
                    VALUES (?, ?, ?, ?);
                """;

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement checkStmnt = conn.prepareStatement(checkIngredientSQL);
                    PreparedStatement insertStmnt = conn.prepareStatement(insertNewIngredientSQL)
                    ) {
                for (Ingredient ingredient : newIngredients) {
                    checkStmnt.setString(1, ingredient.getName());
                    checkStmnt.setInt(2, ingredient.getId());

                    ResultSet rs = checkStmnt.executeQuery();
                    rs.next();

                    if (rs.getInt(1) > 0) {
                        throw new RuntimeException("Ingredient already exists : " + ingredient.getName());
                    };

                    insertStmnt.setInt(1, ingredient.getId());
                    insertStmnt.setString(2, ingredient.getName());
                    insertStmnt.setBigDecimal(3, BigDecimal.valueOf(ingredient.getPrice()));
                    insertStmnt.setString(4, String.valueOf(ingredient.getCategory()));
                    insertStmnt.setInt(5, ingredient.getDish().getId());

                    insertStmnt.executeUpdate();
                }

                conn.commit();

                return newIngredients;

            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while create ingredient : " + e.getMessage());
        }
    }

    public Dish saveDish(Dish dishToSave) {
        String checkDishSQL = """
                SELECT COUNT(*)
                    FROM Dish
                    WHERE id = ?;
                """;

        String insertDishSQL = """
                INSERT INTO Dish(id , name , dish_type)
                    VALUES (?, ?, ?);
                """;

        String updateDishSQL = """
                UPDATE Dish
                    SET name = ?, dish_type = ?
                    WHERE id = ?;
                """;

        String dissociateIngredientSQL = """
                UPDATE Ingredient
                    SET dish_id = NULL
                    WHERE id = ?;
                """;

        String associateIngredientSQL = """
                UPDATE Ingredient
                    SET dish_id = ?
                    WHERE id = ?;
                """;

        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (
                    PreparedStatement checkStmnt = conn.prepareStatement(checkDishSQL);
                    PreparedStatement insertStmnt = conn.prepareStatement(insertDishSQL);
                    PreparedStatement updateStmnt = conn.prepareStatement(updateDishSQL);
                    PreparedStatement dissociateStmnt = conn.prepareStatement(dissociateIngredientSQL);
                    PreparedStatement associateStmnt = conn.prepareStatement(associateIngredientSQL)
                    ) {
                checkStmnt.setInt(1, dishToSave.getId());
                ResultSet rs = checkStmnt.executeQuery();
                rs.next();

                boolean exists = rs.getInt(1) > 0;

                if (!exists) {
                    insertStmnt.setInt(1, dishToSave.getId());
                    insertStmnt.setString(2, dishToSave.getName());
                    insertStmnt.setString(3, String.valueOf(dishToSave.getDishType()));
                    insertStmnt.executeUpdate();
                } else {
                    updateStmnt.setString(1, dishToSave.getName());
                    updateStmnt.setString(2, String.valueOf(dishToSave.getDishType()));
                    updateStmnt.setInt(3, dishToSave.getId());
                    updateStmnt.executeUpdate();
                }

                dissociateStmnt.setInt(1, dishToSave.getId());
                dissociateStmnt.executeUpdate();

                if (dishToSave.getIngredients() != null) {
                    for (Ingredient ingredient : dishToSave.getIngredients()) {
                        associateStmnt.setInt(1, dishToSave.getId());
                        associateStmnt.setInt(2, ingredient.getId());
                        associateStmnt.executeUpdate();
                    }
                }

                conn.commit();

                return dishToSave;
            } catch (RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while save ingredient : " + e.getMessage());
        }
    }

    public List<Dish> findDishsByIngredientName(String ingredientName) {
        List <Dish> dishList = new ArrayList<>();

        String sql = """
                    SELECT DISTINCT
                                d.id AS dish_id,
                                d.name AS dish_name,
                                d.dish_type
                            FROM Dish d
                            JOIN Ingredient i ON d.id = i.dish_id
                            WHERE i.name ILIKE ?;
                """;

        try (Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,"%" + ingredientName + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("dish_id"));
                dish.setName(rs.getString("dish_name"));
                dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));

                dishList.add(dish);
            }
        }catch (SQLException e) {
            throw new RuntimeException("error while searching dishes : ", e);
        }
        return dishList;
    }

    public List<Ingredient> findIngredientsByCriteria(String IngredientName, CategoryEnum Category, String dishName, int page, int size) {
        List <Ingredient> ingredientList = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
                SELECT
                            i.id,
                            i.name,
                            i.price,
                            i.category,
                            d.id AS dish_id,
                            d.name AS dish_name
                        FROM Ingredient i
                        LEFT JOIN Dish d ON i.dish_id = d.id
                        WHERE 1 = 1
                """);

        List<Object> params = new ArrayList<>();

        if (IngredientName != null && !IngredientName.isBlank()) {
            sql.append(" AND i.name ILIKE ? ");
            params.add("%" + IngredientName + "%");
        }

        if (Category != null) {
            sql.append(" AND i.category = ? ");
            params.add(Category.name());
        }

        if (dishName != null && !dishName.isBlank()) {
            sql.append(" AND d.name ILIKE ? ");
            params.add("%" + dishName + "%");
        }

        sql.append(" ORDER BY i.id LIMIT ? OFFSET ? ");
        params.add(size);
        params.add(page * size);

        try (Connection conn = dbConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(1 + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(rs.getInt("id"));
                ingredient.setName(rs.getString("name"));
                ingredient.setPrice(rs.getDouble("price"));
                ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));

                ingredientList.add(ingredient);
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error while searching ingredients : ", e);
        }
        return ingredientList;
    }
}
