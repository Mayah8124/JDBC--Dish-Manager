package com.JDBC;

import java.util.Objects;

public class DishIngredient {
    private int id;
    private Dish dish_id;
    private Ingredient ingredient_id;
    private double quantity_required;
    private UnitType unit;

    public DishIngredient( int id, Dish dish_id, Ingredient ingredient_id, double quantity_required, UnitType unit) {
        this.dish_id = dish_id;
        this.id = id;
        this.ingredient_id = ingredient_id;
        this.quantity_required = quantity_required;
        this.unit = unit;
    }

    public Dish getDish_id() {
        return dish_id;
    }

    public void setDish_id(Dish dish_id) {
        this.dish_id = dish_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingredient getIngredient_id() {
        return ingredient_id;
    }

    public void setIngredient_id(Ingredient ingredient_id) {
        this.ingredient_id = ingredient_id;
    }

    public double getQuantity_required() {
        return quantity_required;
    }

    public void setQuantity_required(double quantity_required) {
        this.quantity_required = quantity_required;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return id == that.id && Double.compare(quantity_required, that.quantity_required) == 0 && Objects.equals(dish_id, that.dish_id) && Objects.equals(ingredient_id, that.ingredient_id) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish_id, ingredient_id, quantity_required, unit);
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                ", id=" + id +
                "dish_id=" + dish_id +
                ", ingredient_id=" + ingredient_id +
                ", quantity_required=" + quantity_required +
                ", unit=" + unit +
                '}';
    }
}
