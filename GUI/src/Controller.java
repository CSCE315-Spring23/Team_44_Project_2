import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import Order.Order;
import DB.DatabaseConnect;
import DB.DatabaseLoginInfo;

public class Controller {

    DatabaseConnect database;

    private Order currentOrder;

    @FXML
    private TextArea orderBox;
    @FXML
    private TextArea totalCostTextBox;
    @FXML
    private Button submitOrderButton;
    @FXML
    private TextField customerNameTextBox;

    public void initialize() {
        //database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;


        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();
        System.out.println("Initialized");
        currentOrder = new Order(1, database.getLastId("orderitemtest") + 1); //TODO: change to orderitem
    }

    /*
     * Handles the button click event for the menu items
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

    /*
     * Handles the text change event for the customer name text box
     */
    public void customerNameOnChanged() {
        currentOrder.setCustomerName(customerNameTextBox.getText());
        System.out.println("Customer Name Changed: " + currentOrder.getCustomerName());
    }

    /*
     * Handles the button click event for the submit order button
     * Inserts the order into the both the orderitem and solditem tables, and TODO: update inventory
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
