package Controller.Reports;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import Items.InventoryItem;
import Items.SalesReportRow;
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
 * Controller for the Sales Report window
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
public class SalesReport {
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
     * {@link DatePicker} for the start of the sales report
     */
    @FXML
    private DatePicker startDate;

    /**
     * {@link DatePicker} for the end of the sales report
     */
    @FXML
    private DatePicker endDate;

    /**
     * {@link Button} that generates the report
     */
    @FXML
    private Button goButton;

    /**
     * {@link TableView} of {@link SalesReportRow} that will display the sales report for the
     * {@link Items.MenuItem}
     */
    @FXML
    private TableView<SalesReportRow> menuItemTable;

    /**
     * {@link TableColumn} that displays menu id numbers
     */
    @FXML
    private TableColumn<SalesReportRow, Long> menuIDCol;

    /**
     * {@link TableColumn} that displays menu names
     */
    @FXML
    private TableColumn<SalesReportRow, String> menuNameCol;

    /**
     * {@link TableColumn} that displays the number of menu items sold
     */
    @FXML
    private TableColumn<SalesReportRow, Long> menuNumSoldCol;

    /**
     * {@link TableView} of {@link SalesReportRow} that will display the sales report for the
     * {@link InventoryItem}
     */
    @FXML
    private TableView<SalesReportRow> invItemTable;

    /**
     * {@link TableColumn} that displays the id number of the inventory items
     */
    @FXML
    private TableColumn<SalesReportRow, Long> invIDCol;

    /**
     * {@link TableColumn} that displays the name of the inventory items
     */
    @FXML
    private TableColumn<SalesReportRow, String> invNameCol;

    /**
     * {@link TableColumn} that displays the number of times an inventory item was used
     */
    @FXML
    private TableColumn<SalesReportRow, Long> invNumSoldCol;

    /**
     * {@link Map} from {@link String} to {@link Long} that maps from a menu item's name to the
     * amount sold
     */
    private final Map<String, Long> orders;

    /**
     * {@link Map} from {@link String} to {@link Long} that maps from a menu item's name to its id
     */
    private final Map<String, Long> menuIDs;

    /**
     * {@link Map} from {@link String} to {@link Long} that maps from an inventory item's name to
     * its id
     */
    private final Map<String, Long> invOrders;

    /**
     * {@link Map} from {@link String} to {@link Long} that maps from an inventory item's name to
     * the quantity
     */
    private final Map<String, Long> invIDs;

    /**
     * {@link Map} from {@link Long} to {@link String}
     */
    private final Map<Long, String> invNames;

    /**
     * Counts the number of orders made
     */
    private long numOrders;

    /**
     * Constructor
     * 
     * @param session {@link SessionData} passed from {@link SceneSwitch}
     */
    public SalesReport(final SessionData session) {
        this.session = session;
        this.database = session.database;

        this.orders = new HashMap<>();
        this.menuIDs = new HashMap<>();

        this.invOrders = new HashMap<>();
        this.invIDs = new HashMap<>();
        this.invNames = new HashMap<>();

        this.numOrders = 0;
    }

