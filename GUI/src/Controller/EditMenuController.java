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
    private TextField deleteIDText;

    @FXML
    private Button updateItemButton;

    @FXML
    private Button addItemButton;

    @FXML
    private Button deleteItemButton;

    /**
     * Default constructor to prevent errors
     */
    public EditMenuController() {
    }

    /**
     * Allows for passing session data from scene to scene
     */
    public EditMenuController(final SessionData session) {
        this.session = session;
        this.database = this.session.database;
    }

    /**
     * Returns the current session object
     * @deprecated
     */
    public SessionData getSession() {
        return this.session;
    }

    /**
     * Sets the current session object
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
    public void setEditTextFieldsNull(){
        updateIDText.setText(null);
        updateNameText.setText(null);
        updateCostText.setText(null);
        updateNumSoldText.setText(null);
        updateRecipeText.setText(null);

        addNameText.setText(null);
        addCostText.setText(null);
        addRecipeText.setText(null);

        deleteIDText.setText(null);
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
            final String query = String.format("SELECT * FROM %s ORDER BY id", DatabaseNames.MENU_ITEM_DATABASE);
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
        // final long itemID = Long.parseLong(this.menuIDText.getText());
        // final String itemName = this.menuNameText.getText();
        // final double itemCost = this.menuCostText.getText() == null ? -1d
        // : Double.parseDouble(this.menuCostText.getText());
        // final long itemNumSold = this.menuNumSoldText.getText() == null ? -1l
        // : Long.parseLong(this.menuNumSoldText.getText());

        // if (this.isDelete.isSelected()) {
        // this.deleteMenuItem(itemID);
        // } else if (this.checkMenuItemExists(itemID)) {
        // this.updateMenuItem(itemID, itemName, itemCost, itemNumSold);
        // } else {
        // this.addMenuItem(itemID, itemName, itemCost, itemNumSold);
        // }

        initialize();

    }

    /**
     * Check if a primary key exists within the database
     * 
     * @param itemID ID number of the item
     * @return {@code true} if found, {@code false} otherwise
     * @deprecated
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
     * @param itemID      identification number
     * @param itemName    name of the item
     * @param itemCost    cost of the item
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
     * @param itemID      identification number
     * @param itemName    name of the item
     * @param itemCost    cost of the item
     * @param itemNumSold number of sales
     * @deprecated
     */
    private void updateMenuItem(final long itemID, final String itemName, final double itemCost,
            final long itemNumSold) {
        if (itemID <= 0l) {
            System.out.println("Invalid ID number.\nAbort updating item.");
            return;
        }

        final String query = String.format("UPDATE %s SET = \'%s\', cost = %.2f, numbersold = %d WHERE id %d;",
                DatabaseNames.MENU_ITEM_DATABASE, itemName, itemCost, itemNumSold, itemID);

        // System.out.println(query);
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
}
