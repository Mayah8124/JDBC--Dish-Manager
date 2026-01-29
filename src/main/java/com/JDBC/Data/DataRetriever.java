package com.JDBC.Data;

import com.JDBC.*;
import com.JDBC.Connection.DBConnection;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataRetriever {
    private List<DishIngredient> findDishIngredientsByDishId(Connection conn, Integer dishId)
            throws SQLException {

        final String sql = """
        SELECT
            di.id                 AS dish_ingredient_id,
            di.quantity_required,
            di.unit,
            i.id                  AS ingredient_id,
            i.name                AS ingredient_name,
            i.price               AS ingredient_price,
            i.category            AS ingredient_category
        FROM dish_ingredient di
        JOIN ingredient i ON i.id = di.id_ingredient
        WHERE di.id_dish = ?
    """;

        List<DishIngredient> dishIngredients = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Ingredient ingredient = new Ingredient(
                            rs.getInt("ingredient_id"),
                            rs.getString("ingredient_name"),
                            CategoryEnum.valueOf(rs.getString("ingredient_category")),
                            rs.getDouble("ingredient_price")
                    );

                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setId(rs.getInt("dish_ingredient_id"));
                    dishIngredient.setIngredient(ingredient);

                    Double quantityRequired = rs.getDouble("quantity_required");
                    dishIngredient.setQuantity_required(
                            rs.wasNull() ? null : quantityRequired
                    );

                    dishIngredient.setUnit(
                            UnitType.valueOf(rs.getString("unit"))
                    );

                    dishIngredients.add(dishIngredient);
                }
            }
        }

        return dishIngredients;
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
                        INSERT INTO ingredient (id, name, category, price)
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
    
    public Ingredient findIngredientById(Integer id) {
              String ingredientSql = """
              SELECT id, name, price, category
              FROM ingredient
              WHERE id = ?
          """;

                try (Connection conn = new DBConnection().getConnection();

                     PreparedStatement ps = conn.prepareStatement(ingredientSql)) {

                    ps.setInt(1, id);
                    ResultSet rs = ps.executeQuery();

                    if (!rs.next()) {
                        throw new RuntimeException("ingredient not found: " + id);
                    }

                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredient.setPrice(rs.getDouble("price"));

                    return ingredient;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
    }


    public Ingredient saveIngredient(Ingredient toSave) {

        String upsertIngredientSql = """
        INSERT INTO ingredient (id, name, category, price)
        VALUES (?, ?, ?::ingredient_category, ?)
        ON CONFLICT (id) DO UPDATE
        SET name = EXCLUDED.name,
            category = EXCLUDED.category,
            price = EXCLUDED.price
        RETURNING id
    """;

        String insertStockMovementSql = """
        INSERT INTO stock_movement
            (id, id_ingredient, quantity, unit, type, creation_datetime)
        VALUES (?, ?, ?, ?::unit_type, ?, ?)
        ON CONFLICT (id) DO NOTHING
    """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Integer ingredientId;

            try (PreparedStatement ps = conn.prepareStatement(upsertIngredientSql)) {

                ps.setInt(1,
                        toSave.getId() != null
                                ? toSave.getId()
                                : getNextSerialValue(conn, "ingredient", "id")
                );
                ps.setString(2, toSave.getName());
                ps.setString(3, toSave.getCategory().name());

                if (toSave.getPrice() != null) {
                    ps.setDouble(4, toSave.getPrice());
                } else {
                    ps.setNull(4, Types.DOUBLE);
                }

                ResultSet rs = ps.executeQuery();
                rs.next();
                ingredientId = rs.getInt(1);
            }

            if (toSave.getStockMovementList() != null
                    && !toSave.getStockMovementList().isEmpty()) {

                try (PreparedStatement ps = conn.prepareStatement(insertStockMovementSql)) {

                    for (StockMovement sm : toSave.getStockMovementList()) {

                        ps.setInt(1, sm.getId());
                        ps.setInt(2, ingredientId);

                        ps.setDouble(3, sm.getValue().getQuantity());
                        ps.setString(4, sm.getValue().getUnit().name());

                        ps.setString(5, sm.getType().name());
                        ps.setTimestamp(6, sm.getCreation_date());

                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            conn.commit();
            toSave.setId(ingredientId);
            return toSave;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Double getStockValueAt(Integer ingredientId, Timestamp t) {

        String sql = """
        SELECT
            COALESCE(
                SUM(
                    CASE
                        WHEN type = 'IN'  THEN quantity
                        WHEN type = 'OUT' THEN -quantity
                    END
                ), 0
            ) AS stock_value
        FROM stock_movement
        WHERE id_ingredient = ?
        AND creation_datetime <= ?
    """;

        try (Connection conn = new DBConnection().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ingredientId);
            ps.setTimestamp(2, t);

            ResultSet rs = ps.executeQuery();
            rs.next();

            return rs.getDouble("stock_value");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String ingredientName) {
            super("Insufficient stock for ingredient: " + ingredientName);
        }
    }

    public class OrderNotFoundException extends RuntimeException {
        public OrderNotFoundException(String reference) {
            super("Order not found with reference: " + reference);
        }
    }

    public Order saveOrder(Order orderToSave) {

        String insertOrderSql = """
        INSERT INTO orders (id, reference, creation_datetime)
        VALUES (?, ?, ?)
        RETURNING id
    """;

        String insertDishOrderSql = """
        INSERT INTO dishOrder (id_order, id_dish, quantity)
        VALUES (?, ?, ?)
    """;

        try (Connection conn = new DBConnection().getConnection()) {
            conn.setAutoCommit(false);

            Timestamp orderTime = orderToSave.getCreationDatetime();

            for (DishOrder dishOrder : orderToSave.getDishOrders()) {

                Dish dish = dishOrder.getDish();
                int orderedQuantity = dishOrder.getQuantity();

                for (DishIngredient di : dish.getDishIngredients()) {

                    Ingredient ingredient = di.getIngredient();
                    double required =
                            di.getQuantity_required() * orderedQuantity;

                    double availableStock =
                            getStockValueAt(ingredient.getId(), orderTime);

                    if (availableStock < required) {
                        throw new InsufficientStockException(ingredient.getName());
                    }
                }
            }

            Integer orderId;
            try (PreparedStatement ps = conn.prepareStatement(insertOrderSql)) {

                ps.setInt(1, getNextSerialValue(conn, "orders", "id"));
                ps.setString(2, orderToSave.getReference());
                ps.setTimestamp(3, orderTime);

                ResultSet rs = ps.executeQuery();
                rs.next();
                orderId = rs.getInt(1);
            }

            try (PreparedStatement ps = conn.prepareStatement(insertDishOrderSql)) {
                for (DishOrder dishOrder : orderToSave.getDishOrders()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, dishOrder.getDish().getId());
                    ps.setInt(3, dishOrder.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            orderToSave.setId(orderId);
            return orderToSave;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAvailable(Connection conn, int tableId, Instant arrival, Instant departure)
            throws SQLException {

        String sql = """
        SELECT 1
        FROM orders
        WHERE table_id = ?
          AND arrival_datetime < ?
          AND departure_datetime > ?
        LIMIT 1
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ps.setTimestamp(2, Timestamp.from(departure));
            ps.setTimestamp(3, Timestamp.from(arrival));

            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        }
    }

    public List<TableOrder> findTableOrdersByTableId(
            Connection conn,
            Table table
    ) throws SQLException {

        String sql = """
        SELECT arrival_datetime, departure_datetime
        FROM orders
        WHERE table_id = ?
    """;

        List<TableOrder> orders = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, table.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    TableOrder tableOrder = new TableOrder();
                    tableOrder.setTable(table);
                    tableOrder.setArrivalDatetime(
                            rs.getTimestamp("arrival_datetime").toInstant()
                    );
                    tableOrder.setDepartureDatetime(
                            rs.getTimestamp("departure_datetime").toInstant()
                    );

                    orders.add(tableOrder);
                }
            }
        }

        return orders;
    }

    public List<Table> findAllTablesWithOrders(Connection conn) throws SQLException {

        String sql = """
        SELECT id, numero_table
        FROM restaurant_table
        ORDER BY numero_table
    """;

        List<Table> tables = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Table table = new Table();
                table.setId(rs.getInt("id"));
                table.setNumber(rs.getInt("numero_table"));

                List<TableOrder> tableOrders =
                        findTableOrdersByTableId(conn, table);

                table.setOrderList();

                tables.add(table);
            }
        }

        return tables;
    }

}