package Controller.Reports;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import Items.InventoryUsage;
import Items.InventoryUsage;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.DatabaseUtils;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Controller for the Excess Inventory Report window
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
public class ExcessReport {
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
     * {@link DatePicker} to pick the start {@link LocalDate}
     */
    @FXML
    private DatePicker inputDate;

    /**
     * {@link TableView} to display the {@link InventoryUsage} that have less than 10% of their
     * starting quantity used over the time period
     */
    @FXML
    private TableView<InventoryUsage> inventoryTable;

    /**
     * {@link TableColumn} to display the id numbers of the {@link InventoryUsage}
     */
    @FXML
    private TableColumn<InventoryUsage, Long> itemID;

    /**
     * {@link TableColumn} to display the names of the {@link InventoryUsage}
     */
    @FXML
    private TableColumn<InventoryUsage, String> itemName;

    /**
     * {@link TableColumn} to display the current quantity of the {@link InventoryUsage}
     */
    @FXML
    private TableColumn<InventoryUsage, String> itemQuantity;

    /**
     * {@link LocalDate} that holds the current date.
     */
    private final LocalDate date;

    /**
     * Constructor
     * 
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public ExcessReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
        this.date = LocalDate.now();
    }

    /**
     * Inittialize the graphical user interface
     */
    public void initialize() {
        this.setUpTable();
        this.inputDate.setConverter(DatabaseUtils.CONVERTER);

        // set visibility of buttons based on employee role
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
     * Navigates to the scene specified by the button clicked
     *
     * @param event {@link ActionEvent} of {@link Button} in the navigation bar
     * @throws IOException if loading a window fails
     */
    public void navButtonClicked(final ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Updates {@link #inventoryTable}
     * 
     * @param event {@link ActionEvent} of {@link #inputDate}
     */
    public void updateTable(final ActionEvent event) {
        this.inventoryTable.setItems(this.getInventory());
        this.inventoryTable.refresh();
    }

    /**
     * Sets up {@link #inventoryTable} and its columns
     */
    private void setUpTable() {
        this.itemID.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.itemName.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.itemQuantity.setCellValueFactory(cellData -> cellData.getValue().getQuantity());
    }

    /**
     * Gets the inventory useage from a date range. Returns a map from the {@link InventoryUsage}'s
     * ID to its usage.
     * 
     * @return {@link Map} from {@link Long} to {@link Long} holding the inventory usage.
     */
    private Map<Long, Long> getInventoryUse() {
        final String start = this.inputDate.getValue().format(DatabaseUtils.DATE_FORMAT);
        final String end = this.date.format(DatabaseUtils.DATE_FORMAT);

        System.out.printf("Retreiving inventory usage since %s until %s%n", start, end);
        final Map<Long, Long> menuUse = new HashMap<>();
        final Map<Long, Long> inventoryUse = new HashMap<>();

        final String menuQuery = String.format(
                "SELECT menuid FROM %1$s INNER JOIN %2$s ON %1$s.orderid = %2$s.id WHERE Date(%2$s.date) >= \'%3$s\' AND Date(%2$s.date) <= \'%4$s\'",
                DatabaseNames.SOLD_ITEM_DATABASE, DatabaseNames.ORDER_ITEM_DATABASE, start, end);

        final ResultSet menu = this.database.executeQuery(menuQuery);
        try {
            while (menu.next()) {
                final long menuID = menu.getLong("menuid");
                if (menuUse.containsKey(menuID))
                    menuUse.put(menuID, menuUse.get(menuID) + 1l);
                else
                    menuUse.put(menuID, 1l);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        final String inventory =
                String.format("SELECT * FROM %s", DatabaseNames.INVENTORY_DATABASE);
        final ResultSet inv = this.database.executeQuery(inventory);
        try {
            while (inv.next()) {
                final long invID = inv.getLong("id");
                inventoryUse.putIfAbsent(invID, 0l);
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        for (final Map.Entry<Long, Long> entry : menuUse.entrySet()) {
            final long menuID = entry.getKey();
            final long quant = entry.getValue();
            final String query = String.format("SELECt inventoryid from %s WHERE menuid=%d",
                    DatabaseNames.RECIPE_ITEM_DATABASE, menuID);
            final ResultSet set = this.database.executeQuery(query);
            for (long i = 0; i < quant; ++i) {
                final long invID;
                try {
                    invID = set.next() ? set.getLong("inventoryid") : -1l;
                    if (inventoryUse.containsKey(invID))
                        inventoryUse.put(invID, inventoryUse.get(invID) + 1l);
                    else
                        inventoryUse.put(invID, 0l);
                } catch (SQLException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }

        return inventoryUse;
    }

    /**
     * Retreived the inventory items that did not use 10% of its remaining quantity
     * 
     * @return {@link ObservableList} of {@link InventoryUsage}
     */
    private ObservableList<InventoryUsage> getInventory() {
        final ObservableList<InventoryUsage> orders = FXCollections.observableArrayList();
        final Map<Long, Long> inventoryUse = this.getInventoryUse();
        final String query =
                String.format("SELECT * FROM %s ORDER BY id;", DatabaseNames.INVENTORY_DATABASE);
        final ResultSet inventory = this.database.executeQuery(query);

        try {
            while (inventory.next()) {
                final long id = inventory.getLong("id");
                final String name = inventory.getString("name");
                final long quantity = inventory.getLong("quantity");

                if (id == 0)
                    continue;

                final long use = inventoryUse.containsKey(id) ? inventoryUse.get(id) : 0l;

                if (use <= quantity / 9)
                    orders.add(new InventoryUsage(id, name, ((double) use) / (use + quantity)));
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return orders;
    }
}
