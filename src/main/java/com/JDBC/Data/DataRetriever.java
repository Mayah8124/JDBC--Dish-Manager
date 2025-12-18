package com.JDBC.Data;

import com.JDBC.*;

import java.util.List;

public class DataRetriever {
    Dish findDishById(int id) {
        throw new RuntimeException("Not implemented yet");
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
