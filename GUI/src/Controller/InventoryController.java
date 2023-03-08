package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import Items.InventoryItem;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controller for the Inventory
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
public class InventoryController {
    private SessionData session;

    /**
     * Connection to the database
     *
     * @see DatabaseConnect
     */
    DatabaseConnect database;

    private SceneSwitch sceneSwitch;

    @FXML
    private Button orderButton;

    @FXML
    private Button orderHistoryButton;

    @FXML
    private Button inventoryButton;

    @FXML
    private Button employeesButton;

    @FXML
    private Button editMenuButton;

    @FXML
    private Button logoutButton;

    /**
     * {@link Button} update inventory button. Triggers {@link #updateInventory()}
     */
    @FXML
    private Button update;

    /**
     * {@link TableView} of {@link InventoryItem} that will display the entire
     * inventory
     */
    @FXML
    private TableView<InventoryItem> inventoryTable;

    /**
     * {@link TableColumn} displaying identificaiton numbers of all inventory items
     */
    @FXML
    private TableColumn<InventoryItem, Long> inventoryID;

    /**
     * {@link TableColumn} displaying name of all inventory items
     */
    @FXML
    private TableColumn<InventoryItem, String> itemName;

    /**
     * {@link TableColumn} displaying the quantity of all inventory items
     */
    @FXML
    private TableColumn<InventoryItem, Long> quantityCol;

    /**
     * {@link TextField} to allow managers to update stock through the item's name
     */
    @FXML
    private TextField itemInput;

    /**
     * {@link TextField} to allow managers to update stock
     */
    @FXML
    private TextField quantityInput;

    /**
     * Name of the item to change as a {@link String}
     */
    private String item;

    /**
     * Amount of the item to change to as a {@link String}
     */
    private String quantity;

    public InventoryController() {
        this.item = new String();
        this.quantity = new String();
        this.session = null;
    }

    public InventoryController(final SessionData session) {
        this.item = new String();
        this.quantity = new String();
        this.session = session;
    }

    /**
     * Initialize Graphical User Interface
     */
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

        if(session.isManager()) {
            System.out.println("Manager");
            editMenuButton.setVisible(true);
            inventoryButton.setVisible(true);
            employeesButton.setVisible(true);
        }
        else{
            System.out.println("Employee");
            editMenuButton.setVisible(false);
            inventoryButton.setVisible(false);
            employeesButton.setVisible(false);
        }
    }

    public void navButtonClicked(ActionEvent event) throws IOException {
        sceneSwitch = new SceneSwitch(this.session);
        sceneSwitch.switchScene(event);
    }

    /**
     * Updates {@link #item} to current item inputed
     */
    public void updateItemName() {
        this.item = this.itemInput.getText();
        System.out.println("Update Item Name:\t" + this.item);
    }

    /**
     * Updates {@link #quantity} to current item inputed
     */
    public void updateQuantity() {
        this.quantity = this.quantityInput.getText();
        System.out.println("Update Quantity:\t" + this.quantity);
    }

    /**
     * Initializes {@link #inventoryTable}
     */
    private void setUpTable() {
        this.inventoryID.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.itemName.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.quantityCol.setCellValueFactory(cellData -> cellData.getValue().getQuantity());

        final ObservableList<InventoryItem> items = this.getInventory();

        this.inventoryTable.setItems(items);
    }

    /**
     * Updates the actual database through the {@link #database}
     */
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

        final String probe = String.format("SELECT quantity FROM inventorytest WHERE name=\'%s\';", this.item);
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
            final String query = String.format("UPDATE inventorytest SET quantity = %d WHERE name=\'%s\';",
                    Long.valueOf(this.quantity), this.item);
            System.out.println(query);
            this.database.executeUpdate(query);

            this.updateTable();
        }
    }

    /**
     * Updates the {@link #inventoryTable} in the Graphical User Interface
     */
    private void updateTable() {
        this.inventoryTable.setItems(this.getInventory());
        this.inventoryTable.refresh();
    }

    /**
     * Helper method to retreive all items from the Inventory.
     * 
     * @return {@link ObservableList} of {@link InventoryItem} holding every item in
     *         the inventory
     */
    private ObservableList<InventoryItem> getInventory() {
        ObservableList<InventoryItem> orders = FXCollections.observableArrayList();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM inventorytest ORDER BY id");
            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                final long quant = rs.getLong("quantity");

                if (id == 0l)
                    continue;
                orders.add(new InventoryItem(id, name, quant));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}
