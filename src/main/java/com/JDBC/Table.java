package com.JDBC;

import java.util.List;
import java.util.Objects;

public class Table {
    private int id;
    private int number;
    private List<Order> orderList;

    public Table() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList() {
        this.orderList = orderList;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return id == table.id && number == table.number && Objects.equals(orderList, table.orderList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, number, orderList);
    }

    @Override
    public String toString() {
        return "Table{" +
                "id=" + id +
                ", table_number=" + number +
                ", orderList=" + orderList +
                '}';
    }
}
