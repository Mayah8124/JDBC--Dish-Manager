package com.JDBC.Data;

import com.JDBC.*;
import com.JDBC.Connection.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {
    private List<DishIngredient> findDishIngredientsByDishId(Connection conn, Integer dishId)
            throws SQLException {

        String sql = """
        SELECT di.id,
               di.quantity_required,
               di.unit,
               i.id   AS ingredient_id,
               i.name,
               i.price,
               i.category
        FROM dish_ingredient di
        JOIN ingredient i ON i.id = di.id_ingredient
        WHERE di.id_dish = ?
    """;

        List<DishIngredient> result = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price")
                );

                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("id"));
                di.setIngredient(ingredient);
                di.setQuantity_required(
                        rs.getObject("quantity_required") == null
                                ? null
                                : rs.getDouble("quantity_required")
                );
                di.setUnit(UnitType.valueOf(rs.getString("unit")));

                result.add(di);
            }
        }
        return result;
    }


    public Dish findDishById(Integer id) {
        String dishSql = """
        SELECT id, name, dish_type, selling_price
        FROM dish
        WHERE id = ?
    """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(dishSql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Dish not found: " + id);
            }

            Dish dish = new Dish();
            dish.setId(rs.getInt("id"));
            dish.setName(rs.getString("name"));
            dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
            dish.setPrice(rs.getObject("selling_price") == null
                    ? null : rs.getDouble("selling_price"));

            dish.setDishIngredients(findDishIngredientsByDishId(conn, id));

            return dish;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void insertDishIngredients(Connection conn,
                                      Integer dishId,
                                      List<DishIngredient> dishIngredients)
            throws SQLException {

        if (dishIngredients == null || dishIngredients.isEmpty()) {
            return;
        }

        String sql = """
        INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
        VALUES (?, ?, ?, ?::unit_type)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : dishIngredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, di.getIngredient().getId());

                if (di.getQuantity_required() != null) {
                    ps.setDouble(3, di.getQuantity_required());
                } else {
                    ps.setNull(3, Types.DOUBLE);
                }

                ps.setString(4, di.getUnit().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void deleteDishIngredients(Connection conn, Integer dishId)
            throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                "DELETE FROM dish_ingredient WHERE id_dish = ?")) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }



    Dish saveDish(Dish toSave) {

        String upsertDishSql = """
        INSERT INTO dish (id, name, dish_type, selling_price)
        VALUES (?, ?, ?::dish_type, ?)
        ON CONFLICT (id) DO UPDATE
        SET name = EXCLUDED.name,
            dish_type = EXCLUDED.dish_type,
            selling_price = EXCLUDED.selling_price
        RETURNING id
    """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Integer dishId;
            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {

                ps.setInt(1, toSave.getId() != null
                        ? toSave.getId()
                        : getNextSerialValue(conn, "dish", "id"));

                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getDishType().name());

                if (toSave.getPrice() != null) {
                    ps.setDouble(4, toSave.getPrice());
                } else {
                    ps.setNull(4, Types.DOUBLE);
                }

                ResultSet rs = ps.executeQuery();
                rs.next();
                dishId = rs.getInt(1);
            }

            deleteDishIngredients(conn, dishId);
            insertDishIngredients(conn, dishId, toSave.getDishIngredients());

            conn.commit();
            return findDishById(dishId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) {
        if (newIngredients == null || newIngredients.isEmpty()) {
            return List.of();
        }
        List<Ingredient> savedIngredients = new ArrayList<>();
        DBConnection dbConnection = new DBConnection();
        Connection conn = dbConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String insertSql = """
                        INSERT INTO ingredient (id, name, category, price, required_quantity)
                        VALUES (?, ?, ?::ingredient_category, ?, ?)
                        RETURNING id
                    """;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                for (Ingredient ingredient : newIngredients) {
                    if (ingredient.getId() != null) {
                        ps.setInt(1, ingredient.getId());
                    } else {
                        ps.setInt(1, getNextSerialValue(conn, "ingredient", "id"));
                    }
                    ps.setString(2, ingredient.getName());
                    ps.setString(3, ingredient.getCategory().name());
                    ps.setDouble(4, ingredient.getPrice());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        int generatedId = rs.getInt(1);
                        ingredient.setId(generatedId);
                        savedIngredients.add(ingredient);
                    }
                }
                conn.commit();
                return savedIngredients;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(conn);
        }
    }


    private void detachIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients)
            throws SQLException {
        if (ingredients == null || ingredients.isEmpty()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE ingredient SET dish_id = NULL WHERE dish_id = ?")) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }
            return;
        }

        String baseSql = """
                    UPDATE ingredient
                    SET id_dish = NULL
                    WHERE id_dish = ? AND id NOT IN (%s)
                """;

        String inClause = ingredients.stream()
                .map(i -> "?")
                .collect(Collectors.joining(","));

        String sql = String.format(baseSql, inClause);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            int index = 2;
            for (Ingredient ingredient : ingredients) {
                ps.setInt(index++, ingredient.getId());
            }
            ps.executeUpdate();
        }
    }

    private void attachIngredients(Connection conn, Integer dishId, List<Ingredient> ingredients)
            throws SQLException {

        if (ingredients == null || ingredients.isEmpty()) {
            return;
        }

        String attachSql = """
                    UPDATE ingredient
                    SET dish_id = ?
                    WHERE id = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(attachSql)) {
            for (Ingredient ingredient : ingredients) {
                ps.setInt(1, dishId);
                ps.setInt(2, ingredient.getId());
                ps.addBatch(); // Can be substitute ps.executeUpdate() but bad performance
            }
            ps.executeBatch();
        }
    }

    private List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {

        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishIngredient> dishIngredients = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    """
                    SELECT di.id,
                           di.quantity_required,
                           di.unit,
                           i.id   AS ingredient_id,
                           i.name,
                           i.price,
                           i.category
                    FROM dish_ingredient di
                    JOIN ingredient i ON i.id = di.id_ingredient
                    WHERE di.id_dish = ?
                    """
            );

            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Ingredient ingredient = new Ingredient(
                        rs.getInt("ingredient_id"),
                        rs.getString("name"),
                        CategoryEnum.valueOf(rs.getString("category")),
                        rs.getDouble("price")
                );

                DishIngredient di = new DishIngredient();
                di.setId(rs.getInt("id"));
                di.setIngredient(ingredient);
                di.setQuantity_required(
                        rs.getObject("quantity_required") == null
                                ? null
                                : rs.getDouble("quantity_required")
                );
                di.setUnit(UnitType.valueOf(rs.getString("unit")));

                dishIngredients.add(di);
            }

            return dishIngredients;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dbConnection.closeConnection(connection);
        }
    }



    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }
}