package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Items.OrderRow;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.DatabaseUtils;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * Controller for the Order History Screen
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
     * {@link Button} Button to navigate to the data trends scene
     */
    @FXML
    private Button dataTrendsButton;

    /**
     * {@link Button} Button to logout
     *
     */
    @FXML
    private Button logoutButton;

    /**
     * {@link TableView} of {@link OrderRow} to display order history
     *
     */
    @FXML
    private TableView<OrderRow> orderHistoryTable;

    /**
     * {@link TableColumn} to display order ID
     *
     */
    @FXML
    private TableColumn<OrderRow, Long> orderID;

    /**
     * {@link TableColumn} to display customer name
     *
     */
    @FXML
    private TableColumn<OrderRow, String> customerName;

    /**
     * {@link TableColumn} to display order date
     *
     */
    @FXML
    private TableColumn<OrderRow, String> orderDate;

    /**
     * {@link TableColumn} to display order total
     *
     */
    @FXML
    private TableColumn<OrderRow, String> orderTotal;

    /**
     * {@link TableColumn} to display employee name
     *
     */
    @FXML
    private TableColumn<OrderRow, String> employeeName;

    /**
     * {@link TextArea} to display order details
     *
     */
    @FXML
    private TextArea orderHistoryTextBox;

    /**
     * Constructor for OrderHistoryController
     * 
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public OrderHistoryController(final SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    /**
     * Initializes the Order History scene
     */
    public void initialize() {
        this.setUpTable();
        this.updateTable();
        this.addRowOnClick();
        this.orderHistoryTable.refresh();

        // set visibility of buttons based on employee role
        if (session.isManager()) {
            System.out.println("Manager");
            this.editMenuButton.setVisible(true);
            this.inventoryButton.setVisible(true);
            this.employeesButton.setVisible(true);
        } else {
            System.out.println("Employee");
            this.editMenuButton.setVisible(false);
            this.inventoryButton.setVisible(false);
            this.employeesButton.setVisible(false);
        }
    }

    /**
     * Navigates to the scene specified by the button clicked
     * 
     * @param event {@link ActionEvent} of {@link Button} in the navigation bar
     * @throws IOException if loading a window fails
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Sets up the table to display order history
     */
    private void setUpTable() {
        // define TableView columns
        this.orderID.setCellValueFactory(cellData -> cellData.getValue().orderIDProperty());
        this.customerName
                .setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        this.orderDate.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());
        this.orderTotal.setCellValueFactory(cellData -> cellData.getValue().orderTotalProperty());
        this.employeeName
                .setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
    }

    /**
     * Update the {@link #orderHistoryTable}
     */
    private void updateTable() {
        this.orderHistoryTable.setItems(this.getOrders());
        this.orderHistoryTable.refresh();
    }

    /**
     * Gets the last 20 orders from the database
     * 
     * @return {@link ObservableList} of {@link OrderRow} of the last 20 orders
     */
    private ObservableList<OrderRow> getOrders() {
        final ObservableList<OrderRow> orders = FXCollections.observableArrayList();
        final String query = String.format("SELECT * FROM %s ORDER BY id DESC LIMIT 20",
                DatabaseNames.ORDER_ITEM_DATABASE);
        final ResultSet rs = database.executeQuery(query);
        try {
            while (rs.next()) {
                final long orderID = rs.getLong("id");
                final String customerName = rs.getString("customer_name");
                final String orderDate = rs.getString("date");
                final Double orderTotal = rs.getDouble("total_cost");
                final long employeeID = rs.getInt("employee_id");
                final String employeeName =
                        DatabaseUtils.getEmployeeName(this.database, employeeID);
                orders.add(
                        new OrderRow(orderID, customerName, orderDate, orderTotal, employeeName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }



    /**
     * Adds a click event to each {@link OrderRow} in table to display order details
     */
    private void addRowOnClick() {
        this.orderHistoryTable.setRowFactory(tv -> {
            final TableRow<OrderRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    final OrderRow rowData = row.getItem();
                    final long id = rowData.getOrderID();

                    List<String> orderDetails = new ArrayList<>();
                    try{
                        // get order details
                        // %1$s = menuitem database, %2$s = solditem database, %3$s = orderitem database, %4$d = order id
                        ResultSet rs = database.executeQuery(String.format("SELECT %1$s.name, %1$s.cost, COUNT(*) as totalSold"+
                            " FROM %2$s " +
                            " JOIN %1$s ON %2$s.menuid = %1$s.id " +
                            " JOIN %3$s ON %2$s.orderid = %3$s.id " +
                            " WHERE %3$s.id = %4$d " +
                            " GROUP BY %1$s.id",
                            DatabaseNames.MENU_ITEM_DATABASE, DatabaseNames.SOLD_ITEM_DATABASE, DatabaseNames.ORDER_ITEM_DATABASE, id));

                        while(rs.next()){
                            String name = rs.getString("name");
                            double cost = rs.getDouble("cost");
                            int totalSold = rs.getInt("totalSold");
                            final String rightside = String.format("$%.2f x%d = $%.2f", cost, totalSold, cost * totalSold);
                            final String print = String.format("%-36s %20s\n", name, rightside);
                            orderDetails.add(print);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    this.orderHistoryTextBox.setText("");
                    for(String s : orderDetails){
                        this.orderHistoryTextBox.appendText(s);
                    }
                }
            });
            return row;
        });
    }
}
