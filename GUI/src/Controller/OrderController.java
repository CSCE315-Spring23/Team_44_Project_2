package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import Order.Order;
import Utils.DatabaseConnect;
import Utils.SessionData;

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

    private final DatabaseConnect database;

    private final int employeeId;

    private Order order;

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
     * Constructor
     * 
     * @param session Session's Information
     */
    public OrderController(SessionData session) {
        this.database = session.database;
        this.employeeId = session.employeeId;
        this.order = session.order;
    }

    /**
     * Verify Database is Connected
     */
    public void initialize() {
    }

    /**
     * Handles the button click event for the menu items
     * 
     * @param event {@link ActionEvent} that triggers on button click
     */
    public void menuItemButtonOnClick(ActionEvent event) {
        Button b = (Button) event.getSource();
        System.out.println("Button Clicked: " + b.getId());

        // id number starts at the second character
        String id = b.getId().substring(1);

        String name = database.getMenuItemName(id);
        double cost = database.getMenuItemCost(id);

        order.addItem(name, cost);

        orderBox.setText(order.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", order.getTotalCost()));
    }

    /**
     * Handles the text change event for the customr name text box
     */
    public void customerNameOnChanged() {
        order.setCustomerName(customerNameTextBox.getText());
        System.out.println("Customer Name Changed: " + order.getCustomerName());
    }

    /**
     * Handles the buttom click event for the submit order button.<br>
     * Inserts the order into both the orderitem and solditem tables.
     * 
     * TODO: update inventory
     */
    public void submitOrderOnClick() {
        if (order.getTotalCost() == 0.0) {
            System.out.println("Error: No items in order");
            return;
        }

        if (order.getCustomerName().isEmpty()) {
            System.out.println("Error: No customer name");
            return;
        }

        database.insertOrderItem(order);
        database.insertSoldItem(order);

        database.updateInventory(order);

        // reset order
        order = new Order(1, database.getLastId("orderitemtest") + 1);
        orderBox.setText(order.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", order.getTotalCost()));
        customerNameTextBox.setText("");

    }
}
