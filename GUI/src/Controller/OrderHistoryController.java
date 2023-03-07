package Controller;

import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.SessionData;


import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.*;


public class OrderHistoryController {

    private SessionData sessionData;
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
    private TableColumn<OrderRow, String> orderTotal;
    @FXML
    private TableColumn<OrderRow, String> employeeName;

    @FXML
    private TextArea orderHistoryTextBox;

    // public OrderHistoryController(SessionData sessionData) {
    //     this.sessionData = sessionData;
    //     database = sessionData.database;
    // }

    public void initialize() {
        // database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();

        setUpTable();
        addRowOnClick();
        orderHistoryTable.refresh();

    }

    private void setUpTable(){
        //define TableView columns
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

    private String getEmployeeName(int id){
        String ret = "";
        try{
            ResultSet rs = database.executeQuery("SELECT * FROM employee WHERE id = " + id);
            while(rs.next()) {
                ret = rs.getString("name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void addRowOnClick(){
        orderHistoryTable.setRowFactory(tv -> {
            TableRow<OrderRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    OrderRow rowData = row.getItem();
                    int id = rowData.getOrderID();
                    ArrayList<Integer> menuIds = getMenuId(id);
                    HashMap<String, Integer> menuItems = getMenuItems(menuIds);
                    orderHistoryTextBox.setText("");
                    for(String name : menuItems.keySet()){
                        double cost = getMenuCost(name);
                        String rightside = String.format("$%.2f x%d = $%.2f", cost, menuItems.get(name), cost * menuItems.get(name));
                        String print = String.format("%-36s %20s\n", name, rightside);
                        orderHistoryTextBox.appendText(print);
                    }
                }
            });
            return row;
        });
    }

    private ArrayList<Integer> getMenuId(int orderId){
        ArrayList<Integer> menuIds = new ArrayList<>();
        try{
            ResultSet rs = database.executeQuery("SELECT * FROM solditem WHERE orderid = " + orderId);
            while(rs.next()) {
                menuIds.add(rs.getInt("menuid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return menuIds;
    }

    private HashMap<String, Integer> getMenuItems(ArrayList<Integer> menuIds){
        HashMap<String, Integer> menuItems = new HashMap<>();
        for(int id : menuIds){
            try{
                ResultSet rs = database.executeQuery("SELECT * FROM menuitem WHERE id = " + id);
                while(rs.next()) {
                    if(menuItems.containsKey(rs.getString("name"))){
                        menuItems.put(rs.getString("name"), menuItems.get(rs.getString("name")) + 1);
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

    private double getMenuCost(String name){
        double ret = 0;
        try{
            ResultSet rs = database.executeQuery("SELECT * FROM menuitem WHERE name = '" + name + "'");
            while(rs.next()) {
                ret = rs.getDouble("cost");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
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

    public String getOrderTotal() {
        return String.format("%.2f",orderTotal.get());
    }

    public SimpleObjectProperty<String> orderTotalProperty() {
        SimpleObjectProperty<String> ret = new SimpleObjectProperty<String>(getOrderTotal());
        return ret;
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
