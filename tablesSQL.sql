create table menuitem (
id serial primary key,
name text not null,
cost numeric(6,2) not null,
numbersold int not null
);

create table inventory (
id serial primary key,
name text not null,
quantity integer not null
);

create table orderitem(
id serial primary key,
customer_name text not null,
total_cost numeric(10,2) not null,
date timestamp default Current_timestamp,
employee_id int not null
);

create table solditem(
id serial primary key,
menuid int not null,
orderid int not null
);

create table recipeitem(
id serial primary key,
inventoryid int not null,
menuid integer not null,
count numeric(10,2) not null
);

create table employees(
id serial primary key,
name text not null,
role text not null,
pin varchar(4) not null
);