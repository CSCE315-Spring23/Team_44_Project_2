package Controller;

import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.SessionData;


import java.sql.ResultSet;


import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;


public class OrderHistoryController {
    private DatabaseConnect database;

    @FXML
    private Button orderHistoryButton;

    @FXML
    private TableView<OrderRow> orderHistoryTable;

    @FXML
    private TableColumn<OrderRow, Integer> orderID;
    @FXML
    private TableColumn<OrderRow, String> customerName;
    @FXML
    private TableColumn<OrderRow, String> orderDate;
    @FXML
    private TableColumn<OrderRow, Double> orderTotal;
    @FXML
    private TableColumn<OrderRow, String> employeeName;

    @FXML
    private TextArea orderHistoryTextBox;


    public void initialize() {
        // database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();

        setUpTable();

        orderHistoryTable.refresh();

    }

    private void setUpTable(){
    //     //define TableView columns
        orderID.setCellValueFactory(cellData -> cellData.getValue().orderIDProperty());

        customerName.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());

        orderDate.setCellValueFactory(cellData -> cellData.getValue().orderDateProperty());

        orderTotal.setCellValueFactory(cellData -> cellData.getValue().orderTotalProperty());

        employeeName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());

        //generate list of last 20 orders
        ObservableList<OrderRow> orders = getOrders();

        //add data to table
        orderHistoryTable.setItems(orders);

    }

    private ObservableList<OrderRow> getOrders() {
        ObservableList<OrderRow> orders = FXCollections.observableArrayList();
        try{
            ResultSet rs = database.executeQuery("SELECT * FROM orderitem ORDER BY id DESC LIMIT 20");
            while(rs.next()) {
                Integer orderID = rs.getInt("id");
                String customerName = rs.getString("customer_name");
                String orderDate = rs.getString("date");
                Double orderTotal = rs.getDouble("total_cost");
                String employeeName = rs.getString("employee_id");

                OrderRow order = new OrderRow(orderID, customerName, orderDate, orderTotal, employeeName);
                orders.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }

}

class OrderRow{
    private final SimpleObjectProperty<Integer> orderID;
    private final SimpleObjectProperty<String> customerName;
    private final SimpleObjectProperty<String> orderDate;
    private final SimpleObjectProperty<Double> orderTotal;
    private final SimpleObjectProperty<String> employeeName;

    public OrderRow(Integer orderID, String customerName, String orderDate, Double orderTotal, String employeeName) {
        this.orderID = new SimpleObjectProperty<>(orderID);
        this.customerName = new SimpleObjectProperty<>(customerName);
        this.orderDate = new SimpleObjectProperty<>(orderDate);
        this.orderTotal = new SimpleObjectProperty<>(orderTotal);
        this.employeeName = new SimpleObjectProperty<>(employeeName);
    }

    public Integer getOrderID() {
        return orderID.get();
    }

    public SimpleObjectProperty<Integer> orderIDProperty() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID.set(orderID);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public SimpleObjectProperty<String> customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public String getOrderDate() {
        return orderDate.get();
    }

    public SimpleObjectProperty<String> orderDateProperty() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate.set(orderDate);
    }

    public Double getOrderTotal() {
        return orderTotal.get();
    }

    public SimpleObjectProperty<Double> orderTotalProperty() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal.set(orderTotal);
    }

    public String getEmployeeName() {
        return employeeName.get();
    }

    public SimpleObjectProperty<String> employeeNameProperty() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName.set(employeeName);
    }

    @Override
    public String toString() {
        return "OrderRow{" +
                "orderID=" + orderID.get() +
                ", customerName=" + customerName.get() +
                ", orderDate=" + orderDate.get() +
                ", orderTotal=" + orderTotal.get() +
                ", employeeName=" + employeeName.get() +
                '}';
    }
}
