package Controller.Reports;

import java.io.IOException;
import java.sql.ResultSet;

import Items.InventoryItem;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Controller for the restock report window
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
public class RestockReport {
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
     */
    @FXML
    private Button logoutButton;

    /**
     * {@link TableView} of {@link InventoryItem} to display in the graphical user interface
     */
    @FXML
    private TableView<InventoryItem> restockReportTable;

    /**
     * {@link TableColumn} displaying the {@link Items.InventoryItem} ID number
     */
    @FXML
    private TableColumn<InventoryItem, Long> itemId;

    /**
     * {@link TableColumn} displaying the {@link Items.InventoryItem} name
     */
    @FXML
    private TableColumn<InventoryItem, String> itemName;

    /**
     * {@link TableColumn} displaying the {@link Items.InventoryItem} stock
     */
    @FXML
    private TableColumn<InventoryItem, Long> itemQuantity;

    /**
     * Constructor
     * 
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public RestockReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    public void initialize() {
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

        this.populateTable();
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

    private void populateTable() {
        this.itemId.setCellValueFactory(cell -> cell.getValue().getId());
        this.itemName.setCellValueFactory(cell -> cell.getValue().getName());
        this.itemQuantity.setCellValueFactory(cell -> cell.getValue().getQuantity());

        final String query = String.format("SELECT * FROM %s WHERE quantity < threshold",
                DatabaseNames.INVENTORY_DATABASE);
        final ResultSet rSet = this.database.executeQuery(query);
        try {
            while (rSet.next()) {
                final InventoryItem row = new InventoryItem(rSet.getInt("id"),
                        rSet.getString("name"), rSet.getInt("quantity"));
                this.restockReportTable.getItems().add(row);
            }

            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
