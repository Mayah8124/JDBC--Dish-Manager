package com.JDBC;

import com.JDBC.Data.DataRetriever;
import com.JDBC.Dish;

import java.sql.Timestamp;

public class Main {
    public static void main(String[] args) {
        // Log before changes

        DataRetriever dataRetriever = new DataRetriever();

        //Dish dish = dataRetriever.findDishById(4);
        //Double cost = dish.getDishCost();
        //double margin = dish.getGrossMargin();
        //System.out.println(dish +"\nTotal cost of the ingredients : " + cost + "\nMargin cost : " + margin);

        Timestamp t = Timestamp.valueOf("2024-01-06 12:00:00");

        System.out.println("=== STOCK LEVELS AT " + t + " ===");
        System.out.println("Ingredient 1 : "
                + dataRetriever.getStockValueAt(4, t) + " KG");
        System.out.println("Ingredient 4 : "
                + dataRetriever.getStockValueAt(5, t) + " KG");
        System.out.println("Ingredient 5 : "
                + dataRetriever.getStockValueAt(3, t) + " KG");

        // Log after changes
//        dish.setIngredients(List.of(new Ingredient(1), new Ingredient(2)));
//        Dish newDish = dataRetriever.saveDish(dish);
//        System.out.println(newDish);

        // Ingredient creations
        //List<Ingredient> createdIngredients = dataRetriever.createIngredients(List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0)));
        //System.out.println(createdIngredients);
    }
}