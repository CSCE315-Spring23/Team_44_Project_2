package Controller;

import java.io.IOException;

import Items.Order;
import Utils.DatabaseConnect;
import Utils.SceneSwitch;
import Utils.SessionData;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.FXML;


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

    private SessionData session;

    private final DatabaseConnect database;

    private final int employeeId;

    private Order order;

    private SceneSwitch sceneSwitch;

    @FXML
    private Label orderBox;

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
        this.session = session;
        this.database = session.database;
        this.employeeId = session.employeeId;
        this.order = session.order;
    }

    /**
     * Verify Database is Connected
     */
    public void initialize() {}


    public void navButtonClicked(ActionEvent event) throws IOException {
        SessionData session = new SessionData(database, employeeId, order);
        sceneSwitch = new SceneSwitch(session);
        sceneSwitch.switchScene(event);
    }

    /**
     * Handles the button click event for the menu items
     * 
     * @param event {@link ActionEvent} that triggers on button click
     */
    public void menuItemButtonOnClick(ActionEvent event) {
        Button b = (Button) event.getSource();
        System.out.println("Menu Item Button Clicked: " + b.getId());

        // add item to order
        String id = b.getId().substring(1);

        String name = database.getMenuItemName(id);
        double cost = database.getMenuItemCost(id);

        order.addItem(name, cost);
        
        // update order box and cost
        orderBox.setText(order.getItemCount());
        totalCostLabel.setText(String.format("Total Cost: $%.2f", order.getTotalCost()));
    }

    /**
     * Handles the text change event for the customr name text box
     */
    public void customerNameOnChanged() {}

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
        orderBox.setText(order.getItemCount());
        totalCostLabel.setText(String.format("Total Cost: $%.2f", order.getTotalCost()));
        customerNameField.setText("");
    }
}
