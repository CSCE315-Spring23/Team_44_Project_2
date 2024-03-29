CREATE TABLE menuitem (
id SERIAL PRIMARY KEY,
name TEXT NOT NULL,
cost NUMERIC(6,2) NOT NULL,
numbersold INT NOT NULL
);

CREATE TABLE inventory (
id SERIAL PRIMARY KEY,
name TEXT NOT NULL,
quantity INTEGER NOT NULL
);

CREATE TABLE orderitem(
id SERIAL PRIMARY KEY,
customer_name TEXT NOT NULL,
total_cost NUMERIC(10,2) NOT NULL,
date TIMESTAMP DEFAULT Current_timestamp,
employee_id INT NOT NULL
);

CREATE TABLE solditem(
id SERIAL PRIMARY KEY,
menuid INT NOT NULL,
orderid INT NOT NULL
);

CREATE TABLE recipeitem(
id SERIAL PRIMARY KEY,
inventoryid INT NOT NULL,
menuid INTEGER NOT NULL,
count NUMERIC(10,2) NOT NULL
);

CREATE TABLE employees(
id SERIAL PRIMARY KEY,
name TEXT NOT NULL,
role TEXT NOT NULL,
pin VARCHAR(4) NOT NULL
);