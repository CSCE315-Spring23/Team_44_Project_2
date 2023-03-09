package Controller;

import java.io.IOException;

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
    private final long employeeId;

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

    /**
     * Handle loading a new window when a navigation button
     * 
     * @param event {@link ActionEvent} of the {@link Button} pressed
     * @throws IOException if loading the new window fails
     */
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

        final String name = database.getMenuItemName(id);
        final double cost = database.getMenuItemCost(id);

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
        this.order.setOrderId(this.database.getLastId(DatabaseNames.ORDER_ITEM_DATABASE) + 1);
        this.order.setCustomerName(this.customerNameField.getText());

        this.database.insertOrderItem(this.order);
        this.database.insertSoldItem(this.order);
        this.database.updateInventory(this.order);

        // reset order and screen
        this.order = new Order(this.employeeId);
        this.refreshPage();
        this.customerNameField.setText("");
    }
}
