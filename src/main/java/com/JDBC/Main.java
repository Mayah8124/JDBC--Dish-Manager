package com.JDBC;

import com.JDBC.Data.DataRetriever;
import com.JDBC.Dish;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Log before changes

        DataRetriever dataRetriever = new DataRetriever();

        //Dish dish = dataRetriever.findDishById(4);
        //Double cost = dish.getDishCost();
        //double margin = dish.getGrossMargin();
        //System.out.println(dish +"\nTotal cost of the ingredients : " + cost + "\nMargin cost : " + margin);

//        Timestamp t = Timestamp.valueOf("2024-01-06 12:00:00");
//
//        System.out.println("=== STOCK LEVELS AT " + t + " ===");
//        System.out.println("Ingredient 1 : "
//                + dataRetriever.getStockValueAt(4, t) + " KG");
//        System.out.println("Ingredient 4 : "
//                + dataRetriever.getStockValueAt(5, t) + " KG");
//        System.out.println("Ingredient 5 : "
//                + dataRetriever.getStockValueAt(3, t) + " KG");

        // Log after changes
//        dish.setIngredients(List.of(new Ingredient(1), new Ingredient(2)));
//        Dish newDish = dataRetriever.saveDish(dish);
//        System.out.println(newDish);

        // Ingredient creations
        //List<Ingredient> createdIngredients = dataRetriever.createIngredients(List.of(new Ingredient(null, "Fromage", CategoryEnum.DAIRY, 1200.0)));
        //System.out.println(createdIngredients);

        Table table1 = new Table();
        table1.setId(1);
        table1.setNumber(1);
        table1.setOrderList(new ArrayList<>());

        Table table2 = new Table();
        table2.setId(2);
        table2.setNumber(2);
        table2.setOrderList(new ArrayList<>());

        Table table3 = new Table();
        table3.setId(3);
        table3.setNumber(3);
        table3.setOrderList(new ArrayList<>());

        List<Table> tables = List.of(table1, table2, table3);

        TableOrder existingOrder1 = new TableOrder();
        existingOrder1.setTable(table1);
        existingOrder1.setArrivalDatetime(Instant.parse("2026-01-29T18:00:00Z"));
        existingOrder1.setDepartureDatetime(Instant.parse("2026-01-29T19:30:00Z"));
        table1.getOrderList().add(existingOrder1);

        TableOrder existingOrder2 = new TableOrder();
        existingOrder2.setTable(table2);
        existingOrder2.setArrivalDatetime(Instant.parse("2026-01-29T18:30:00Z"));
        existingOrder2.setDepartureDatetime(Instant.parse("2026-01-29T20:00:00Z"));
        table2.getOrderList().add(existingOrder2);

        System.out.println("Tables et commandes existantes :");
        for (Table t : tables) {
            System.out.println(t);
        }

        TableOrder newOrder1 = new TableOrder();
        newOrder1.setTable(table3);
        newOrder1.setArrivalDatetime(Instant.parse("2026-01-29T18:45:00Z"));
        newOrder1.setDepartureDatetime(Instant.parse("2026-01-29T19:15:00Z"));

        table3.getOrderList().add(newOrder1);
        System.out.println("\nNouvelle commande sur table libre ajoutée : " + newOrder1);

        TableOrder newOrder2 = new TableOrder();
        newOrder2.setTable(table1);
        newOrder2.setArrivalDatetime(Instant.parse("2026-01-29T18:45:00Z"));
        newOrder2.setDepartureDatetime(Instant.parse("2026-01-29T19:15:00Z"));

        boolean available = dataRetriever.isAvailable(
                newOrder2.getTable().getId(),
                newOrder2.getArrivalDatetime(),
                newOrder2.getDepartureDatetime()
        );

        System.out.println("\nLa table " + newOrder2.getTable().getNumber() +
                (available ? " est libre pour cette plage horaire." : " est occupée pour cette plage horaire."));

        if (available) {
            table1.getOrderList().add(newOrder2);
        } else {
            List<Integer> freeTables = new ArrayList<>();
            for (Table t : tables) {
                if (dataRetriever.isAvailable(t.getId(), newOrder2.getArrivalDatetime(), newOrder2.getDepartureDatetime())) {
                    freeTables.add(t.getNumber());
                }
            }

            if (freeTables.isEmpty()) {
                System.out.println("Aucune table disponible à cette plage horaire.");
            } else {
                System.out.println("Tables libres disponibles : " + freeTables);
            }
        }
    }
}