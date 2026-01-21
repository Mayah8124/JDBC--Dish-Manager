package com.JDBC;

import java.sql.Timestamp;
import java.util.Objects;

public class StockMouvement {
    private int stockID;
    private Ingredient ingredient;
    private int quantity;
    private UnitType unit;
    private Timestamp stockDate;

    public StockMouvement(Ingredient ingredient, int quantity, int stockID, UnitType unit, Timestamp stockDate) {
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.stockID = stockID;
        this.unit = unit;
        this.stockDate = stockDate;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType unit) {
        this.unit = unit;
    }

    public Timestamp getStockDate() {
        return stockDate;
    }

    public void setStockDate(Timestamp stockDate) {
        this.stockDate = stockDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockMouvement that = (StockMouvement) o;
        return stockID == that.stockID && quantity == that.quantity && Objects.equals(ingredient, that.ingredient) && unit == that.unit && Objects.equals(stockDate, that.stockDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockID, ingredient, quantity, unit, stockDate);
    }

    @Override
    public String toString() {
        return "StockMouvement{" +
                "ingredient= " + ingredient +
                ", stockID= " + stockID +
                ", quantity= " + quantity +
                ", unit= " + unit +
                ", stockDate= " + stockDate +
                '}';
    }
}
