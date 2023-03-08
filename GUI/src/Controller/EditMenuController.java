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

    @FXML
    private TableColumn<MenuItem, Long> menuID;

    @FXML
    private TableColumn<MenuItem, String> menuName;

    @FXML
    private TableColumn<MenuItem, Double> menuPrice;

    @FXML
    private TableColumn<MenuItem, Long> numberSold;

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
    public EditMenuController(final SessionData session) {
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
    public void setSession(final SessionData session) {
        this.session = session;
    }

    /**
     * Loads menu items onto screen from database. Sets other fields to null
     */
    public void initialize() {
        this.setUpTable();
        this.updateTable();

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

    private void setUpTable() {
        this.menuID.setCellValueFactory(cellData -> cellData.getValue().getId());
        this.menuName.setCellValueFactory(cellData -> cellData.getValue().getName());
        this.menuPrice.setCellValueFactory(cellData -> cellData.getValue().getPrice());
        this.numberSold.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    /**
     * Handle nav bar
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    private ObservableList<MenuItem> getMenuItems() {
        ObservableList<MenuItem> menu = FXCollections.observableArrayList();
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

    private void updateTable() {
        this.menuTable.setItems(this.getMenuItems());
        this.menuTable.refresh();
    }

    /**
     * Submits a menu item edit (add,remove,update)
     */
    @FXML
    private void submitMenuChange(ActionEvent e) {
        Integer itemID =
                (menuIDText.getText() == null) ? null : Integer.parseInt(menuIDText.getText());
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
        final String query =
                String.format("SELECT COUNT(*) FROM menuitem WHERE id = %s;", itemID.toString());
        final ResultSet rs = database.executeQuery(query);

        try {
            if (rs.next()) {
                int has = rs.getInt(0);
                return has != 0;
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
        final String query = String.format(
                "INSERT INTO %s (id, name, cost, numbersold) VALUES (%s, '%s', %s, %s);",
                DatabaseNames.MENU_ITEM_DATABASE, ID.toString(), itemName, itemCost.toString(),
                itemNumSold.toString());
        System.out.println(query);
        this.database.executeQuery(query);
    }

    /**
     * Updates a menu item in the database with user inputed fields
     */
    private void updateMenuItem(Integer itemID, String itemName, Double itemCost,
            Integer itemNumSold) {
        if (itemID == null) {
            return;
        }

        final String query =
                String.format("UPDATE %s SET = \'%s\', cost = %s, numbersold = %s WHERE id %s;",
                        DatabaseNames.MENU_ITEM_DATABASE, itemName, itemCost.toString(),
                        itemNumSold.toString(), itemID.toString());

        System.out.println(query);
        this.database.executeQuery(query);
    }

    /**
     * deletes a menu item from database
     */
    private void deleteMenuItem(Integer ID) {
        final String query = String.format("DELETE FROM menuitem WHERE id = %s;", ID.toString());
        System.out.println(query);
        this.database.executeQuery(query);
    }
}
