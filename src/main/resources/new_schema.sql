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

alter table DishIngredient rename  to dish_ingredient;

alter table ingredient drop column dish_id;

CREATE TABLE stock_mouvement (
    id serial primary key ,
    id_ingredient int references ingredient(id) ,
    quantity int ,
    unit unit_type ,
    stock_date timestamp
);

create type mouvement_type as enum ('IN' , 'OUT');

alter table stock_mouvement add column type mouvement_type;