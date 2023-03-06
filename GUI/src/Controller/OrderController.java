package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import Order.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;

/**
 * This class handles initializnig variables for JavaFX.
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
     * Connection to the database.
     * 
     * @see DatabaseConnect
     */
    DatabaseConnect database;

    /**
     * Current {@link Order} being processed
     */
    private Order currentOrder;

    /**
     * {@link TextArea} that holds list of currently ordered items
     */
    @FXML
    private TextArea orderBox;

    /**
     * {@link TextArea} that holds total price of the order
     */
    @FXML
    private TextArea totalCostTextBox;

    /**
     * {@link Button} that completes the order and updates Inventory
     */
    @FXML
    private Button submitOrderButton;

    /**
     * {@link TextField} that inputs the customer's name
     */
    @FXML
    private TextField customerNameTextBox;

    /**
     * Initialize connection to the database
     */
    public void initialize() {
        // database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();
        System.out.println("Initialized");

        // TODO: change to orderitem
        currentOrder = new Order(1, database.getLastId("orderitemtest") + 1);
    }

    /**
     * Handles the button click event for the menu items
     * 
     * @param event {@link ActionEvent} that triggers on button click
     */
    public void menuItemButtonOnClick(ActionEvent event) {
        Button b = (Button) event.getSource();
        System.out.println("Button Clicked: " + " " + b.getId());

        // id number starts at the second character
        String id = b.getId().substring(1);

        String name = database.getMenuItemName(id);
        double cost = database.getMenuItemCost(id);

        currentOrder.addItem(name, cost);

        orderBox.setText(currentOrder.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", currentOrder.getTotalCost()));
    }

    /**
     * Handles the text change event for the customr name text box
     */
    public void customerNameOnChanged() {
        currentOrder.setCustomerName(customerNameTextBox.getText());
        System.out.println("Customer Name Changed: " + currentOrder.getCustomerName());
    }

    /**
     * Handles the buttom click event for the submit order button.<br>
     * Inserts the order into both the orderitem and solditem tables.
     * 
     * TODO: update inventory
     */
    public void submitOrderOnClick() {
        if (currentOrder.getTotalCost() == 0.0) {
            System.out.println("Error: No items in order");
            return;
        }
        if (currentOrder.getCustomerName().equals("")) {
            System.out.println("Error: No customer name");
            return;
        }

        database.insertOrderItem(currentOrder);
        database.insertSoldItem(currentOrder);

        database.updateInventory(currentOrder);

        // reset order
        currentOrder = new Order(1, database.getLastId("orderitemtest") + 1);
        orderBox.setText(currentOrder.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", currentOrder.getTotalCost()));
        customerNameTextBox.setText("");

    }
}
