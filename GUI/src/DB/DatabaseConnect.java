package DB;

import java.sql.*;
import java.util.HashMap;

import Order.Order;

public class DatabaseConnect {
    private Connection conn;

    private String dbConnectionString;
    private String username;
    private String password;

    public DatabaseConnect(String dbConnectionString, String username, String password) {
        this.dbConnectionString = dbConnectionString;
        this.username = username;
        this.password = password;
    }

    public void setUpDatabase() {
        try {
            conn = DriverManager.getConnection(dbConnectionString, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error connecting to database");
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public String getMenuItemName(String id) {
        String ret = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM menuitem WHERE id = " + id + ";");
            while (rs.next()) {
                ret = rs.getString("name");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting menu item name");
        }
        return ret;
    }

    public double getMenuItemCost(String id) {
        double ret = 0.0;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT cost FROM menuitem WHERE id = " + id + ";");
            while (rs.next()) {
                ret = rs.getDouble("cost");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting menu item cost");
        }
        return ret;
    }


    public int getLastId(String table) {
        int ret = 0;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) from " + table + ";");
            while (rs.next()) {
                ret = rs.getInt("max");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting last id");
        }
        return ret;
    }

    public void insertOrderItem(Order order) {
        String databaseName = "orderitemtest"; //TODO: change to orderitem

        int id = order.getOrderId();
        String customerName = order.getCustomerName();
        double totalCost = order.getTotalCost();
        String date = order.getDate().toString();
        int employeeId = order.getEmployeeId();

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO " + databaseName + " VALUES (" + id + ", '" + customerName + "', "
                    + totalCost + ", '" + date + "', " + employeeId + ");");
            System.out.println("Inserted order " + id + " into " + databaseName);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting into orderitem");
        }
    }

    public void insertSoldItem(Order order){
        String databaseName = "solditemtest"; //TODO: change to solditem

        int orderId = order.getOrderId();
        HashMap<String, Integer> soldItems = order.getItems();
        int soldItemId = getLastId("solditemtest") + 1;

        for(String item : soldItems.keySet()){
            int quantity = soldItems.get(item);
            int menuItemId = getMenuItemId(item);
            for(int i = 0; i < quantity; i++){
                try {
                    Statement stmt = conn.createStatement();
                    stmt.executeUpdate("INSERT INTO " + databaseName +  " VALUES (" + soldItemId + ", " + menuItemId + ", " + orderId + ");");
                    System.out.println(soldItemId + ": Inserted "+ item + " into " + databaseName + "for order " + orderId);
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error inserting into solditem");
                }
                soldItemId++;
            }
        }
    }

    public int getMenuItemId(String name) {
        int ret = -1;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id FROM menuitem WHERE name = '" + name + "';");
            while (rs.next()) {
                ret = rs.getInt("id");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting menu item id");
        }

        return ret;
    }
}

