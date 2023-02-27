/* tables exist */

SELECT * FROM menuitem;
SELECT * FROM inventory;
SELECT * FROM orderitem;
SELECT * FROM solditem;
SELECT * FROM recipeitem;
SELECT * FROM employee;

/* over $1mill */

SELECT SUM(total_cost) FROM orderitem;

/* game day */

SELECT SUM(total_cost) FROM orderitem WHERE date='2022-09-02';
SELECT SUM(total_cost) FROM orderitem WHERE date='2022-09-03';
-- SELECT SUM(total_cost) FROM orderitem WHERE date='2022-10-22';
-- SELECT SUM(total_cost) FROM orderitem where date='2022-11-19';
-- SELECT SUM(total_cost) FROM orderitem where date='2022-11-26';
