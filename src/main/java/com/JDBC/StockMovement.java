package com.JDBC;

import java.sql.Timestamp;
import java.util.Objects;

public class StockMovement {
    private int id;
    private StockValue value;
    private MomentTypeEnum type;
    private Timestamp creation_date;

    public StockMovement(Timestamp creation_date, int id, MomentTypeEnum type, StockValue value) {
        this.creation_date = creation_date;
        this.id = id;
        this.type = type;
        this.value = value;
    }

    public Timestamp getCreation_date() {
        return creation_date;
    }

    public void setCreation_date(Timestamp creation_date) {
        this.creation_date = creation_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MomentTypeEnum getType() {
        return type;
    }

    public void setType(MomentTypeEnum type) {
        this.type = type;
    }

    public StockValue getValue() {
        return value;
    }

    public void setValue(StockValue value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StockMovement that = (StockMovement) o;
        return id == that.id && Objects.equals(value, that.value) && Objects.equals(type, that.type) && Objects.equals(creation_date, that.creation_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value, type, creation_date);
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                "creation_date= " + creation_date +
                ", id= " + id +
                ", value= " + value +
                ", type= " + type +
                '}';
    }
}
