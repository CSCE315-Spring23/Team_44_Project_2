package Controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import Inventory.InventoryItem;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class InventoryController {
    DatabaseConnect database;

    @FXML
    private Button logOut;

    @FXML
    private TableView<InventoryItem> inventoryTable;

    public void initialize() {
        // database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();
        System.out.println("Database Connection Established");

        ObservableList<InventoryItem> items = FXCollections.observableArrayList();

        ResultSet set = database.executeQuery("SELECT * FROM inventory");

        String name;
        long quant;
        try {
            while (set.next()) {
                name = set.getString("name");
                quant = set.getLong("quantity");
                items.add(new InventoryItem(name, quant));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (InventoryItem item : items)
            System.out.println(item);

        this.inventoryTable.setItems(items);
    }

    public void openOrders() {
        System.out.println("Switch to order view");
    }

    public void logOff() {
        System.out.println("Log off");
    }
}
