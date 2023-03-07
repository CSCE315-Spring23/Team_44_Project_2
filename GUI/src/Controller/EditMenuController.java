package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Utils.SessionData;
import Utils.DatabaseConnect;

public class EditMenuController {

    private DatabaseConnect database;
    private SessionData session;
    private ArrayList<String> menuItems;

    @FXML
    private ListView menuList;

    public EditMenuController(){
        
    }

    public EditMenuController(SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    public void initialize() {
        ResultSet rs = getMenuItemsQuery();

        try {
            readMenuItems(rs);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("COULDN'T READ MENU ITEMS");
            e.printStackTrace();
        }

        addMenuItemsToListView();

    }

    private ResultSet getMenuItemsQuery(){
        String query = "SELECT * FROM menuitem";
        ResultSet rs = database.executeQuery(query);
        return rs;
    }

    private void readMenuItems(ResultSet rs) throws SQLException{
        menuItems = new ArrayList<>();
        if(rs == null){
            menuItems.add("No Menu Items Retrieved");
            return;
        }
        while(rs.next()){
            String curLine = "";
            curLine += "ID: " + Integer.valueOf(rs.getInt("id")).toString() + ", ";
            curLine += "name: " + rs.getString("name") + ", ";
            curLine += "cost: " + rs.getString("cost") + ", ";
            curLine += "numbersold: " + rs.getString("numbersold") + " ";
            menuItems.add(curLine);
        }
    }

    private void addMenuItemsToListView(){
        ObservableList<String> items = FXCollections.observableArrayList(menuItems);
        menuList.setItems(items);

    }
}
