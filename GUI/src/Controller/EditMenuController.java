package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import Items.MenuItem;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
     * {@link Button} Button to logout
     *
     */
    @FXML
    private Button logoutButton;

    /**
     * {@link TableView} of {@link MenuItem} to display
     */
    @FXML
    private TableView<MenuItem> menuTable;

    /**
     * {@link TableColumn} displaying the ID number of {@link MenuItem}
     */
    @FXML
    private TableColumn<MenuItem, Long> menuID;

    /**
     * {@link TableColumn} displaying the name of the {@link MenuItem}
     */
    @FXML
    private TableColumn<MenuItem, String> menuName;

    /**
     * {@link TableColumn} displaying the price of the {@link MenuItem}
     */
    @FXML
    private TableColumn<MenuItem, Double> menuPrice;

    /**
     * {@link TableColumn} displaying the quantity of {@link MenuItem} sold
     */
    @FXML
    private TableColumn<MenuItem, Long> numberSold;

    @FXML
    private TextField updateIDText;

    @FXML
    private TextField updateNameText;

    @FXML
    private TextField updateCostText;

    @FXML
    private TextField updateNumSoldText;

    @FXML
    private TextField updateRecipeText;

    @FXML
    private TextField addNameText;

    @FXML
    private TextField addCostText;

    @FXML
    private TextField addRecipeText;

    @FXML
    private TextField deleteItemID;

    @FXML
    private Button updateItemButton;

    @FXML
    private Button addItemButton;

    @FXML
    private Button deleteItemButton;

    /**
     * Default constructor to prevent errors
     */
    public EditMenuController() {}

    /**
     * Allows for passing session data from scene to scene
     */
    public EditMenuController(final SessionData session) {
        this.session = session;
        this.database = this.session.database;
    }

    /**
     * Returns the current session object
     * 
     * @deprecated
     */
    public SessionData getSession() {
        return this.session;
    }

    /**
     * Sets the current session object
     * 
     * @deprecated
     */
    public void setSession(final SessionData session) {
        this.session = session;
    }

    /**
     * Loads menu items onto screen from database. Sets other fields to null
     */
    public void initialize() {
        this.setUpTable();
        this.updateTable();

        setEditTextFieldsNull();

        if (this.session.isManager()) {
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
     * Reset all text fields that get user input for editing a menu item to null
     */
    public void setEditTextFieldsNull() {
        this.updateIDText.setText("");
        this.updateNameText.setText("");
        this.updateCostText.setText("");
        this.updateNumSoldText.setText("");
        this.updateRecipeText.setText("");

        this.addNameText.setText("");
        this.addCostText.setText("");
        this.addRecipeText.setText("");

        this.deleteItemID.setText("");
    }

    /**
     * Initialized the columns for {@link #menuTable}
     */
    private void setUpTable() {
        this.menuID.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.menuName.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.menuPrice.setCellValueFactory(cellData -> cellData.getValue().getPrice());
        this.numberSold.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    /**
     * Handle switching scenes through the navigation bar
     * 
     * @param event {@link ActionEvent} of the {@link Button} pressed
     * @throws IOException if loading the new GUI failed
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Gets all menu items from the database
     * 
     * @return {@link ObservableList} of {@link MenuItem}
     */
    private ObservableList<MenuItem> getMenuItems() {
        final ObservableList<MenuItem> menu = FXCollections.observableArrayList();
        try {
            final String query =
                    String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.MENU_ITEM_DATABASE);
            final ResultSet rs = database.executeQuery(query);
            while (rs.next()) {
                final long id = rs.getLong("id");
                final String name = rs.getString("name");
                final double price = rs.getDouble("cost");
                final long quant = rs.getLong("numbersold");

                if (id == 0l)
                    continue;
                menu.add(new MenuItem(id, name, price, quant));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return menu;
    }

    /**
     * Updates {@link #menuTable} in the Graphical User Interface
     */
    private void updateTable() {
        this.menuTable.setItems(this.getMenuItems());
        this.menuTable.refresh();
    }

    /**
     * Submits a menu item edit (add, remove, update)
     * 
     * @param e {@link ActionEvent} of the {@link Button} pressed
     * @deprecated
     */
    @FXML
    private void submitMenuChange(ActionEvent e) {
        this.initialize();
    }

    /**
     * Check if a primary key exists within the database
     * 
     * @param itemID ID number of the item
     * @return {@code true} if found, {@code false} otherwise
     * 
     */
    private boolean checkMenuItemExists(final long itemID) {
        if (itemID <= 0l)
            return false;

        final String query = String.format("SELECT COUNT(*) FROM menuitem WHERE id = %d;", itemID);
        final ResultSet rs = database.executeQuery(query);

        try {
            if (rs.next()) {
                long has = rs.getLong(0);
                return has != 0l;
            } else
                return false;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Adds a menu item to database
     * 
     * @param itemID identification number
     * @param itemName name of the item
     * @param itemCost cost of the item
     * @param itemNumSold number of sales
     * @deprecated
     */
    private void addMenuItem(final long itemID, final String itemName, final double itemCost,
            final long itemNumSold) {
        if (itemID <= 0l) {
            System.out.println("Invalid ID number.\nAbort adding item.");
            return;
        }

        final String query = String.format(
                "INSERT INTO %s (id, name, cost, numbersold) VALUES (%d, '%s', %.2f, %d);",
                DatabaseNames.MENU_ITEM_DATABASE, itemID, itemName, itemCost, itemNumSold);
        // System.out.println(query);
        this.database.executeQuery(query);
    }

    /**
     * Updates a menu item in teh database
     * 
     * @param itemID identification number
     * @param itemName name of the item
     * @param itemCost cost of the item
     * @param itemNumSold number of sales
     * @deprecated
     */
    private void updateMenuItem(final long itemID, final String itemName, String itemCost) {
        if (itemID <= 0l) {
            System.out.println("Invalid ID number.\nAbort updating item.");
            return;
        }

        final String query = String.format("UPDATE %s SET name = \'%s\', cost = %s WHERE id %d;",
                DatabaseNames.MENU_ITEM_DATABASE, itemName, itemCost, itemID);

        System.out.println(query);
        this.database.executeQuery(query);
    }

    /**
     * deletes a menu item from database
     * 
     * @param itemID identification number
     * @deprecated
     */
    private void deleteMenuItem(final long itemID) {
        if (itemID <= 0l) {
            System.out.println("Invalid ID number.\nAbort deleting item.");
            return;
        }

        final String query = String.format("DELETE FROM %s WHERE id = %d;",
                DatabaseNames.MENU_ITEM_DATABASE, itemID);
        // System.out.println(query);
        this.database.executeUpdate(query);
    }

    /**
     * Update existing item in the menuitem table. First checks if the item exists, then updates its
     * values that the user entered that aren't null, then updates the recipeitem table
     * 
     * @param e
     */
    public void updateItemClicked(ActionEvent e) {
        final String idText = updateIDText.getText();
        final String costText = updateCostText.getText();
        final String nameText = updateNameText.getText();
        final String recipeText = updateRecipeText.getText();

        long itemID;
        double itemCost;
        if (idText == null) {
            System.err.println("No ID provided");
            return;
        }

        try {
            itemID = Integer.parseInt(idText);
        } catch (Exception error) {
            // TODO: handle exception
            System.err.println("Error in input values");
            return;
        }

        if (checkMenuItemExists(itemID) == false) {
            return;
        }



    }

    /**
     * If item doesn't exist, it adds it to menu table and adds its recipe to the recipeitem table
     * 
     * @param e
     */
    public void addItemClicked(ActionEvent e) {

    }

    /**
     * Remove item from menu table and recipe item table if it exists
     * 
     * @param e
     */
    public void deleteItemClicked(ActionEvent e) {

    }

}
