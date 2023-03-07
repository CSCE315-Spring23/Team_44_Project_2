package Controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Items.InventoryItem;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class InventoryController {
    DatabaseConnect database;

    @FXML
    private Button logOut;

    @FXML
    private Button update;

    @FXML
    private TextArea inventoryTable;

    @FXML
    private TextField itemInput;

    @FXML
    private TextField quantityInput;

    private String item;
    private String quantity;

    public void initialize() {
        // database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();
        System.out.println("Database Connection Established");

        this.updateTable();
    }


    public void openOrders() {
        System.out.println("Switch to order view");
    }

    public void logOff() {
        System.out.println("Log off");
    }

    public void updateItemName() {
        this.item = this.itemInput.getText();
        System.out.println("Update Item Name:\t" + this.item);
    }

    public void updateQuantity() {
        this.quantity = this.quantityInput.getText();
        System.out.println("Update Quantity:\t" + this.quantity);
    }

    public void updateInventory() {
        System.out.println("Update Inventory");

        if (this.item == null)
            return;
        else if (this.item.isEmpty())
            return;
        if (this.quantity == null)
            return;
        else if (this.quantity.isEmpty())
            return;

        final String probe =
                String.format("SELECT quantity FROM inventorytest WHERE name=\'%s\';", this.item);
        final ResultSet result = this.database.executeQuery(probe);

        long quant = 0l;
        try {
            if (result.next())
                quant = result.getLong("quantity");
            result.close();
        } catch (SQLException e) {
            System.out.println("Query failed.");
        }


        if (quant < 0l) {
            System.out.println("Cannot update item to a negative number");
            return;
        } else if (quant == 0l) {
            System.out.println("Item does not exist");
            return;
        } else {
            final String query =
                    String.format("UPDATE inventorytest SET quantity = %d WHERE name=\'%s\';",
                            Long.valueOf(this.quantity), this.item);
            System.out.println(query);
            this.database.executeUpdate(query);
            this.updateTable();
        }
    }

    private void updateTable() {
        final ResultSet set = database.executeQuery("SELECT name,quantity FROM inventorytest;");
        final StringBuilder sb = new StringBuilder();
        final List<InventoryItem> items = new ArrayList<>();

        try {
            // Calculate maximum length of name and quantity fields
            int maxNameLength = 0;
            int maxQuantityLength = "Quantity".length();
            while (set.next()) {
                String name = set.getString("name");
                if (name.equals("null"))
                    continue;

                long quantity = set.getLong("quantity");
                InventoryItem item = new InventoryItem(name, quantity);
                items.add(item);
                maxNameLength = Math.max(maxNameLength, item.getName().length());
                maxQuantityLength =
                        Math.max(maxQuantityLength, String.valueOf(item.getQuantity()).length());
            }

            sb.append(String.format("%-" + maxNameLength + "s", "Name"));
            sb.append(" | ");
            sb.append(String.format("%" + maxQuantityLength + "s", "Quantity\n"));

            for (int i = 0; i < maxNameLength; ++i)
                sb.append('-');
            sb.append("-+-");
            for (int i = 0; i < maxQuantityLength; ++i)
                sb.append('-');
            sb.append('\n');

            for (InventoryItem item : items) {
                String paddedName = String.format("%-" + maxNameLength + "s", item.getName());
                String paddedQuantity =
                        String.format("%" + maxQuantityLength + "s", item.getQuantity());
                String itemString = paddedName + " | " + paddedQuantity + "\n";

                sb.append(itemString);
            }

            set.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set text to TextArea
        this.inventoryTable.setFont(Font.font("Monospaced")); // Use fixed-width font
        this.inventoryTable.setText(sb.toString());
    }
}
