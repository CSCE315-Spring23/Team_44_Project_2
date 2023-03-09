package Controller;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;

import Items.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;


/**
 * .Controller for the Order Screen
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
public class OrderController {
    /**
     * Current session data
     *
     * @see SessionData
     */
    private SessionData session;

    /**
     * Connection to the database
     *
     * @see DatabaseConnect
     */
    private DatabaseConnect database;

    /**
     * Switches between scenes or tabs
     *
     * @see SceneSwitch
     */
    private SceneSwitch sceneSwitch;

    /**
     * ID number of the employee completing the order
     */
    private final int employeeId;

    /**
     * {@link Order} being completed
     */
    private Order order;


    /**
     * {@link Button} Button to navigate order scene
     *
     */
    @FXML
    private Button orderButton;

    /**
     * {@link Button} Button to navigate order history scene
     *
     */
    @FXML
    private Button orderHistoryButton;

    /**
     * {@link Button} Button to navigate inventory scene
     *
     */
    @FXML
    private Button inventoryButton;

    /**
     * {@link Button} Button to navigate employees scene
     *
     */
    @FXML
    private Button employeesButton;

    /**
     * {@link Button} Button to navigate edit menu scene
     *
     */
    @FXML
    private Button editMenuButton;

    /**
     * {@link Button} Button to logout
     *
     */
    @FXML
    private Button logoutButton;

    /*
     * Text that lists the items in the order
     */
    @FXML
    private Label orderBox;

    /*
     * Text field to input the customer's name
     */
    @FXML
    private TextField customerNameField;

    /*
     * Shows total cost of the order
     */
    @FXML
    private Label totalCostLabel;

    /*
     * Button to submit the order
     */
    @FXML
    private Button submitOrderButton;

    /**
     * Constructor
     * 
     * @param session Session's Information
     */
    public OrderController(SessionData session) {
        this.session = session;
        this.database = session.database;
        this.employeeId = session.employeeId;
        this.order = session.order;
    }

    /**
     * Verify Database is Connected
     */
    public void initialize() {
        if (session.isManager()) {
            System.out.println("Manager");
            editMenuButton.setVisible(true);
            inventoryButton.setVisible(true);
            employeesButton.setVisible(true);
        } else {
            System.out.println("Employee");
            editMenuButton.setVisible(false);
            inventoryButton.setVisible(false);
            employeesButton.setVisible(false);
        }
        this.refreshPage();
    }

    /**
     * Refreshes the front-end
     */
    private void refreshPage() {
        this.orderBox.setText(this.order.getItemCount());
        this.totalCostLabel.setText(String.format("Total Cost: $%.2f", this.order.getTotalCost()));
    }


    public void navButtonClicked(ActionEvent event) throws IOException {
        SessionData session = new SessionData(this.database, this.employeeId, this.order);
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Handles the button click event for the menu items
     * 
     * @param event {@link ActionEvent} that triggers on button click
     */
    public void menuItemButtonOnClick(ActionEvent event) {
        final Button b = (Button) event.getSource();
        System.out.println("Menu Item Button Clicked: " + b.getId());

        // add item to order
        final String id = b.getId().substring(1);

        final String name = getMenuItemName(id);
        final double cost = getMenuItemCost(id);

        this.order.addItem(name, cost);

        // update order box and cost
        this.refreshPage();
    }

    /**
     * Handles the text change event for the customr name text box
     * 
     * @deprecated
     */
    public void customerNameOnChanged() {}

    /**
     * Handles the buttom click event for the submit order button. Inserts the order into both the
     * orderitem and solditem tables.
     */
    public void submitOrderOnClick() {
        if (this.order.getTotalCost() == 0.0) {
            System.out.println("Error: No items in order");
            return;
        }

        if (this.customerNameField.getText().isEmpty()) {
            System.out.println("Error: No customer name");
            return;
        }

        // setup order and submit to database
        this.order.setOrderId(getLastId(DatabaseNames.ORDER_ITEM_DATABASE) + 1);
        this.order.setCustomerName(this.customerNameField.getText());

        insertOrderItem(this.order);
        insertSoldItem(this.order);
        updateInventory(this.order);

        // reset order and screen
        this.order = new Order(this.employeeId);
        this.refreshPage();
        this.customerNameField.setText("");
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
            ResultSet rs = database.executeQuery(String.format("Select name FROM %s where id = %s;", DatabaseNames.MENU_ITEM_DATABASE, id));
            while(rs.next()){
                ret = rs.getString("name");
            }
            rs.close();
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
            ResultSet rs = database.executeQuery(String.format("SELECT cost FROM %s where id = %s;",
                DatabaseNames.MENU_ITEM_DATABASE, id));
            while (rs.next()) {
                ret = rs.getDouble("cost");
            }
            rs.close();
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
            ResultSet rs = database.executeQuery(String.format("SELECT MAX(id) from %s;", table));
            while (rs.next()) {
                ret = rs.getInt("max");
            }
            rs.close();
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

        int id = order.getOrderId();
        String customerName = order.getCustomerName();
        double totalCost = order.getTotalCost();
        String date = order.getDate().toString();
        int employeeId = order.getEmployeeId();

        try {
            database.executeUpdate("INSERT INTO " + DatabaseNames.ORDER_ITEM_DATABASE + " VALUES (" + id + ", '"
                    + customerName + "', " + totalCost + ", '" + date + "', " + employeeId + ");");
            System.out.println("Inserted order " + id + " into " + DatabaseNames.ORDER_ITEM_DATABASE);
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

        int orderId = order.getOrderId();
        HashMap<String, Integer> soldItems = order.getItems();
        int soldItemId = getLastId("solditem") + 1;

        for (String item : soldItems.keySet()) {
            int quantity = soldItems.get(item);
            int menuItemId = getMenuItemId(item);
            for (int i = 0; i < quantity; ++i) {
                try {
                    final String query = String.format("INSERT INTO %s VALUES (%d, %d, %d);",
                            DatabaseNames.SOLD_ITEM_DATABASE, soldItemId, menuItemId, orderId);
                    database.executeUpdate(query);

                    // final String debug =
                    //         String.format("Inserted %s with id %d into %s for order %d", item,
                    //                 soldItemId, DatabaseNames.SOLD_ITEM_DATABASE, orderId);
                    // System.out.println(debug);

                    // stmt.close();
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

            ResultSet rs = database.executeQuery(String.format("SELECT id FROM %s WHERE name = \'%s\';",
                DatabaseNames.MENU_ITEM_DATABASE, name));
            while (rs.next()) {
                ret = rs.getInt("id");
            }
            rs.close();
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
        final String databaseName = "inventory";

        HashMap<String, Integer> soldItems = order.getItems();

        for (final String item : soldItems.keySet()) {
            HashMap<Integer, Double> inventoryIDs = new HashMap<Integer, Double>();

            int menuItemId = this.getMenuItemId(item);
            int count = soldItems.get(item);

            try {
                ResultSet rs = database.executeQuery(String.format("SELECT inventoryid, count FROM %s WHERE menuid = %d;",
                    DatabaseNames.RECIPE_ITEM_DATABASE, menuItemId));

                while (rs.next()) {
                    inventoryIDs.put(rs.getInt("inventoryid"), rs.getDouble("count"));
                }
                rs.close();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error getting recipeitem");
            }

            for (int inventoryid : inventoryIDs.keySet()) {
                double quantity = inventoryIDs.get(inventoryid);

                try {

                    database.executeUpdate(String.format("UPDATE %s SET quantity = quantity - %f WHERE id = %s;",
                    DatabaseNames.INVENTORY_DATABASE, quantity * count, inventoryid));

                    System.out
                            .println("Updated " + databaseName + " for inventoryid " + inventoryid);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error updating inventory");
                }

            }

        }

        //To-Go Bag
        database.executeUpdate(String.format("UPDATE %s SET quantity = quantity - 1 WHERE id = 1;",
                DatabaseNames.INVENTORY_DATABASE));
    }
}
