package Controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InvalidNameException;
import Inventory.InventoryItem;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;

public class InventoryController {
    DatabaseConnect database;

    @FXML
    private Button logOut;

    // @FXML
    // private TableView<InventoryItem> inventoryTable;

    // @FXML
    // private TableColumn<InventoryItem, String> nameColumn;

    // @FXML
    // private TableColumn<InventoryItem, Long> quantityColumn;

    @FXML
    private TextArea inventoryTable;

    public void initialize() {
        // database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();
        System.out.println("Database Connection Established");

        StringBuilder sb = new StringBuilder();

        ResultSet set = database.executeQuery("SELECT * FROM inventory");

        List<InventoryItem> items = new ArrayList<>();

        try {
            // Calculate maximum length of name and quantity fields
            int maxNameLength = 0;
            int maxQuantityLength = 0;
            while (set.next()) {
                String name = set.getString("name");
                long quantity = set.getLong("quantity");
                InventoryItem item = new InventoryItem(name, quantity);
                items.add(item);
                maxNameLength = Math.max(maxNameLength, item.getName().length());
                maxQuantityLength =
                        Math.max(maxQuantityLength, String.valueOf(item.getQuantity()).length());
            }

            sb.append(String.format("%-" + maxNameLength + "s", "Name"));
            sb.append(" | ");
            sb.append(String.format("%" + maxQuantityLength + "s", "Quant\n"));

            for (int i = 0; i < maxNameLength + maxQuantityLength + 3; ++i)
                sb.append('-');
            sb.append('\n');

            for (InventoryItem item : items) {
                String paddedName = String.format("%-" + maxNameLength + "s", item.getName());
                String paddedQuantity =
                        String.format("%" + maxQuantityLength + "s", item.getQuantity());
                String itemString = paddedName + " | " + paddedQuantity + "\n";

                sb.append(itemString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set text to TextArea
        this.inventoryTable.setFont(Font.font("Monospaced")); // Use fixed-width font
        this.inventoryTable.setText(sb.toString());
    }


    public void openOrders() {
        System.out.println("Switch to order view");
    }

    public void logOff() {
        System.out.println("Log off");
    }
}
