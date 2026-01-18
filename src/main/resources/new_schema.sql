ALTER TABLE ingredient
    ADD CONSTRAINT ingredient_pk PRIMARY KEY (id);

CREATE TABLE DishIngredient (
    id serial primary key ,
    id_dish int references dish(id),
    id_ingredient int references ingredient(id),
    quantity_required numeric,
    unit unit_type
);

CREATE TYPE unit_type AS ENUM ('KG' , 'L' , 'PCS');

alter table dish
    rename column price to selling_price;
