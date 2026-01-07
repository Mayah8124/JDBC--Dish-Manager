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

ALTER TABLE Dish ADD COLUMN price NUMERIC(10,2);

UPDATE Dish
    SET price = 2000
    WHERE name = 'Salade fraiche';

UPDATE Dish
    SET price = 6000
    WHERE name = 'Poulet grillé';

UPDATE Dish
    SET price = NULL
    WHERE name IN (
               'Riz aux légumes',
               'Gateau au chocolat',
               'Salade de fruit'
    );


INSERT INTO Dish (id, name , dish_type) VALUES
    (1, 'Salade fraiche', 'START'),
    ('2','Poulet grillé', 'MAIN'),
    (3,'Riz aux légumes', 'MAIN'),
    (4, 'Gateau au chocolat' , 'DESSERT'),
    (5, 'Salade de fruit', 'DESSERT');

INSERT INTO Ingredient (id , name , price , category , dish_id) VALUES
    (1,'Laitue', 800.00 , 'VEGETABLE', 1),
    (2,'Tomate' , 600.00, 'VEGETABLE', 1),
    (3, 'Poulet', 4500.00, 'ANIMAL' , 2),
    (4 , 'Chocolat', 3000.00, 'OTHER', 4),
    (5, 'Beurre', 2500.00, 'DAIRY', 4);