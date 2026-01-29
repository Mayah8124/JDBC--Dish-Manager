package com.JDBC;

import java.util.Objects;

public class DishIngredient {
    private int id;
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity_required;
    private UnitType unit;

    public DishIngredient( int id, Dish dish_id, Ingredient ingredient_id, double quantity_required, UnitType unit) {
        this.dish = dish_id;
        this.id = id;
        this.ingredient = ingredient_id;
        this.quantity_required = quantity_required;
        this.unit = unit;
    }

    public DishIngredient() {

    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish_id) {
        this.dish = dish_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantity() {
        return quantity_required;
    }

    public void setQuantity(double quantity_required) {
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
        return id == that.id && Double.compare(quantity_required, that.quantity_required) == 0 && Objects.equals(dish, that.dish) && Objects.equals(ingredient, that.ingredient) && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish, ingredient, quantity_required, unit);
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                ", id=" + id +
                "dish_id=" + dish +
                ", ingredient_id=" + ingredient +
                ", quantity_required=" + quantity_required +
                ", unit=" + unit +
                '}';
    }
}
