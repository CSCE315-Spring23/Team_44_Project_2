import java.sql.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Order.Order;


public class Controller {

    //database connection
    private Connection conn;

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
        System.out.println("Initialized");
        setUpDatabase();
        currentOrder = new Order(1, getLastId("orderitemtest")+1);
    }


    public void setUpDatabase(){
        //database login info
        String teamNumber = "team_44";
        String dbName = "csce315331_" + teamNumber;
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
        String username = "csce315331_dai"; // change to your username
        String password = "Kd115711"; //nothing to see here
        
        try{
            conn = DriverManager.getConnection(dbConnectionString, username, password);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error connecting to database");
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }



    public void menuItemButtonOnClick(ActionEvent event){
        Button b = (Button)event.getSource();
        System.out.println("Button Clicked: "+" "+ b.getId());

        String id = b.getId().substring(1);

        String name = getMenuItemName(id);
        double cost = getMenuItemCost(id);

        currentOrder.addItem(name, cost);

        orderBox.setText(currentOrder.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", currentOrder.getTotalCost()));
    }

    public String getMenuItemName(String id){
        String ret = "";
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM menuitem WHERE id = " + id + ";");
            while(rs.next()){
                ret = rs.getString("name");
            }
            rs.close();
            stmt.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting menu item name");
        }
        return ret;
    }

    public double getMenuItemCost(String id){
        double ret = 0.0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT cost FROM menuitem WHERE id = " + id + ";");
            while(rs.next()){
                ret = rs.getDouble("cost");
            }
            rs.close();
            stmt.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting menu item cost");
        }
        return ret;
    }

    public void customerNameOnChanged(){
        currentOrder.setCustomerName(customerNameTextBox.getText());
        System.out.println("Customer Name Changed: " + currentOrder.getCustomerName());
    }


    public void submitOrderOnClick(){
        if(currentOrder.getTotalCost() == 0.0){
            System.out.println("No items in order");
            return;
        }
        if(currentOrder.getCustomerName().equals("")){
            System.out.println("No customer name");
            return;
        }

        insertOrderItem(currentOrder);
        /* TODO: add order to solditem table */


        //reset order
        currentOrder = new Order(1, getLastId("orderitemtest")+1);
        orderBox.setText(currentOrder.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", currentOrder.getTotalCost()));
        customerNameTextBox.setText("");

    }

    public int getLastId(String table){
        int ret = 0;
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) from " + table + ";");
            System.out.println(rs);
            while(rs.next()){
                ret = rs.getInt("max");
            }
            rs.close();
            stmt.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error getting last id");
        }
        return ret;
    }

    public void insertOrderItem(Order order){
        int id = order.getOrderId();
        String customerName = order.getCustomerName();
        double totalCost = order.getTotalCost();
        String date = order.getDate().toString();
        int employeeId = order.getEmployeeId();

        try{
            Statement stmt = conn.createStatement();
            int rs = stmt.executeUpdate("INSERT INTO orderitemtest VALUES (" + id + ", '" + customerName + "', " + totalCost + ", '" + date + "', " + employeeId + ");");
            System.out.println("Inserted into orderitemtest");
            stmt.close();
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error inserting into orderitem");
        }
    }
}