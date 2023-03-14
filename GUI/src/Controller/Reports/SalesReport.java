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

    private HashMap<String, Long> invOrders;
    private HashMap<String, Long> invIDs;

    private long numOrders;

    public SalesReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
        this.orders = new HashMap<>();
        this.menuIDs = new HashMap<>();
        this.invOrders = new HashMap<>();
        this.invIDs = new HashMap<>();
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

    private ObservableList<SalesReportRow> getMenuItems(){
        ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for(String name : orders.keySet()){
            items.add(new SalesReportRow(menuIDs.get(name), name, orders.get(name)));
        }
        return items;
    }

    private void setUpHashMap(){
        //set up all menu items
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.MENU_ITEM_DATABASE));
            while(rs.next()){
                orders.put(rs.getString("name"), 0l);
                menuIDs.put(rs.getString("name"), rs.getLong("id"));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //set up all inventory items
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.INVENTORY_DATABASE));
            while(rs.next()){
                invOrders.put(rs.getString("name"), 0l);
                invIDs.put(rs.getString("name"), rs.getLong("id"));
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
        getInventory();
        menuItemTable.setItems(getMenuItems());
        invItemTable.setItems(getInvItems());
        sortTableByID();
    }

    private void getOrderItemsBetweenDates(final String startDate, final String endDate) {
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s WHERE Date(date) >= \'%s\' AND Date(date) <= \'%s\'",
                DatabaseNames.ORDER_ITEM_DATABASE, startDate, endDate));
            while(rs.next()){
                int orderID = rs.getInt("id");
                ArrayList<Long> items = getSoldItems(orderID);

                for(long itemID : items){
                    String name = getMenuItemName(itemID);
                    orders.put(name, orders.getOrDefault(name, 0l) + 1);
                }
                numOrders++;
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Long> getSoldItems(final long orderID){
        ArrayList<Long> items = new ArrayList<>();
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s WHERE orderid = %d",
                    DatabaseNames.SOLD_ITEM_DATABASE, orderID));
            while(rs.next()){
                long itemID = rs.getLong("menuid");
                items.add(itemID);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    private String getMenuItemName(final long itemID){
        String name = "";
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %S WHERE id = %d",
                DatabaseNames.MENU_ITEM_DATABASE, itemID));
            while(rs.next()){
                name = rs.getString("name");
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
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
                invOrders.put(getInvName(invID), invOrders.getOrDefault(getInvName(invID), 0l) + numSold);
            }

            //To-Go Bag

        }

        invOrders.put("To-Go Bags", invOrders.getOrDefault("To-Go Bags", 0l) + numOrders);
    }

    private String getInvName(final long invID){
        String name = "";
        try{
            ResultSet rs = database.executeQuery(String.format("SELECT * FROM %s WHERE id = %d",
                DatabaseNames.INVENTORY_DATABASE, invID));

            while(rs.next()){
                name = rs.getString("name");
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private ObservableList<SalesReportRow> getInvItems(){
        ObservableList<SalesReportRow> items = FXCollections.observableArrayList();
        for(String name : invOrders.keySet()){
            items.add(new SalesReportRow(invIDs.get(name), name, invOrders.get(name)));
        }
        return items;
    }

}
