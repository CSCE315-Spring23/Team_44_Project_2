package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.HashMap;

import Items.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.util.Pair;


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
    private final long employeeId;

    /**
     * {@link Order} being completed
     */
    private Order order;

    /**
     * {@link HashMap} of the menu items
     * Elements: <id, <name, price>>
     */
    private TreeMap<String, Pair<String, Double>> menuItems;


    /**
     * {@link Button} Button to navigate order scene
     */
    @FXML
    private Button orderButton;

    /**
     * {@link Button} Button to navigate order history scene
     */
    @FXML
    private Button orderHistoryButton;

    /**
     * {@link Button} Button to navigate inventory scene
     */
    @FXML
    private Button inventoryButton;

    /**
     * {@link Button} Button to navigate employees scene
     */
    @FXML
    private Button employeesButton;

    /**
     * {@link Button} Button to navigate edit menu scene
     */
    @FXML
    private Button editMenuButton;

    /**
     * {@link Button} Button to logout
     */
    @FXML
    private Button logoutButton;

    /**
     * FlowPane that holds the menu items
     */
    @FXML
    private FlowPane menuPane;

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
        this.menuItems = new TreeMap<String, Pair<String, Double>>(Comparator.comparingInt(Integer::parseInt));
    }

    /**
     * Set up page. Load menu table into hash map, load buttons from hash map, set navbar visibility, and refresh page
     */
    public void initialize() {
        // using database (menuitem), load into hashmap and create corresponding button
        ArrayList<Button> buttons = new ArrayList<Button>();
        try {
            ResultSet rs =
                    database.executeQuery("SELECT * FROM " + DatabaseNames.MENU_ITEM_DATABASE);
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                Double price = rs.getDouble("cost");

                // insert into hashmap
                this.menuItems.put(id, new Pair<String, Double>(name, price));
            }
            
            for (String id : this.menuItems.keySet()) {
                // create and insert button
                Button button = new Button(this.menuItems.get(id).getKey());
                button.setId("b" + id);
                button.setOnAction(this::menuItemButtonOnClick);
                button.setPadding(new Insets(8, 16, 8, 16));
                buttons.add(button);
            }
            menuPane.getChildren().addAll(buttons);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // set navbar visibility
        boolean isManager = session.isManager();
        System.out.println("Logged In As " + (isManager ? "Manager" : "Employee"));
        editMenuButton.setVisible(isManager);
        inventoryButton.setVisible(isManager);
        employeesButton.setVisible(isManager);
        this.refreshPage();
        this.customerNameField.setText(this.session.customerName);
    }

    /**
     * Refreshes the front-end
     */
    private void refreshPage() {
        this.orderBox.setText(this.order.getItemCount());
        this.totalCostLabel.setText(String.format("Total Cost: $%.2f", this.order.getTotalCost()));
    }

    /**
     * Handle loading a new window when a navigation button
     * 
     * @param event {@link ActionEvent} of the {@link Button} pressed
     * @throws IOException if loading the nindow fails
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        SessionData session = new SessionData(this.database, this.employeeId, this.order, this.customerNameField.getText());
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

        // add item to order
        final String id = b.getId().substring(1);
        this.order.addItem(getMenuItemName(id), getMenuItemCost(id));

        // update order box and cost
        this.refreshPage();
    }

    /**
     * Handles the text change event for the customr name text box
     * 
     * @deprecated
     */
    @Deprecated
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
        Pair<String, Double> result;
        if ((result = this.menuItems.get(id)) == null) {
            System.out.println("Error getting menu item name");
        }
        return result.getKey();
    }

    /**
     * Returns the COST of a menu item given its ID
     *
     * @param id Identification number as a {@link String}
     * @return the cost of the menu item
     */
    public double getMenuItemCost(final String id) {
        Pair<String, Double> result;
        if ((result = this.menuItems.get(id)) == null) {
            System.out.println("Error getting menu item cost");
        }
        return result.getValue(); 
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

        long id = order.getOrderId();
        String customerName = order.getCustomerName();
        double totalCost = order.getTotalCost();
        String date = order.getDate().toString();
        long employeeId = order.getEmployeeId();

        try {
            database.executeUpdate(String.format("INSERT INTO %s VALUES (%s, '%s', %s, '%s', %s);",
                    DatabaseNames.ORDER_ITEM_DATABASE, id, customerName, totalCost, date, employeeId));
            System.out.println("Inserted order " + id + " into " + DatabaseNames.ORDER_ITEM_DATABASE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting into orderitem");
        }

        // update solditems based on order, menuitem's num_sold, and inventory
        insertSoldItem(order);
        updateMenuItem(order);
        updateInventory(order);
    }

    /**
     * Inserts each individual menu item in an order into the {@code solditem} database
     *
     * @param order
     */
    public void insertSoldItem(final Order order) {

        long orderId = order.getOrderId();
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
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error inserting into solditem");
                }
                ++soldItemId;
            }
        }
    }

    /**
     * Updates the {@code menuitem} database based on an {@link Order}
     * @param order
     */
    public void updateMenuItem(final Order order) {
        HashMap<String, Integer> soldItems = order.getItems();
        for (String item : soldItems.keySet()) {
            int quantity = soldItems.get(item);
            int menuItemId = getMenuItemId(item);
            try {
                database.executeUpdate(String.format("UPDATE %s SET numbersold = numbersold + %d WHERE id = %d;",
                    DatabaseNames.MENU_ITEM_DATABASE, quantity, menuItemId));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error updating menuitem");
            }
        }
    }

    /**
     * Returns the ID of a menu item given its NAME
     *
     * @param name of the menu item as a {@link String}
     * @return the Identification number, -1 when not found
     */
    public int getMenuItemId(final String name) {
        int ret = -1;
        try {
            ResultSet rs =
                    database.executeQuery(String.format("SELECT id FROM %s WHERE name = \'%s\';",
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
                ResultSet rs = database.executeQuery(
                        String.format("SELECT inventoryid, count FROM %s WHERE menuid = %d;",
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
                    database.executeUpdate(String.format(
                            "UPDATE %s SET quantity = quantity - %f WHERE id = %s;",
                            DatabaseNames.INVENTORY_DATABASE, quantity * count, inventoryid));
                    System.out.println("Updated " + databaseName + " for inventoryid " + inventoryid);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error updating inventory");
                }
            }

        }

        // To-Go Bag
        database.executeUpdate(String.format("UPDATE %s SET quantity = quantity - 1 WHERE id = 1;",
                DatabaseNames.INVENTORY_DATABASE));
    }
}
