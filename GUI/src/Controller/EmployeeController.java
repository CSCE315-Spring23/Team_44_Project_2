package Controller;

import Utils.DatabaseConnect;
import Utils.SceneSwitch;
import Utils.DatabaseNames;
import Utils.SessionData;
import Items.EmployeeRow;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * Controller for the Employee Screen
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
public class EmployeeController {
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
     * {@link ResourceBundle} the was given to the FXMLLoader
     */
    @FXML
    private ResourceBundle resources;

    /**
     * {@link URL} location of the FXML file that was given to the FXMLLoader
     */
    @FXML
    private URL location;

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
     * {@link TableView} of {@link EmployeeRow} to display the Employee database
     */
    @FXML
    private TableView<EmployeeRow> employeeTable;

    /**
     * {@link TableColumn} displaying the identification number of all Employees
     */
    @FXML
    private TableColumn<EmployeeRow, Integer> randomID;

    /**
     * {@link TableColumn} displaying the name of all Employees
     */
    @FXML
    private TableColumn<EmployeeRow, String> employeeName;

    /**
     * {@link TableColumn} displaying the roles of all Employees
     */
    @FXML
    private TableColumn<EmployeeRow, String> role;

    /**
     * {@link TableColumn} displaying the pin number of all Employees
     */
    @FXML
    private TableColumn<EmployeeRow, Integer> employeePin;

    /**
     * Constructor
     * 
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public EmployeeController(final SessionData session) {
        this.session = session;
        this.database = this.session.database;
    }

    /**
     * Initialize
     */
    public void initialize() {
        this.setUpTable();
        this.updateTable();

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
     * Set up columns in the {@link #employeeTable}
     */
    private void setUpTable() {
        // define TableView columns
        this.randomID.setCellValueFactory(cellData -> cellData.getValue().randomIDProperty());
        this.employeeName
                .setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());
        this.role.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        this.employeePin.setCellValueFactory(cellData -> cellData.getValue().employeePinProperty());
    }

    /**
     * Update {@link #employeeTable} in the Graphical User Interface
     */
    private void updateTable() {
        this.employeeTable.setItems(this.getEmployees());
        this.employeeTable.refresh();
    }

    /**
     * Gets all Employees in the database
     * 
     * @return {@link ObservableList} of {@link EmployeeRow}
     */
    private ObservableList<EmployeeRow> getEmployees() {
        ObservableList<EmployeeRow> employees = FXCollections.observableArrayList();
        try {
            ResultSet rs = database.executeQuery(
                    String.format("SELECT * FROM %s", DatabaseNames.EMPLOYEE_DATABASE));
            while (rs.next()) {
                Integer randomID = rs.getInt("id");
                String employeeName = rs.getString("name");
                String role = rs.getString("role");
                Integer employeePin = rs.getInt("pin");

                EmployeeRow employeeRow =
                        new EmployeeRow(randomID, employeeName, role, employeePin);
                employees.add(employeeRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    /**
     * Handles switching scenes
     * 
     * @param event {@link ActionEvent} of the {@link Button} being pressed
     * @throws IOException if loading a scene fails
     */
    @FXML
    void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }
}
