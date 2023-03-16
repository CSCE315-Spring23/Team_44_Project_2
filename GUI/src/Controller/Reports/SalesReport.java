package Controller.Reports;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import Items.SalesReportRow;
import Utils.DatabaseConnect;
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
     * {@link TextField} Text field to enter the start date
     */
    @FXML
    TextField startDateText;

    /**
     * {@link TextField} Text field to enter the end date
     */
    @FXML
    TextField endDateText;

    /**
     * {@link Button} Button to generate the Sales report
     */
    @FXML
    Button goButton;

    /**
     * {@link TableView} Table to display the menu items
     */
    @FXML
    TableView<SalesReportRow> menuItemTable;

    /**
     * {@link TableColumn} Column to display the menu item ID
     */
    @FXML
    TableColumn<SalesReportRow, Long> menuIDCol;

    /**
     * {@link TableColumn} Column to display the menu item name
     */
    @FXML
    TableColumn<SalesReportRow, String> menuNameCol;

    /**
     * {@link TableColumn} Column to display the number of menu items sold
     */
    @FXML
    TableColumn<SalesReportRow, Long> menuNumSoldCol;

    /**
     * {@link TableView} Table to display the inventory items
     */
    @FXML
    TableView<SalesReportRow> invItemTable;

    /**
     * {@link TableColumn} Column to display the inventory item ID
     */
    @FXML
    TableColumn<SalesReportRow, Long> invIDCol;

    /**
     * {@link TableColumn} Column to display the inventory item name
     */
    @FXML
    TableColumn<SalesReportRow, String> invNameCol;

    /**
     * {@link TableColumn} Column to display the number of inventory items sold
     */
    @FXML
    TableColumn<SalesReportRow, Long> invNumSoldCol;

    /**
     * {@link Map} Map of menu item names to the number of times they were sold
     */
    private final Map<String, Long> orders;

    /**
     * {@link Map} Map of menu item names to their IDs
     */
    private final Map<String, Long> menuIDs;

    /**
     * {@link Map} Map of menu item IDs to their names
     */
    private final Map<Long, String> menuNames;

    /**
     * {@link Map} Map of inventory item names to the number of times they were sold
     */
    private final Map<String, Long> invOrders;

    /**
     * {@link Map} Map of inventory item names to their IDs
     */
    private final Map<String, Long> invIDs;

    /**
     * {@link Map} Map of inventory item IDs to their names
     */
    private final Map<Long, String> invNames;

    /**
     * {@link Long} Number of orders
     */
    private long numOrders;

    /**
     * Constructor
     *
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public SalesReport(final SessionData session) {
        this.session = session;
        this.database = session.database;

        this.orders = new HashMap<>();
        this.menuIDs = new HashMap<>();
        this.menuNames = new HashMap<>();

        this.invOrders = new HashMap<>();
        this.invIDs = new HashMap<>();
        this.invNames = new HashMap<>();

        this.numOrders = 0;
    }

    /**
     * Initializes the GUI
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
    public void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Sorts both {@link TableView}s by ID in ascending order
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
     * Sets up the {@link TableView} for the menu items
     */
    private void setUpMenuItemTable() {
        // set up columns
        this.menuIDCol.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.menuNameCol.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.menuNumSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    /**
     * Sets up the {@link TableView} for the inventory items
     */
    private void setUpInvItemTable() {
        // set up columns
        this.invIDCol.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.invNameCol.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.invNumSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    /**
     * Sets up the {@link Map}s for the menu and inventory items from the database
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
                menuNames.put(id, name);
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
     * Generates the Sales Report for the given dates
     * in the {@link #startDateText} and {@link #endDateText} {@link TextField}s
     */
    public void onGoClick() {
        final String startDate = this.startDateText.getText();
        final String endDate = this.endDateText.getText();

        // format check for dates
        if (!startDate.matches("\\d{4}-\\d{2}-\\d{2}")
                || !endDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format");
            return;
        }

        this.getOrderItemsBetweenDates(startDate, endDate);
        this.menuItemTable.setItems(this.getMenuItems());

        this.getInventory();
        this.invItemTable.setItems(this.getInvItems());

        this.sortTableByID();
    }

    /**
     * Gets all the {@link OrderItem}s between the given dates
     *
     * @param startDate {@link String} of the start date
     * @param endDate   {@link String} of the end date
     */
    private void getOrderItemsBetweenDates(final String startDate, final String endDate) {

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
     * Adds all the {@link SalesReportRow}s to the {@link TableView}
     * for the menu items
     * @return {@link ObservableList} of {@link SalesReportRow}s
     */
    private ObservableList<SalesReportRow> getMenuItems() {
        final ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for (String name : orders.keySet())
            items.add(new SalesReportRow(menuIDs.get(name), name, orders.get(name)));
        return items;
    }

    /**
     * Gets the inventory items needed for the menu items
     * based on the {@link #orders} {@link Map}
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
     * Adds all the {@link SalesReportRow}s to the {@link TableView}
     * for the inventory items
     * @return {@link ObservableList} of {@link SalesReportRow}s
     */
    private ObservableList<SalesReportRow> getInvItems() {
        final ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for (final String name : invOrders.keySet())
            items.add(new SalesReportRow(invIDs.get(name), name, invOrders.get(name)));
        return items;
    }
}
