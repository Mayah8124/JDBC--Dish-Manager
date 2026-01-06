package test.JDBC.data;

import com.JDBC.CategoryEnum;
import com.JDBC.Data.DataRetriever;
import com.JDBC.Dish;
import com.JDBC.Ingredient;
import org.junit.*;

import javax.xml.crypto.Data;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DataRetrieverTest {

    public static void main(String[] args) {
        final DataRetriever dr = new DataRetriever();

        @Test
        void testFindDishByID() {
            Dish dish = dr.findDishById(1);

            assertEquals("Salade fraiche", dish.getName(), "invalid dish name");
            assertEquals(String.valueOf(2), dish.getIngredients().size(), "invalid ingredient number");

        }

        @Test
        void testFindDishById() {
            assertThrows(RuntimeException.class, () -> dr.findDishById(999));
        }

        @Test
        void testFindIngredientsByPageAndSize() {
            List<Ingredient> ingredients = dr.findIngredients(2, 2);

            assertEquals(2, ingredients.size());
            assertEquals("Poulet", ingredients.get(0).getName());
            assertEquals("Chocolat", ingredients.get(1).getName());
        }

        @Test
        void testFindIngredients_outOfRange() {
            List<Ingredient> ingredients = dr.findIngredients(3, 5);

            assertTrue("The list should be empty", ingredients.isEmpty());
        }

        @Test
        void testFindDishByIngredientName() {
            List<Dish> dishes = dr.findDishsByIngredientName("eur");

            assertEquals(1, dishes.size());
            assertEquals("Gateau au chocolat", dishes.get(0).getName());
        }

        @Test
        void testFindIngredientsByCriteria() {
            List<Ingredient> ingredients = dr.findIngredientsByCriteria(
                    null,
                    CategoryEnum.VEGETABLE,
                    null,
                    1,
                    10
            );

            assertEquals(2, ingredients.size());
            assertEquals("Laitue", ingredients.get(0).getName());
            assertEquals("Tomate", ingredients.get(1).getName());
        }

        @Test
        void testFindIngredientsByCriteria_notFound() {
            List<Ingredient> ingredients = dr.findIngredientsByCriteria(
                    "cho",
                    null,
                    "Sal",
                    1,
                    10
            );

            assertTrue("The list should be emp", ingredients.isEmpty());
        }

        @Test
        void testFindIngredientsByCriteria_chocolat() {
            List<Ingredient> ingredients = dr.findIngredientsByCriteria(
                    "cho",
                    null,
                    "gâteau",
                    1,
                    10
            );

            assertEquals(1, ingredients.size());
            assertEquals("Chocolat", ingredients.get(0).getName());
        }

        @Test
        void testCreateIngredients_ok() {
            List<Ingredient> newIngredients = new ArrayList<>();
            newIngredients.add(new Ingredient("Fromage", CategoryEnum.DAIRY, 1200.0));
            newIngredients.add(new Ingredient("Oignon", CategoryEnum.VEGETABLE, 500.0));

            List<Ingredient> created = dr.createIngredients(newIngredients);

            assertEquals(2, created.size());
            assertEquals("Fromage", created.get(0).getName());
            assertEquals("Oignon", created.get(1).getName());
        }

        @Test
        void testCreateIngredients_duplicate() {
            List<Ingredient> newIngredients = new ArrayList<>();
            newIngredients.add(new Ingredient("Carotte", CategoryEnum.VEGETABLE, 2000.0));
            newIngredients.add(new Ingredient("Laitue", CategoryEnum.VEGETABLE, 2000.0));

            assertThrows(RuntimeException.class, () -> dr.createIngredients(newIngredients));
        }

        @Test
        void testSaveDish_newDish() {
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient("Oignon", CategoryEnum.VEGETABLE, 500.0));

            Dish dish = new Dish("Soupe de légumes", "START", ingredients);

            Dish saved = dr.saveDish(dish);

            assertNotNull(saved.getId());
            assertEquals("Soupe de légumes", saved.getName());
            assertEquals(1, saved.getIngredients().size());
            assertEquals("Oignon", saved.getIngredients().get(0).getName());
        }

        @Test
        void testSaveDish_updateDish_addIngredients() {
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient("Oignon", CategoryEnum.VEGETABLE, 500.0));
            ingredients.add(new Ingredient("Laitue", CategoryEnum.VEGETABLE, 800.0));
            ingredients.add(new Ingredient("Tomate", CategoryEnum.VEGETABLE, 600.0));
            ingredients.add(new Ingredient("Fromage", CategoryEnum.DAIRY, 1200.0));

            Dish dish = new Dish(1, "Salade fraîche", 'START', ingredients);

            Dish updated = dr.saveDish(dish);

            assertEquals(4, updated.getIngredients().size());
            assertTrue(updated.getIngredients().stream().anyMatch(i -> i.getName().equals("Oignon")));
            assertTrue(updated.getIngredients().stream().anyMatch(i -> i.getName().equals("Fromage")));
        }

        @Test
        void testSaveDish_updateDish_removeIngredients() {
            List<Ingredient> ingredients = new ArrayList<>();
            ingredients.add(new Ingredient());

            Dish dish = new Dish(1, "Salade de fromage", 'START', ingredients);

            Dish updated = dr.saveDish(dish);

            assertEquals(1, updated.getIngredients().size());
            assertEquals("Fromage", updated.getIngredients().get(0).getName());
        }
    }
}
