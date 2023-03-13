package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import Items.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.DatabaseUtils;
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
     * {@link HashMap} of the menu items Elements: <id, <name, price>>
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
        this.menuItems = new TreeMap<String, Pair<String, Double>>(
                Comparator.comparingLong(Long::parseLong));
    }

    /**
     * Set up page. Load menu table into hash map, load buttons from hash map, set navbar
     * visibility, and refresh page
     */
    public void initialize() {
        // using database (menuitem), load into hashmap and create corresponding button
        final List<Button> buttons = new ArrayList<Button>();
        try {
            final String query =
                    String.format("SELECT * FROM %s", DatabaseNames.MENU_ITEM_DATABASE);
            final ResultSet rs = database.executeQuery(query);
            while (rs.next()) {
                final String id = rs.getString("id");
                final String name = rs.getString("name");
                final double price = rs.getDouble("cost");

                // insert into hashmap
                this.menuItems.put(id, new Pair<String, Double>(name, price));
            }

            for (final String id : this.menuItems.keySet()) {
                // create and insert button
                final Button button = new Button(this.menuItems.get(id).getKey());
                button.setId("b" + id);
                button.setOnAction(this::menuItemButtonOnClick);
                button.setPadding(new Insets(8, 16, 8, 16));
                buttons.add(button);
            }
            this.menuPane.getChildren().addAll(buttons);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // set navbar visibility
        boolean isManager = session.isManager();
        System.out.println("Logged In As " + (isManager ? "Manager" : "Employee"));
        this.editMenuButton.setVisible(isManager);
        this.inventoryButton.setVisible(isManager);
        this.employeesButton.setVisible(isManager);
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
        SessionData session = new SessionData(this.database, this.employeeId, this.order,
                this.customerNameField.getText());
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
        this.order.addItem(this.getMenuItemName(id), this.getMenuItemCost(id));

        // update order box and cost
        this.refreshPage();
    }

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
        this.order.setCustomerName(this.customerNameField.getText());

        this.insertOrderItem(this.order);

        // reset order and screen
        this.order = new Order(this.employeeId,
                DatabaseUtils.getLastId(this.database, DatabaseNames.ORDER_ITEM_DATABASE) + 1l);
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
            return new String();
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
            return -1d;
        }
        return result.getValue();
    }

    /**
     * Inserts an order into the database
     *
     * @param order {@link Order} to insert
     */
    public void insertOrderItem(final Order order) {
        final long id = order.getOrderID();
        final String customerName = order.getCustomerName();
        final double totalCost = order.getTotalCost();
        final String date = order.getDate().format(DatabaseUtils.DATE_FORMAT);
        final long employeeId = order.getEmployeeId();
        final String query = String.format("INSERT INTO %s VALUES (%s, '%s', %s, '%s', %s);",
                DatabaseNames.ORDER_ITEM_DATABASE, id, customerName, totalCost, date, employeeId);
        try {
            this.database.executeUpdate(query);
            System.out
                    .println("Inserted order " + id + " into " + DatabaseNames.ORDER_ITEM_DATABASE);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error inserting into orderitem");
        }

        // update solditems based on order, menuitem's num_sold, and inventory
        this.insertSoldItem(order);
        this.updateMenuItem(order);
        this.updateInventory(order);
    }

    /**
     * Inserts each individual menu item in an order into the {@code solditem} database
     *
     * @param order {@link Order} to insert into the database
     */
    public void insertSoldItem(final Order order) {
        final long orderId = order.getOrderID();
        final HashMap<String, Long> soldItems = order.getItems();
        long soldItemId =
                DatabaseUtils.getLastId(this.database, DatabaseNames.SOLD_ITEM_DATABASE) + 1;

        for (final String item : soldItems.keySet()) {
            final long quantity = soldItems.get(item);
            final long menuItemId = this.getMenuItemId(item);
            for (long i = 0; i < quantity; ++i) {
                final String query = String.format("INSERT INTO %s VALUES (%d, %d, %d);",
                        DatabaseNames.SOLD_ITEM_DATABASE, soldItemId, menuItemId, orderId);
                try {
                    database.executeUpdate(query);
                    ++soldItemId;
                } catch (Exception e) {
                    System.out.println("Error inserting into solditem");
                    e.printStackTrace();
                }
            }
            final String debug = String.format("Inserted %dx %s into %s", quantity, item,
                    DatabaseNames.SOLD_ITEM_DATABASE);
            System.out.println(debug);
        }
    }

    /**
     * Updates the {@code menuitem} database based on an {@link Order}
     * 
     * @param order {@link Order} to insert into the database
     */
    public void updateMenuItem(final Order order) {
        final HashMap<String, Long> soldItems = order.getItems();
        for (final String item : soldItems.keySet()) {
            final long quantity = soldItems.get(item);
            final long menuItemId = this.getMenuItemId(item);
            final String query =
                    String.format("UPDATE %s SET numbersold = numbersold + %d WHERE id = %d;",
                            DatabaseNames.MENU_ITEM_DATABASE, quantity, menuItemId);

            try {
                database.executeUpdate(query);
            } catch (Exception e) {
                System.out.println("Error updating menuitem");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the ID of a menu item given its NAME
     *
     * @param name of the menu item as a {@link String}
     * @return the Identification number, -1 when not found
     */
    public long getMenuItemId(final String name) {
        long ret = -1l;
        final String query = String.format("SELECT id FROM %s WHERE name = \'%s\';",
                DatabaseNames.MENU_ITEM_DATABASE, name);
        final ResultSet rs = database.executeQuery(query);

        try {
            if (rs.next())
                ret = rs.getLong("id");
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
        final HashMap<String, Long> soldItems = order.getItems();

        for (final String item : soldItems.keySet()) {
            final HashMap<Long, Long> inventoryIDs = new HashMap<>();

            final long menuItemId = this.getMenuItemId(item);
            final long count = soldItems.get(item);

            final String query =
                    String.format("SELECT inventoryid, count FROM %s WHERE menuid = %d;",
                            DatabaseNames.RECIPE_ITEM_DATABASE, menuItemId);
            final ResultSet rs = database.executeQuery(query);
            try {
                while (rs.next())
                    inventoryIDs.put(rs.getLong("inventoryid"), rs.getLong("count"));
                rs.close();
            } catch (Exception e) {
                System.out.println("Error getting recipeitem");
                e.printStackTrace();
            }

            for (final long inventoryid : inventoryIDs.keySet()) {
                final long quantity = inventoryIDs.get(inventoryid);
                final String update =
                        String.format("UPDATE %s SET quantity = quantity - %d WHERE id = %d;",
                                DatabaseNames.INVENTORY_DATABASE, quantity * count, inventoryid);
                try {
                    this.database.executeUpdate(update);
                } catch (Exception e) {
                    System.out.println("Error updating inventory");
                    e.printStackTrace();
                }
            }
        }

        // To-Go Bag
        final String togo = String.format("UPDATE %s SET quantity = quantity - 1 WHERE id = 1;",
                DatabaseNames.INVENTORY_DATABASE);
        this.database.executeUpdate(togo);
    }
}
