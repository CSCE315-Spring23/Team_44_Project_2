package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.naming.spi.DirStateFactory.Result;

import Order.Order;

/**
 * This class establishes a connection to the database and performs queries.<br>
 * Additionally, it will update the Inventory automatically based on {@link Order}.
 * 
 * @since 2023-03-07
 * @version 2023-03-07
 * 
 * @author Dai, Kevin
 * @author Davis, Sloan
 * @author Kuppa Jayaram, Shreeman
 * @author Lai, Huy
 * @author Mao, Steven
 */
public class DatabaseConnect {
    /**
     * {@link Connection} to the server
     */
    private Connection conn;

    /**
     * {@link String} that connects to the server
     */
    private final String dbConnectionString;

    /**
     * {@link String} user name credentials
     */
    private final String username;

    /**
     * {@link String} password credentials
     */
    private final String password;

    /**
     * Construct a connection to the database
     * 
     * @param dbConnectionString connection String
     * @param username user name credentials
     * @param password password credentials
     */
    public DatabaseConnect(final String dbConnectionString, final String username,
            final String password) {
        this.dbConnectionString = dbConnectionString;
        this.username = username;
        this.password = password;
    }

    /**
     * Initialize {@link #conn} and established a connection to the database.
     */
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

    public ResultSet executeQuery(String command){
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(command);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * Returns the NAME of a menu item given its ID
     * 
     * @param id Identification number as a {@link String}
     * @return the name of the menu item
     */
    public String getMenuItemName(final String id) {
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

    /**
     * Returns the COST of a menu item given its ID
     * 
     * @param id Identification number as a {@link String}
     * @return the cost of the menu item
     */
    public double getMenuItemCost(final String id) {
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

    /**
     * Returns the last ID in a given table
     * 
     * @param table table name
     * @return the last ID in the table
     */
    public int getLastId(final String table) {
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

    /**
     * Inserts an order into the database
     * 
     * @param order {@link Order} to insert
     */
    public void insertOrderItem(final Order order) {
        String databaseName = "orderitemtest"; // TODO: change to orderitem

        int id = order.getOrderId();
        String customerName = order.getCustomerName();
        double totalCost = order.getTotalCost();
        String date = order.getDate().toString();
        int employeeId = order.getEmployeeId();

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO " + databaseName + " VALUES (" + id + ", '"
                    + customerName + "', " + totalCost + ", '" + date + "', " + employeeId + ");");
            System.out.println("Inserted order " + id + " into " + databaseName);
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting into orderitem");
        }
    }

    /**
     * Inserts each individual menu item in an order into the {@code solditem} database
     * 
     * @param order
     */
    public void insertSoldItem(final Order order) {
        String databaseName = "solditemtest"; // TODO: change to solditem

        int orderId = order.getOrderId();
        HashMap<String, Integer> soldItems = order.getItems();
        int soldItemId = getLastId("solditemtest") + 1;

        for (String item : soldItems.keySet()) {
            int quantity = soldItems.get(item);
            int menuItemId = getMenuItemId(item);
            for (int i = 0; i < quantity; ++i) {
                try {
                    final Statement stmt = conn.createStatement();
                    final String query = String.format("INSERT INTO %s VALUES (%d, %d, %d);",
                            databaseName, soldItemId, menuItemId, orderId);
                    stmt.executeUpdate(query);

                    final String debug =
                            String.format("Inserted %s with id %d into %s for order %d", item,
                                    soldItemId, databaseName, orderId);
                    System.out.println(debug);

                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error inserting into solditem");
                }
                ++soldItemId;
            }
        }
    }

    /*
     * Returns the ID of a menu item given its NAME
     * 
     * @param name
     * 
     * @return menuitem.id
     */
    /**
     * Returns the ID of a menu item given its NAME
     * 
     * @param name of the menu item as a {@link String}
     * @return the Identification number, -1 when not found
     */
    public int getMenuItemId(final String name) {
        int ret = -1;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs =
                    stmt.executeQuery("SELECT id FROM menuitem WHERE name = '" + name + "';");
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

    /**
     * Update the inventory count based on an {@link Order}
     * 
     * @param order {@link Order} that will update inventory
     */
    public void updateInventory(final Order order) {
        final String databaseName = "inventorytest"; // TODO: change to inventory

        HashMap<String, Integer> soldItems = order.getItems();

        for (final String item : soldItems.keySet()) {
            HashMap<Integer, Double> inventoryIDs = new HashMap<Integer, Double>();

            int menuItemId = this.getMenuItemId(item);
            int count = soldItems.get(item);

            try {
                Statement stmt = this.conn.createStatement();
                final String query = String.format(
                        "SELECT inventoryid, count FROM recipeitem WHERE menuid = %d;", menuItemId);
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()) {
                    inventoryIDs.put(rs.getInt("inventoryid"), rs.getDouble("count"));
                }

                rs.close();
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error getting recipeitem");
            }

            for (int inventoryid : inventoryIDs.keySet()) {
                double quantity = inventoryIDs.get(inventoryid);

                try {
                    final Statement stmt = conn.createStatement();
                    final String query =
                            String.format("UPDATE %s SET quantity = quantity - %f WHERE id = %s;",
                                    databaseName, quantity * count, inventoryid);
                    stmt.executeUpdate(query);
                    System.out
                            .println("Updated " + databaseName + " for inventoryid " + inventoryid);
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error updating inventory");
                }

            }

        }
    }
}
