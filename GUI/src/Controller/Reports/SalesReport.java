package Controller.Reports;

import Items.SalesReportRow;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;


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


    @FXML TextField startDateText;

    @FXML TextField endDateText;

    @FXML Button goButton;

    @FXML TableView<SalesReportRow> menuItemTable;

    @FXML TableColumn<SalesReportRow, Long> menuIDCol;

    @FXML TableColumn<SalesReportRow, String> menuNameCol;

    @FXML TableColumn<SalesReportRow, Long> menuNumSoldCol;

    @FXML TableView<SalesReportRow> invItemTable;

    @FXML TableColumn<SalesReportRow, Long> invIDCol;

    @FXML TableColumn<SalesReportRow, String> invNameCol;

    @FXML TableColumn<SalesReportRow, Long> invNumSoldCol;



    private HashMap<String, Long> orders;
    private HashMap<String, Long> menuIDs;
    private HashMap<Long, String> menuNames;

    private HashMap<String, Long> invOrders;
    private HashMap<String, Long> invIDs;
    private HashMap<Long, String> invNames;

    private long numOrders;

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

    public void initialize() {
        setUpHashMap();
        setUpMenuItemTable();
        setUpInvItemTable();

        // set visibility of buttons based on employee role
        if (session.isManager()) {
            System.out.println("Manager");
            editMenuButton.setVisible(true);
            inventoryButton.setVisible(true);
            employeesButton.setVisible(true);
        } else {
            System.out.println("Employee");
            editMenuButton.setVisible(false);
            inventoryButton.setVisible(false);
            employeesButton.setVisible(false);
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

    private void sortTableByID(){

        //sort table by id by default
        menuItemTable.getSortOrder().add(menuIDCol);
        menuIDCol.setSortType(TableColumn.SortType.ASCENDING);
        menuItemTable.sort();

        menuItemTable.refresh();

        invItemTable.getSortOrder().add(invIDCol);
        invIDCol.setSortType(TableColumn.SortType.ASCENDING);
        invItemTable.sort();

        invItemTable.refresh();
    }

    private void setUpMenuItemTable(){
        //set up columns
        menuIDCol.setCellValueFactory(cellData -> cellData.getValue().getId());
        menuNameCol.setCellValueFactory(cellData -> cellData.getValue().getName());
        menuNumSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    private void setUpInvItemTable(){
        //set up columns
        invIDCol.setCellValueFactory(cellData -> cellData.getValue().getId());
        invNameCol.setCellValueFactory(cellData -> cellData.getValue().getName());
        invNumSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }


    private void setUpHashMap(){
        //set up all menu items
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.MENU_ITEM_DATABASE));
            while(rs.next()){
                final String name = rs.getString("name");
                final long id = rs.getLong("id");
                orders.put(name, 0l);
                menuIDs.put(name, id);
                menuNames.put(id, name);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //set up all inventory items
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.INVENTORY_DATABASE));
            while(rs.next()){
                final String name = rs.getString("name");
                final long id = rs.getLong("id");
                invOrders.put(name, 0l);
                invIDs.put(name, id);
                invNames.put(id, name);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onGoClick(){
        final String startDate = startDateText.getText();
        final String endDate = endDateText.getText();

        System.out.println(startDate + " " + endDate);
        //format check for dates
        if(!startDate.matches("\\d{4}-\\d{2}-\\d{2}") || !endDate.matches("\\d{4}-\\d{2}-\\d{2}")){
            System.out.println("Invalid date format");
            return;
        }

        getOrderItemsBetweenDates(startDate, endDate);
        menuItemTable.setItems(getMenuItems());

        getInventory();
        invItemTable.setItems(getInvItems());

        sortTableByID();
    }

    private void getOrderItemsBetweenDates(final String startDate, final String endDate) {

        try{
            // ResultSet rs = database.executeQuery(String.format("SELECT name, count(*) as total_sold from %s join %s on orderitem.id =
            // solditem.orderid join menuitem on solditem.menuid = menuitem.id where Date(orderitem.date) >= \'%s\' AND Date(orderitem.date) <= \'%s\'
            // group by name order by total_sold desc", startDate, endDate));
            ResultSet rs = database.executeQuery(String.format("SELECT name, count(*) AS totalSold FROM %1$s" +
                " join %2$s ON %1$s.id = %2$s.orderid" +
                " join %3$s ON %2$s.menuid = %3$s.id" +
                " WHERE Date(%1$s.date) >= \'%4$s\' AND Date(%1$s.date) <= \'%5$s\'" +
                " GROUP By name ORDER BY totalSold DESC",
                DatabaseNames.ORDER_ITEM_DATABASE, DatabaseNames.SOLD_ITEM_DATABASE, DatabaseNames.MENU_ITEM_DATABASE, startDate, endDate));
            while(rs.next()){
                String name = rs.getString("name");
                long numSold = rs.getLong("totalSold");
                orders.put(name, numSold);
                numOrders++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ObservableList<SalesReportRow> getMenuItems(){
        ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for(String name : orders.keySet()){
            items.add(new SalesReportRow(menuIDs.get(name), name, orders.get(name)));
        }
        return items;
    }


    private void getInventory(){
        for(String menuItem : orders.keySet()){
            long itemCount = orders.get(menuItem);
            long menuID = menuIDs.get(menuItem);

            HashMap<Long, Long> invItems = new HashMap<>();
            try{
                ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s WHERE menuid = %d",
                    DatabaseNames.RECIPE_ITEM_DATABASE, menuID));
                while(rs.next()){
                    long invID = rs.getLong("inventoryid");
                    long invCount = rs.getLong("count");
                    invItems.put(invID, invCount);
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for(long invID : invItems.keySet()){
                long invCount = invItems.get(invID);
                long numSold = itemCount * invCount;
                invOrders.put(invNames.get(invID), invOrders.getOrDefault(invNames.get(invID), 0l) + numSold);
            }

        }
        //To-Go Bag
        invOrders.put("To-Go Bags", invOrders.getOrDefault("To-Go Bags", 0l) + numOrders);
    }


    private ObservableList<SalesReportRow> getInvItems(){
        ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for(String name : invOrders.keySet()){
            items.add(new SalesReportRow(invIDs.get(name), name, invOrders.get(name)));
        }
        return items;
    }

}
