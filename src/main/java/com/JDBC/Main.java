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

        System.out.println("=== TEST 1 : findDishById + getGrossMargin ===");

        // --- 1 : dish with defined price ---
        Dish salade = dr.findDishById(1);
        System.out.println("Dish : " + salade.getName());

        try {
            Double margin = salade.getGrossMargin();
            System.out.println("Gross Margin : " + margin);
        } catch (RuntimeException e) {
            System.out.println("Error margin : " + e.getMessage());
        }

        Dish dish = dr.findDishById(1);

        if (!dish.getName().equalsIgnoreCase("Salade fraiche")) {
            throw new RuntimeException("invalid dish name");
        }

        if (dish.getIngredients().size() != 2) {
            throw new RuntimeException("Nombre d’ingrédients incorrect");
        }

        System.out.println(" Test a) OK");

        try {
            dr.findDishById(999);
            throw new RuntimeException(" Exception attendue non levée");
        } catch (RuntimeException e) {
            System.out.println(" Test b) OK");
        }

        List<Ingredient> ingredients = dr.findIngredients(2, 2);

        if (ingredients.size() != 2) {
            throw new RuntimeException(" Taille incorrecte");
        }

        if (!ingredients.get(0).getName().equalsIgnoreCase("Poulet") ||
                !ingredients.get(1).getName().equalsIgnoreCase("Chocolat")) {
            throw new RuntimeException(" Mauvais ingrédients");
        }

        System.out.println(" Test c) OK");

        List<Ingredient> ingredients2 = dr.findIngredients(3, 5);

        if (!ingredients2.isEmpty()) {
            throw new RuntimeException(" Liste devait être vide");
        }

        System.out.println(" Test d) OK");

        List<Dish> dishes = dr.findDishsByIngredientName("eur");

        if (dishes.size() != 1) {
            throw new RuntimeException(" Nombre de plats incorrect");
        }

        if (!dishes.get(0).getName().equalsIgnoreCase("Gateau au chocolat")) {
            throw new RuntimeException(" Mauvais plat retourné");
        }

        System.out.println(" Test e) OK");

        List<Ingredient> ingredients3 = dr.findIngredientsByCriteria(
                null,
                CategoryEnum.VEGETABLE,
                null,
                1,
                10
        );

        if (ingredients3.size() != 2) {
            throw new RuntimeException(" Résultat incorrect");
        }

        System.out.println(" Test f) OK");

        List<Ingredient> ingredients4 = dr.findIngredientsByCriteria(
                "cho",
                null,
                "Sal",
                1,
                10
        );

        if (!ingredients4.isEmpty()) {
            throw new RuntimeException(" La liste devait être vide");
        }

        System.out.println(" Test g) OK");

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