package com.JDBC;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Dish {
    private int id;
    private String name;
    private Double price;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Dish(int id, String name, DishTypeEnum dishType, double price) {
        this.dishType = dishType;
        this.name = name;
        this.price = price;
        this.id = id;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return id == dish.id && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, ingredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "dishType=" + dishType +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                '}';
    }

    public Double getDishCost() {
        return ingredients == null ? null : ingredients.stream()
                                                        .mapToDouble(Ingredient::getPrice)
                                                        .sum();
    }

    public Double getGrossMargin() {
        if (price==null) {
            throw new RuntimeException("price should not be null");
        }
        else
            return price.doubleValue() - getDishCost();
    }
}
