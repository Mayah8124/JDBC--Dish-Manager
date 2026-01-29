ALTER TABLE ingredient
    ADD CONSTRAINT ingredient_pk PRIMARY KEY (id);

CREATE TABLE dish_ingredient (
    id serial primary key ,
    id_dish int references dish(id),
    id_ingredient int references ingredient(id),
    quantity_required numeric,
    unit unit_type
);

CREATE TYPE unit_type AS ENUM ('KG' , 'L' , 'PCS');

alter table dish
    rename column price to selling_price;

alter table ingredient drop column dish_id;

create type movement_type as enum ('IN' , 'OUT');

CREATE TABLE stock_movement (
    id serial primary key ,
    id_ingredient int references ingredient(id) ,
    quantity numeric ,
    type movement_type ,
    unit unit_type ,
    creation_datetime timestamp
);

CREATE TABLE orders (
    id serial primary key ,
    reference varchar(8) ,
    creation_datetime timestamp
);

CREATE TABLE dishOrder (
    id serial primary key ,
    id_order int references orders(id),
    id_dish int references dish(id),
    quantity int
);