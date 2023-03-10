package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import Items.InventoryItem;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.DatabaseNames;
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
 * Controller for the Inventory Screen
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
    /**
     * Current session data
     *
     * @see SessionData
     */
    private SessionData session;

    /**
     * Connection to the database
     *
     * @see DatabaseConnect
     */
    private DatabaseConnect database;

    /**
     * Switches between scenes or tabs
     *
     * @see SceneSwitch
     */
    private SceneSwitch sceneSwitch;

    /**
     * {@link Button} Button to navigate order scene
     *
     */
    @FXML
    private Button orderButton;

    /**
     * {@link Button} Button to navigate order history scene
     *
     */
    @FXML
    private Button orderHistoryButton;

    /**
     * {@link Button} Button to navigate inventory scene
     *
     */
    @FXML
    private Button inventoryButton;

    /**
     * {@link Button} Button to navigate employees scene
     *
     */
    @FXML
    private Button employeesButton;

    /**
     * {@link Button} Button to navigate edit menu scene
     *
     */
    @FXML
    private Button editMenuButton;

    /**
     * {@link Button} Button to navigate to the data trends scene
     */
    @FXML
    private Button dataTrendsButton;

    /**
     * {@link Button} Button to logout
     *
     */
    @FXML
    private Button logoutButton;


    /**
     * {@link TableView} of {@link InventoryItem} that will display the entire inventory
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
    private TextField updateItemID;

    /**
     * {@link TextField} to allow managers to update stock
     */
    @FXML
    private TextField updateItemQuant;

    /**
     * {@link Button} update inventory button. Triggers {@link #updateInventory()}
     */
    @FXML
    private Button updateItem;

    /**
     * {@link TextField} that allows user input to delete an item
     */
    @FXML
    private TextField deleteItemID;

    /**
     * {@link Button} that will delete the item
     */
    @FXML
    private Button deleteItem;

    /**
     * {@link TextField} that allows user input to add an item
     */
    @FXML
    private TextField addItemName;

    /**
     * {@link TextField} that allows user input to add an item
     */
    @FXML
    private TextField addItemQuant;

    /**
     * {@link Button} that will add an item
     */
    @FXML
    private Button addItem;

    /**
     * Constructor
     */
    public InventoryController() {}

    /**
     * Constructor
     * 
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public InventoryController(final SessionData session) {
        this.session = session;
    }

    /**
     * Initialize Graphical User Interface
     */
    public void initialize() {
        // database login info
        final String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        final String username = DatabaseLoginInfo.username;
        final String password = DatabaseLoginInfo.password;

        this.database = new DatabaseConnect(dbConnectionString, username, password);
        this.database.setUpDatabase();
        System.out.println("Database Connection Established");

        this.setUpTable();
        this.updateTable();

        if (session.isManager()) {
            System.out.println("Manager");
            this.editMenuButton.setVisible(true);
            this.inventoryButton.setVisible(true);
            this.employeesButton.setVisible(true);
        } else {
            System.out.println("Employee");
            this.editMenuButton.setVisible(false);
            this.inventoryButton.setVisible(false);
            this.employeesButton.setVisible(false);
        }
    }

    /**
     * Handles switching to a new scene
     * 
     * @param event {@link ActionEvent} of the {@link Button} being pressing
     * @throws IOException when loading the new window fails
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(this.session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Initializes {@link #inventoryTable}
     */
    private void setUpTable() {
        this.inventoryID.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.itemName.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.quantityCol.setCellValueFactory(cellData -> cellData.getValue().getQuantity());
    }

    /**
     * Adds an item to the inventory database
     */
    public void addItem() {
        System.out.println("Add Item to Inventory.");

        final String itemName = this.addItemName.getText();
        if (itemName.isEmpty()) {
            System.out.println("Invalid item name.\nAbort addition.");
            return;
        }

        long quantity = -1l;
        try {
            quantity = Long.parseLong(this.addItemQuant.getText());
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid item quantity.\nAbort addition.");
            return;
        }

        if (quantity <= 0l) {
            System.out.println("Invalid item quantity.\nAbort addition.");
            return;
        }

        long itemID = 0l;

        final String probe =
                String.format("SELECT MAX(id) FROM %s", DatabaseNames.INVENTORY_DATABASE);
        final ResultSet result = this.database.executeQuery(probe);

        try {
            if (result.next())
                itemID = result.getLong("max") + 1l;
            result.close();
        } catch (SQLException e) {
            System.out.println("Query failed");
            return;
        }

        final String insert =
                String.format("INSERT INTO %s (id, name, quantity) VALUES (%d, '%s', %d);",
                        DatabaseNames.INVENTORY_DATABASE, itemID, itemName, quantity);

        this.database.executeUpdate(insert);
        this.updateTable();
        this.addItemName.setText("");
        this.addItemQuant.setText("");
    }

    /**
     * Deletes an item from the database
     */
    public void deleteItem() {
        System.out.println("Delete Item");

        long itemID = -1l;
        try {
            itemID = Long.parseLong(this.deleteItemID.getText());
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid ID number.\nAbort deletion.");
            return;
        }

        if (itemID <= 0l) {
            System.out.println("Invalid ID number.\nAbort deletion.");
            return;
        }

        final String delete = String.format("DELETE FROM %s WHERE id = %d;",
                DatabaseNames.INVENTORY_DATABASE, itemID);
        this.database.executeUpdate(delete);

        this.updateTable();
        this.deleteItemID.setText("");
    }

    /**
     * Updates the actual database through the {@link #database}
     */
    public void updateInventory() {
        System.out.println("Update Inventory");

        long itemID = -1l;
        try {
            itemID = Long.parseLong(this.updateItemID.getText());
        } catch (NumberFormatException nfe) {
            System.out.println("Failed to parse ID number.\nAbort Update.");
            return;
        }

        if (itemID <= 0l) {
            System.out.println("Invalid ID number.\nAbort Update.");
            return;
        }

        long quantity = -1l;
        try {
            quantity = Long.parseLong(this.updateItemQuant.getText());
        } catch (NumberFormatException nfe) {
            System.out.println("Failed to parse quantity.\nAbort Update.");
            return;
        }

        if (quantity <= 0l) {
            System.out.println("Invalid quantity.\nAbort Update.");
            return;
        }

        final String probe = String.format("SELECT quantity FROM %s WHERE id=%d;",
                DatabaseNames.INVENTORY_DATABASE, itemID);
        final ResultSet result = this.database.executeQuery(probe);

        long quant = 0l;
        try {
            if (result.next())
                quant = result.getLong("quantity");
            result.close();
        } catch (SQLException e) {
            System.out.println("Query failed.");
            return;
        }

        if (quant == 0l) {
            System.out.println("Item does not exist");
            return;
        }

        final String query = String.format("UPDATE %s SET quantity = %d WHERE id=%d;",
                DatabaseNames.INVENTORY_DATABASE, quantity, itemID);
        // System.out.println(query);
        this.database.executeUpdate(query);

        this.updateTable();
        this.updateItemID.setText("");
        this.updateItemQuant.setText("");
        return;
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
     * @return {@link ObservableList} of {@link InventoryItem} holding every item in the inventory
     */
    private ObservableList<InventoryItem> getInventory() {
        final ObservableList<InventoryItem> orders = FXCollections.observableArrayList();
        try {
            final String inventory =
                    String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.INVENTORY_DATABASE);
            final ResultSet rs = database.executeQuery(inventory);
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
