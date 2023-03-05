import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Order.Order;
import DB.DatabaseConnect;

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
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce315331_team_44";
        String username = "csce315331_team_44_master"; // change to your username
        String password = "ShreemanLikesDeepWork"; //nothing to see here


        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();
        System.out.println("Initialized");
        currentOrder = new Order(1, database.getLastId("orderitemtest")+1); //TODO: change to orderitem
    }

    public void menuItemButtonOnClick(ActionEvent event) {
        Button b = (Button) event.getSource();
        System.out.println("Button Clicked: " + " " + b.getId());

        String id = b.getId().substring(1);

        String name = database.getMenuItemName(id);
        double cost = database.getMenuItemCost(id);

        currentOrder.addItem(name, cost);

        orderBox.setText(currentOrder.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", currentOrder.getTotalCost()));
    }

    public void customerNameOnChanged() {
        currentOrder.setCustomerName(customerNameTextBox.getText());
        System.out.println("Customer Name Changed: " + currentOrder.getCustomerName());
    }

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

        // reset order
        currentOrder = new Order(1, database.getLastId("orderitemtest") + 1);
        orderBox.setText(currentOrder.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", currentOrder.getTotalCost()));
        customerNameTextBox.setText("");

    }
}
