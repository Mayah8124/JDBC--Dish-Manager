package com.JDBC;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Order {
    private int id;
    private String reference;
    private Timestamp creationDatetime;
    private Double vatRate;
    private List<DishOrder> dishOrders;
    private TableOrder table;

    public Order() {
    }

    public Order(Timestamp creationDatetime, List<DishOrder> dishOrders, int id, Double vatRate, String reference) {
        this.creationDatetime = creationDatetime;
        this.dishOrders = dishOrders;
        this.id = id;
        this.reference = reference;
    }

    public Double getVatRate() {
        return vatRate;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public Timestamp getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Timestamp creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TableOrder getTable() {
        return table;
    }

    public void setTable(TableOrder table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id && Objects.equals(reference, order.reference) && Objects.equals(creationDatetime, order.creationDatetime) && Objects.equals(vatRate, order.vatRate) && Objects.equals(dishOrders, order.dishOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDatetime, vatRate, dishOrders);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id= " + id +
                ", reference= '" + reference + '\'' +
                ", creationDatetime= " + creationDatetime +
                ", vatRate= " + vatRate +
                ", dishOrders= " + dishOrders +
                '}';
    }

    public Double getTotalAmountWithoutVAT() {
        if (dishOrders.isEmpty()) {
            return 0.0;
        }

        return dishOrders.stream()
                        .mapToDouble(dishOrders -> dishOrders.getDish().getPrice() * dishOrders.getQuantity())
                        .sum();
    }

}
