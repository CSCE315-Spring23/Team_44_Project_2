package Controller;

import Items.OrderRow;
import Utils.DatabaseConnect;
import Utils.SceneSwitch;
import Utils.SessionData;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controller for the Order History
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
public class OrderHistoryController {
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
     * {@link Button} Button to navigate order scene
     *
     */
    @FXML private Button orderButton;

    /**
     * {@link Button} Button to navigate order history scene
     *
     */
    @FXML private Button orderHistoryButton;

    /**
     * {@link Button} Button to navigate inventory scene
     *
     */
    @FXML private Button inventoryButton;

    /**
     * {@link Button} Button to navigate employees scene
     *
     */
    @FXML private Button employeesButton;

    /**
     * {@link Button} Button to navigate edit menu scene
     *
     */
    @FXML private Button editMenuButton;

    /**
     * {@link Button} Button to logout
     *
     */
    @FXML private Button logoutButton;

    /**
     * {@link TableView} of {@link OrderRow} to display order history
     *
     */
    @FXML private TableView<OrderRow> orderHistoryTable;

    /**
     * {@link TableColumn} to display order ID
     *
     */
    @FXML private TableColumn<OrderRow, Integer> orderID;

    /**
     * {@link TableColumn} to display customer name
     *
     */
    @FXML private TableColumn<OrderRow, String> customerName;

    /**
     * {@link TableColumn} to display order date
     *
     */
    @FXML private TableColumn<OrderRow, String> orderDate;

    /**
     * {@link TableColumn} to display order total
     *
     */
    @FXML private TableColumn<OrderRow, String> orderTotal;

    /**
     * {@link TableColumn} to display employee name
     *
     */
    @FXML private TableColumn<OrderRow, String> employeeName;

    /**
     * {@link TextArea} to display order details
     *
     */
    @FXML private TextArea orderHistoryTextBox;

    /**
     *  Constructor for OrderHistoryController
     * @param session
     */
    public OrderHistoryController(SessionData session) {
        this.session = session;
        database = session.database;

    }

    /**
     * Initializes the Order History scene
     */
    public void initialize() {
        setUpTable();
        addRowOnClick();
        orderHistoryTable.refresh();

        // set visibility of buttons based on employee role
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
    }

    /**
     * Navigates to the scene specified by the button clicked
     * @param event
     * @throws IOException
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        sceneSwitch = new SceneSwitch(session);
        sceneSwitch.switchScene(event);
    }

    /**
     * Sets up the table to display order history
     */
    private void setUpTable() {
        // define TableView columns
        orderID.setCellValueFactory(cellData -> cellData.getValue().orderIDProperty());

        customerName.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());

        orderDate.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());

        orderTotal.setCellValueFactory(cellData -> cellData.getValue().orderTotalProperty());

        employeeName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());

        // generate list of last 20 orders
        ObservableList<OrderRow> orders = getOrders();

        // add data to table
        orderHistoryTable.setItems(orders);
    }

    /**
     * Gets the last 20 orders from the database
     * @return {@link ObservableList} of {@link OrderRow} of the last 20 orders
     */
    private ObservableList<OrderRow> getOrders() {
        ObservableList<OrderRow> orders = FXCollections.observableArrayList();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM orderitem ORDER BY id DESC LIMIT 20");
            while (rs.next()) {
                Integer orderID = rs.getInt("id");
                String customerName = rs.getString("customer_name");
                String orderDate = rs.getString("date");
                Double orderTotal = rs.getDouble("total_cost");
                Integer employeeID = rs.getInt("employee_id");

                String employeeName = getEmployeeName(employeeID);

                OrderRow order = new OrderRow(orderID, customerName, orderDate, orderTotal, employeeName);
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

    /**
     * Gets the employee name from the database based on the employee ID
     * @param id
     * @return {@link String} of the employee name
     */
    private String getEmployeeName(int id) {
        String ret = "";
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM employee WHERE id = " + id);
            while (rs.next()) {
                ret = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Adds a click event to each {@link OrderRow} in table to display order details
     */
    private void addRowOnClick() {
        orderHistoryTable.setRowFactory(tv -> {
            TableRow<OrderRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    OrderRow rowData = row.getItem();
                    int id = rowData.getOrderID();
                    ArrayList<Integer> menuIds = getMenuId(id);
                    HashMap<String, Integer> menuItems = getMenuItems(menuIds);
                    orderHistoryTextBox.setText("");
                    for (String name : menuItems.keySet()) {
                        double cost = getMenuCost(name);
                        String rightside = String.format("$%.2f x%d = $%.2f", cost,
                                menuItems.get(name), cost * menuItems.get(name));
                        String print = String.format("%-36s %20s\n", name, rightside);
                        orderHistoryTextBox.appendText(print);
                    }
                }
            });
            return row;
        });
    }

    /**
     * Gets the Menu IDs from the database based on the order ID
     * @param orderId
     * @return {@link ArrayList} of {@link Integer} of the menu IDs
     */
    private ArrayList<Integer> getMenuId(int orderId) {
        ArrayList<Integer> menuIds = new ArrayList<>();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM solditem WHERE orderid = " + orderId);
            while (rs.next()) {
                menuIds.add(rs.getInt("menuid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuIds;
    }

    /**
     * Gets the menu items from the database based on Menu IDs
     * @param menuIds
     * @return {@link HashMap} of {@link String} and {@link Integer} of the menu items
     */
    private HashMap<String, Integer> getMenuItems(ArrayList<Integer> menuIds) {
        HashMap<String, Integer> menuItems = new HashMap<>();
        for (int id : menuIds) {
            try {
                ResultSet rs = database.executeQuery("SELECT * FROM menuitem WHERE id = " + id);
                while (rs.next()) {
                    if (menuItems.containsKey(rs.getString("name"))) {
                        menuItems.put(rs.getString("name"),
                                menuItems.get(rs.getString("name")) + 1);
                    } else {
                        menuItems.put(rs.getString("name"), 1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return menuItems;
    }

    /**
     * Gets the menu cost from the database based on the menu name
     * @param name
     * @return {@link Double} of the menu cost
     */
    private double getMenuCost(String name) {
        double ret = 0;
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM menuitem WHERE name = '" + name + "'");
            while (rs.next()) {
                ret = rs.getDouble("cost");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
