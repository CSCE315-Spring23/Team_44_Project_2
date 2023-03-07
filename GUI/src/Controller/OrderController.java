package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Items.Order;
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

    @FXML
    private Label orderBoxLabel;

    @FXML
    private TextField customerNameField;

    @FXML
    private Label totalCostLabel;

    @FXML
    private Button submitOrderButton;


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
    public void initialize() {}

    /**
     * Handles the button click event for the menu items
     * 
     * @param event {@link ActionEvent} that triggers on button click
     */
    public void menuItemButtonOnClick(ActionEvent event) {
        Button b = (Button) event.getSource();
        System.out.println("Menu Item Button Clicked: " + b.getId());

        // id number starts at the second character
        String id = b.getId().substring(1);

        String name = database.getMenuItemName(id);
        double cost = database.getMenuItemCost(id);

        order.addItem(name, cost);

        orderBoxLabel.setText(order.getItemCount());
        totalCostLabel.setText(String.format("Total Cost: $%.2f", order.getTotalCost()));
    }

    /**
     * Handles the text change event for the customr name text box
     */
    public void customerNameOnChanged() {
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

        if (customerNameField.getText().equals("")) {
            System.out.println("Error: No customer name");
            return;
        }
        
        // finalize order and submit to database
        order.setCustomerName(customerNameField.getText());

        database.insertOrderItem(order);
        database.insertSoldItem(order);
        database.updateInventory(order);

        // reset order
        order = new Order(employeeId, database.getLastId("orderitemtest") + 1);
        orderBoxLabel.setText(order.getItemCount());
        totalCostLabel.setText(String.format("Total Cost: $%.2f", order.getTotalCost()));
        customerNameField.setText("");
    }
}
