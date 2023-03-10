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

    /**
     * {@link TextField} to allow for user input to update a {@link MenuItem}
     */
    @FXML
    private TextField updateIDText;

    /**
     * {@link TextField} to allow for user input to update a {@link MenuItem}
     */
    @FXML
    private TextField updateNameText;

    /**
     * {@link TextField} to allow for user input to update a {@link MenuItem}
     */
    @FXML
    private TextField updateCostText;

    /**
     * {@link TextField} to allow for user input to update a {@link MenuItem}
     */
    @FXML
    private TextField updateRecipeText;

    /**
     * {@link TextField} to allow for user input to add a {@link MenuItem}
     */
    @FXML
    private TextField addNameText;

    /**
     * {@link TextField} to allow for user input to add a {@link MenuItem}
     */
    @FXML
    private TextField addCostText;

    /**
     * {@link TextField} to allow for user input to add a {@link MenuItem}
     */
    @FXML
    private TextField addRecipeText;

    /**
     * {@link TextField} to allow for user input to remove a {@link MenuItem}
     */
    @FXML
    private TextField deleteItemID;

    /**
     * {@link Button} that will trigger {@link #updateItemClicked(ActionEvent)}
     */
    @FXML
    private Button updateItemButton;

    /**
     * {@link Button} that will trigger {@link #addItemClicked(ActionEvent)}
     */
    @FXML
    private Button addItemButton;

    /**
     * {@link Button} that will trigger {@link #deleteItemClicked(ActionEvent)}
     */
    @FXML
    private Button deleteItemButton;

    /**
     * Default constructor to prevent errors
     */
    public EditMenuController() {}

    /**
     * Allows for passing session data from scene to scene
     * 
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public EditMenuController(final SessionData session) {
        this.session = session;
        this.database = this.session.database;
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
     * Check if a primary key exists within the database
     * 
     * @param itemID ID number of the item
     * @return {@code true} if found, {@code false} otherwise
     * @deprecated
     */
    @Deprecated
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
     * Update existing item in the menuitem table. First checks if the item exists, then updates its
     * values that the user entered that aren't null, then updates the recipeitem table
     * 
     * @param e {@link ActionEvent} of {@link #updateItemButton}
     */
    public void updateItemClicked(ActionEvent e) {
        final String idText = this.updateIDText.getText();
        String cost = this.updateCostText.getText();
        String name = this.updateNameText.getText();

        long itemID = -1L;
        try {
            itemID = Long.valueOf(idText);
        } catch (Exception exc) {
            System.err.println("Invalid ID Provided");
        }

        if (itemID < 0L) {
            return;
        }
        if (cost != null) {
            cost = "cost = " + cost;
        }
        if (name != null) {
            name = "name = " + name;
        }
        // Update Menu Item Table
        String query = String.format("UPDATE menuitem SET {} {} WHERE id = {}", name, cost, itemID);
        database.executeUpdate(query);

        // update recipe table
        // TODO create functions clearRecipe, parseRecipeString, addRecipeItems

    }

    /**
     * If item doesn't exist, it adds it to menu table and adds its recipe to the recipeitem table
     * 
     * @param e {@link ActionEvent} of {@link #addItemButton}
     */
    public void addItemClicked(ActionEvent e) {

    }

    /**
     * Remove item from menu table and recipe item table if it exists
     * 
     * @param e {@link ActionEvent} of {@link #deleteItemClicked(ActionEvent)}
     */
    public void deleteItemClicked(ActionEvent e) {

    }

}