    /**
     * Initialize the graphical user interface
     */
    public void initialize() {
        this.setUpHashMap();
        this.setUpMenuItemTable();
        this.setUpInvItemTable();

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
     * Sort the {@link #menuItemTable} and {@link #invItemTable} by the id number
     */
    private void sortTableByID() {
        // sort table by id by default
        this.menuItemTable.getSortOrder().add(menuIDCol);
        this.menuIDCol.setSortType(TableColumn.SortType.ASCENDING);
        this.menuItemTable.sort();

        this.menuItemTable.refresh();

        this.invItemTable.getSortOrder().add(invIDCol);
        this.invIDCol.setSortType(TableColumn.SortType.ASCENDING);
        this.invItemTable.sort();

        this.invItemTable.refresh();
    }

    /**
     * Set up the {@link #menuItemTable} and its columns
     */
    private void setUpMenuItemTable() {
        // set up columns
        this.menuIDCol.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.menuNameCol.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.menuNumSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    /**
     * Set up the {@link #invItemTable} and its columns
     */
    private void setUpInvItemTable() {
        // set up columns
        this.invIDCol.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.invNameCol.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.invNumSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    /**
     * Set up all the {@link Map}
     */
    private void setUpHashMap() {
        // set up all menu items
        final String menu =
                String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.MENU_ITEM_DATABASE);
        final ResultSet menuSet = database.executeQuery(menu);
        try {
            while (menuSet.next()) {
                final String name = menuSet.getString("name");
                final long id = menuSet.getLong("id");
                orders.put(name, 0l);
                menuIDs.put(name, id);
            }
            menuSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // set up all inventory items
        final String inventory =
                String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.INVENTORY_DATABASE);
        final ResultSet inventorySet = database.executeQuery(inventory);
        try {
            while (inventorySet.next()) {
                final String name = inventorySet.getString("name");
                final long id = inventorySet.getLong("id");
                if (id == 0l)
                    continue;

                invOrders.put(name, 0l);
                invIDs.put(name, id);
                invNames.put(id, name);
            }
            inventorySet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates the sales report
     */
    public void onGoClick() {
        final LocalDate sDate = this.startDate.getValue();
        if (sDate == null)
            return;

        final LocalDate eDate = this.endDate.getValue();
        if (eDate == null)
            return;

        final String startDate = sDate.format(DatabaseUtils.DATE_FORMAT);
        final String endDate = eDate.format(DatabaseUtils.DATE_FORMAT);

        this.getOrderItemsBetweenDates(startDate, endDate);
        this.menuItemTable.setItems(this.getMenuItems());

        this.getInventory();
        this.invItemTable.setItems(this.getInvItems());

        this.sortTableByID();
    }

    /**
     * Retreived {@link Items.Order} between the start and end dates
     * 
     * @param startDate the start of the sales report
     * @param endDate the end of the sales reprot
     */
    private void getOrderItemsBetweenDates(final String startDate, final String endDate) {
        System.out.printf("Retreiving orders between %s and %s%n", startDate, endDate);
        // get all orders between the dates
        // %1$s = orderitem database, %2$s = solditem database, %3$s = menuitem database, %4$s =
        // start date, %5$s = end date
        final String query = String.format(
                "SELECT name, count(*) AS totalSold FROM %1$s"
                        + " join %2$s ON %1$s.id = %2$s.orderid join %3$s ON %2$s.menuid = %3$s.id"
                        + " WHERE Date(%1$s.date) >= \'%4$s\' AND Date(%1$s.date) <= \'%5$s\'"
                        + " GROUP By name ORDER BY totalSold DESC",
                DatabaseNames.ORDER_ITEM_DATABASE, DatabaseNames.SOLD_ITEM_DATABASE,
                DatabaseNames.MENU_ITEM_DATABASE, startDate, endDate);
        final ResultSet rs = database.executeQuery(query);
        try {
            while (rs.next()) {
                final String name = rs.getString("name");
                final long numSold = rs.getLong("totalSold");
                orders.put(name, numSold);
                numOrders++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Retreived the menu items
     * 
     * @return ObservableList of {@link SalesReportRow}
     */
    private ObservableList<SalesReportRow> getMenuItems() {
        final ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for (String name : orders.keySet())
            items.add(new SalesReportRow(menuIDs.get(name), name, orders.get(name)));
        return items;
    }

    /**
     * Gets inventory items and placed them into a {@link Map}
     */
    private void getInventory() {
        for (final String menuItem : orders.keySet()) {
            final long itemCount = orders.get(menuItem);
            final long menuID = menuIDs.get(menuItem);

            final Map<Long, Long> invItems = new HashMap<>();
            final String query = String.format("SELECT * FROM %s WHERE menuid = %d",
                    DatabaseNames.RECIPE_ITEM_DATABASE, menuID);
            final ResultSet rs = database.executeQuery(query);
            try {
                while (rs.next()) {
                    final long invID = rs.getLong("inventoryid");
                    final long invCount = rs.getLong("count");
                    invItems.put(invID, invCount);
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (final long invID : invItems.keySet()) {
                final long invCount = invItems.get(invID);
                final long numSold = itemCount * invCount;
                invOrders.put(invNames.get(invID),
                        invOrders.getOrDefault(invNames.get(invID), 0l) + numSold);
            }

        }

        // To-Go Bag
        this.invOrders.put("To-Go Bags", invOrders.getOrDefault("To-Go Bags", 0l) + numOrders);
    }

    /**
     * Retreives the inventory items
     * 
     * @return {@link ObservableList} of {@link SalesReportRow}
     */
    private ObservableList<SalesReportRow> getInvItems() {
        final ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for (final String name : invOrders.keySet())
            items.add(new SalesReportRow(invIDs.get(name), name, invOrders.get(name)));
        return items;
    }
}
