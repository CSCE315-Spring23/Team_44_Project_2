package Controller.Reports;


import java.io.IOException;
import java.sql.ResultSet;

import Items.RestockReportRow;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
     *
     */
    @FXML
    private Button logoutButton;

    @FXML
    private TableView<RestockReportRow> restockReportTable;

    @FXML
    private TableColumn<RestockReportRow, Integer> itemId;

    @FXML
    private TableColumn<RestockReportRow, String> itemName;

    @FXML
    private TableColumn<RestockReportRow, Integer> itemQuantity;


    public RestockReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    public void initialize() {
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

        populateTable();
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

        // TODO: manually add threshold values into the database
        String query = "SELECT * FROM " + DatabaseNames.INVENTORY_DATABASE
            + " WHERE quantity < threshold";
        try {
            ResultSet rSet = database.executeQuery(query);

            while (rSet.next()) {
                RestockReportRow row = new RestockReportRow(rSet.getInt("id"), 
                    rSet.getString("name"), rSet.getInt("quantity"));
                this.restockReportTable.getItems().add(row);
            }

            rSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
