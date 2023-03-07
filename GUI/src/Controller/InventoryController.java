package Controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class InventoryController {
    private static final class InventoryItem {
        private final SimpleObjectProperty<Long> id;
        private final SimpleObjectProperty<String> name;
        private final SimpleObjectProperty<Long> quantity;

        InventoryItem(final long id, final String name, final long quant) {
            this.id = new SimpleObjectProperty<>(id);
            this.name = new SimpleObjectProperty<>(name);
            this.quantity = new SimpleObjectProperty<>(quant);
        }

        public SimpleObjectProperty<Long> getId() {
            return id;
        }

        public SimpleObjectProperty<String> getName() {
            return name;
        }

        public SimpleObjectProperty<Long> getQuantity() {
            return quantity;
        }
    }

    DatabaseConnect database;

    @FXML
    private Button logOut;

    @FXML
    private Button update;

    @FXML
    private TableView<InventoryItem> inventoryTable;

    @FXML
    private TableColumn<InventoryItem, Long> inventoryID;

    @FXML
    private TableColumn<InventoryItem, String> itemName;

    @FXML
    private TableColumn<InventoryItem, Long> quantityCol;

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

        this.setUpTable();
        this.updateTable();
        this.inventoryTable.refresh();
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

    private void setUpTable() {
        this.inventoryID.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.itemName.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.quantityCol.setCellValueFactory(cellData -> cellData.getValue().getQuantity());

        final ObservableList<InventoryItem> items = this.getInventory();

        this.inventoryTable.setItems(items);
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
        this.inventoryTable.setItems(this.getInventory());
        this.inventoryTable.refresh();
    }

    private ObservableList<InventoryItem> getInventory() {
        ObservableList<InventoryItem> orders = FXCollections.observableArrayList();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM inventorytest ORDER BY id");
            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                final long quant = rs.getLong("quantity");

                if (name.equals("null"))
                    continue;
                orders.add(new InventoryItem(id, name, quant));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}
