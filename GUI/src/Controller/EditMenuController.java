package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Utils.SessionData;
import Utils.DatabaseConnect;
import Utils.SceneSwitch;

/**
 * Controller for the Edit Menu Screen
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
public class EditMenuController {

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
     * Contains the menu items from database to display in GUI
     */
    private ArrayList<String> menuItems;

        /**
     * {@link Button} Button to navigate order scene
     *
     */
    @FXML private Button orderButton;

    /**
     * {@link Button} Button to navigate order history scene
     *
     */
    @FXML private Button orderHistoryButton;

    /**
     * {@link Button} Button to navigate inventory scene
     *
     */
    @FXML private Button inventoryButton;

    /**
     * {@link Button} Button to navigate employees scene
     *
     */
    @FXML private Button employeesButton;

    /**
     * {@link Button} Button to navigate edit menu scene
     *
     */
    @FXML private Button editMenuButton;

    /**
     * {@link Button} Button to logout
     *
     */
    @FXML private Button logoutButton;

    /**
     * FXML List for view in GUI
     */
    @FXML
    private ListView<String> menuList;

    /**
     * Field to input menu item id
     */
    @FXML
    private TextField menuIDText;

    /**
     * Field to input menu item name
     */
    @FXML
    private TextField menuNameText;

    /**
     * Field to input menu item cost
     */
    @FXML
    private TextField menuCostText;

    /**
     * Field to input menu item number sold
     */
    @FXML
    private TextField menuNumSoldText;

    /**
     * Checkbox to determine if should delete menu item
     */
    @FXML
    private CheckBox isDelete;

    /**
     * Submit button for menu item changes
     */
    @FXML
    private Button SubmitMenuChangeBtn;

    /**
     * Default constructor to prevent errors
     */
    public EditMenuController() {

    }

    /**
     * Allows for passing session data from scene to scene
     */
    public EditMenuController(SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    /**
     * Returns the current session object
     */
    public SessionData getSession() {
        return this.session;
    }

    /**
     * Sets the current session object
     */
    public void setSession(SessionData session) {
        this.session = session;
    }

    /**
     * Loads menu items onto screen from database. Sets other fields to null
     */
    public void initialize() {
        ResultSet rs = getMenuItemsQuery();

        try {
            readMenuItems(rs);
        } catch (SQLException e) {
            System.out.println("COULDN'T READ MENU ITEMS");
            e.printStackTrace();
        }

        addMenuItemsToListView();

        menuIDText.setText(null);
        menuNameText.setText(null);
        menuCostText.setText(null);
        menuNumSoldText.setText(null);
        isDelete.setSelected(false);

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
     * Handle nav bar
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        sceneSwitch = new SceneSwitch(session);
        sceneSwitch.switchScene(event);
    }

    /**
     * Get menu items from database into a ResultSet
     */
    private ResultSet getMenuItemsQuery() {
        String query = "SELECT * FROM menuitem";
        ResultSet rs = database.executeQuery(query);
        return rs;
    }

    /**
     * Puts menu items into this.menuItems from the ResultSet that getMenuItemsQuery
     * returns
     */
    private void readMenuItems(ResultSet rs) throws SQLException {
        menuItems = new ArrayList<>();
        if (rs == null) {
            menuItems.add("No Menu Items Retrieved");
            return;
        }
        while (rs.next()) {
            String curLine = "";
            curLine += "ID: " + Integer.valueOf(rs.getInt("id")).toString() + ", ";
            curLine += "name: " + rs.getString("name") + ", ";
            curLine += "cost: " + rs.getString("cost") + ", ";
            curLine += "numbersold: " + rs.getString("numbersold") + " ";
            menuItems.add(curLine);
        }
    }

    /**
     * Displays menu items
     */
    private void addMenuItemsToListView() {
        ObservableList<String> items = FXCollections.observableArrayList(menuItems);
        menuList.setItems(items);

    }

    /**
     * Submits a menu item edit (add,remove,update)
     */
    @FXML
    private void submitMenuChange(ActionEvent e) {
        Integer itemID = (menuIDText.getText() == null) ? null : Integer.parseInt(menuIDText.getText());
        String itemName = menuNameText.getText();
        Double itemCost = (menuCostText.getText() == null) ? null
                : Double.parseDouble(menuCostText.getText());
        Integer itemNumSold = (menuNumSoldText.getText() == null) ? null
                : Integer.parseInt(menuNumSoldText.getText());

        if (isDelete.isSelected() == true) {
            deleteMenuItem(itemID);
        } else if (checkMenuItemExists(itemID) == true) {
            updateMenuItem(itemID, itemName, itemCost, itemNumSold);
        } else {
            addMenuItem(itemID, itemName, itemCost, itemNumSold);

        }
        initialize();

    }

    /**
     * Check with the database to see if a primary key exists
     */
    private boolean checkMenuItemExists(Integer itemID) {
        String query = "SELECT COUNT(*) FROM menuitem WHERE id = " + itemID.toString() + ";";
        ResultSet rs = database.executeQuery(query);

        try {
            if (rs.next()) {
                int has = rs.getInt(0);
                if (has == 0) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Adds a menu item to database
     */
    private void addMenuItem(Integer ID, String itemName, Double itemCost, Integer itemNumSold) {
        String query = "INSERT INTO menuitem (id, name, cost, numbersold) VALUES (" + ID.toString()
                + ", '" + itemName + "'," + itemCost.toString() + "," + itemNumSold.toString()
                + ");";
        System.out.println(query);
        database.executeQuery(query);
    }

    /**
     * Updates a menu item in the database with user inputed fields
     */
    private void updateMenuItem(Integer itemID, String itemName, Double itemCost,
            Integer itemNumSold) {
        if (itemID == null) {
            return;
        }

        String query = "UPDATE menuitem " + "SET name = '" + itemName + "', cost = "
                + itemCost.toString() + ", numbersold = " + itemNumSold.toString() + " WHERE id = "
                + itemID.toString() + ";";

        System.out.println(query);
        database.executeQuery(query);
    }

    /**
     * deletes a menu item from database
     */
    private void deleteMenuItem(Integer ID) {
        String query = "DELETE FROM menuitem WHERE id = " + ID.toString() + ";";
        System.err.println(query);
        database.executeQuery(query);

    }
}
