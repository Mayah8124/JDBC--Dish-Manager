package com.JDBC;

import com.JDBC.Data.DataRetriever;
import com.JDBC.Dish;

public class Main {
    public static void main(String[] args) {
        // Log before changes
        DataRetriever dataRetriever = new DataRetriever();
        Dish dish = dataRetriever.findDishById(3);
        Double cost = dish.getDishCost();
        double margin = dish.getGrossMargin();

        System.out.println(dish +"\nTotal cost of the ingredients : " + cost + "\nMargin cost : " + margin);


        // Log after changes
//        dish.setIngredients(List.of(new Ingredient(1), new Ingredient(2)));
//        Dish newDish = dataRetriever.saveDish(dish);
//        System.out.println(newDish);

        // Ingredient creations
        //List<Ingredient> createdIngredients = dataRetriever.createIngredients(List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0)));
        //System.out.println(createdIngredients);
    }
}