package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import Items.MenuItem;
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

        this.clearTextFields();

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
     * Update existing item in the menuitem table. First checks if the item exists, then updates its
     * values that the user entered that aren't null, then updates the recipeitem table
     * 
     * @param e {@link ActionEvent} of {@link #updateItemButton}
     */
    public void updateItemClicked(ActionEvent e) {
        System.out.println("Updaing item in the menu");
        final String name = this.updateNameText.getText();

        final long itemID;
        try {
            itemID = Long.valueOf(this.updateIDText.getText());
        } catch (final NumberFormatException nfe) {
            System.err.println("Invalid ID Provided");
            return;
        }

        final double cost;
        try {
            cost = Double.parseDouble(this.updateCostText.getText());
        } catch (final NumberFormatException nfe) {
            System.err.println("Invalid Cost provided");
            return;
        }

        if (itemID < 0L)
            return;
        if (cost < 0d)
            return;
        if (name == null)
            return;
        if (name.isEmpty())
            return;

        // Update Menu Item Table
        final String query = String.format("UPDATE menuitem SET name=%s cost=%.2f WHERE id=%d",
                name, cost, itemID);
        this.database.executeUpdate(query);

        // update recipe table
        this.removeItemsFromRecipe(itemID);
        final String[] recipe = this.updateRecipeText.getText().split(",");
        final Map<Long, Long> inventoryCountMap = this.parseRecipe(recipe);
        this.addItemsToRecipe(inventoryCountMap, itemID);
        this.updateTable();
        this.clearTextFields();
    }

    /**
     * If item doesn't exist, it adds it to menu table and adds its recipe to the recipeitem table
     * 
     * @param e {@link ActionEvent} of {@link #addItemButton}
     */
    public void addItemClicked(final ActionEvent e) {
        System.out.println("Adding item to menu");
        final long menuID =
                DatabaseUtils.getLastId(this.database, DatabaseNames.MENU_ITEM_DATABASE) + 1;
        final String name = this.addNameText.getText();
        final double cost;
        try {
            cost = Double.parseDouble(this.addCostText.getText());
        } catch (NumberFormatException nfe) {
            System.err.println("Failed to parse price");
            return;
        }

        // Parse addRecipeText
        final String[] recipe = this.addRecipeText.getText().split(",");
        final Map<Long, Long> inventoryCountMap = this.parseRecipe(recipe);

        // Check if any recipe item exists within the inventory
        for (final long item : inventoryCountMap.keySet())
            if (!DatabaseUtils.hasItem(this.database, item, DatabaseNames.INVENTORY_DATABASE)) {
                System.err.println("Inventory does not have item with ID:\t" + item);
                return;
            }

        final String query = String.format(
                "INSERT INTO %s (id, name, cost, numbersold) VALUES (%d, \'%s\', %.2f, 0)",
                DatabaseNames.MENU_ITEM_DATABASE, menuID, name, cost);
        this.database.executeUpdate(query);

        this.addItemsToRecipe(inventoryCountMap, menuID);
        this.updateTable();
        this.clearTextFields();
    }

    /**
     * Remove item from menu table and recipe item table if it exists
     * 
     * @param e {@link ActionEvent} of {@link #deleteItemClicked(ActionEvent)}
     */
    public void deleteItemClicked(final ActionEvent e) {
        System.out.println("Deleting item from menu");
        final long menuID;
        try {
            menuID = Long.parseLong(this.deleteItemID.getText());
        } catch (NumberFormatException nfe) {
            System.err.println("Failed to parse menuID");
            return;
        }

        final String query = String.format("DELETE FROM %s WHERE id=%d",
                DatabaseNames.MENU_ITEM_DATABASE, menuID);
        this.database.executeUpdate(query);
        this.removeItemsFromRecipe(menuID);
        this.updateTable();
        this.clearTextFields();
    }

    /**
     * Reset all text fields that get user input for editing a menu item to null
     */
    private void clearTextFields() {
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
     * Parses the recipe list into a {@link Map} maping from {@link Long} to {@link Long}. The Key
     * is the ID number of the recipe item, the Value is the amount of that item in the recipe.
     * 
     * @param recipe array of {@link String} holding the recipe
     * @return {@link Map} of {@link Long} to {@link Long}
     */
    private Map<Long, Long> parseRecipe(final String[] recipe) {
        final Map<Long, Long> map = new HashMap<>();
        for (final String item : recipe) {
            final long id;
            try {
                id = Long.parseLong(item);
            } catch (NumberFormatException nfe) {
                throw nfe;
            }

            if (map.containsKey(id))
                map.put(id, map.get(id) + 1l);
            else
                map.put(id, 1l);
        }

        return map;
    }

    /**
     * Adds items to the recipe table
     * 
     * @param recipeMap {@link Map} parsed by {@link #parseRecipe(String[])}
     * @param menuID ID number of the menu item
     */
    private void addItemsToRecipe(final Map<Long, Long> recipeMap, final long menuID) {
        for (final Map.Entry<Long, Long> node : recipeMap.entrySet()) {
            final long recipeID =
                    DatabaseUtils.getLastId(this.database, DatabaseNames.RECIPE_ITEM_DATABASE) + 1;
            final long inventoryID = node.getKey();
            final long count = node.getValue();
            final String query = String.format(
                    "INSERT INTO %s (id, inventoryid, menuid, count) VALUES (%d, %d, %d, %d);",
                    DatabaseNames.RECIPE_ITEM_DATABASE, recipeID, inventoryID, menuID, count);
            this.database.executeUpdate(query);
        }
    }

    /**
     * Remove all recipe items from the recipe table that have a specified menu ID number
     * 
     * @param menuID ID number of the menu to remove from the recipe table.
     */
    private void removeItemsFromRecipe(final long menuID) {
        final String query = String.format("DELETE FROM %s WHERE menuid=%d",
                DatabaseNames.RECIPE_ITEM_DATABASE, menuID);
        this.database.executeUpdate(query);
    }
}
