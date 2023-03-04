import java.sql.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.*;
import Order.Order;


public class Controller {

    //database connection
    private Connection conn;

    private Order currentOrder;


    @FXML
    private Button button;
    @FXML
    private TextArea orderBox;
    @FXML
    private TextArea totalCostTextBox;

    public void initialize() {
        System.out.println("Initialized");
        setUpDatabase();
        currentOrder = new Order("1");
    }


    public void setUpDatabase(){
        //database login info
        String teamNumber = "team_44";
        String dbName = "csce315331_" + teamNumber;
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
        String username = "csce315331_team_44_master"; // change to your username
        String password = "ShreemanLikesDeepWork"; //nothing to see here
        
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



    public void menuItemButtonClick(ActionEvent event){
        Button b = (Button)event.getSource();
        System.out.println("Button Clicked"+" "+ b.getId());

        String id = b.getId().substring(1);

        String name = getMenuItemName(id);
        double cost = getMenuItemCost(id);

        currentOrder.addItem(name, cost);
        orderBox.setText(currentOrder.getItemCount());
        totalCostTextBox.setText(String.format("Total Cost: $%.2f", currentOrder.getTotalPrice()));
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

}