\c mini_dish_db;

CREATE TYPE category AS ENUM ('VEGETABLE', 'ANIMAL' , 'MARINE' , 'DAIRY', 'OTHER');

CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');

CREATE TABLE Ingredient (
    id INT NOT NULL ,
    name VARCHAR(100) NOT NULL ,
    price NUMERIC(10,2) NOT NULL ,
    category category NOT NULL,
    dish_id INT,
    CONSTRAINT fk_dish FOREIGN KEY (dish_id)
                        REFERENCES Dish (id)
);

CREATE TABLE Dish (
    id INT CONSTRAINT dish_pk PRIMARY KEY NOT NULL ,
    name VARCHAR(100) NOT NULL ,
    dish_type dish_type NOT NULL
);