package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import Items.EmployeeRow;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import Utils.DatabaseConnect;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

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

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private TableView<EmployeeRow> employeeTable;

    @FXML // fx:id="customerName"
    private TableColumn<?, ?> customerName; // Value injected by FXMLLoader

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

    @FXML // fx:id="orderDate"
    private TableColumn<?, ?> orderDate; // Value injected by FXMLLoader

    @FXML // fx:id="orderHistoryTable"
    private TableView<?> orderHistoryTable; // Value injected by FXMLLoader

    @FXML // fx:id="orderHistoryTextBox"
    private TextArea orderHistoryTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="randomID"
    private TableColumn<EmployeeRow, Integer> randomID; // Value injected by FXMLLoader

    @FXML // fx:id="employeeName"
    private TableColumn<EmployeeRow, String> employeeName; // Value injected by FXMLLoader

    @FXML // fx:id="role"
    private TableColumn<EmployeeRow, String> role; // Value injected by FXMLLoader

    @FXML // fx:id="employeePin"
    private TableColumn<EmployeeRow, Integer> employeePin; // Value injected by FXMLLoader

    public EmployeeController(SessionData session) {
        this.session = session;
        database = session.database;
    }

    public void initialize() {
        setUpTable();
        employeeTable.refresh();

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

    /*
     * private HashMap<String, Integer> getEmployees(ArrayList<String> employeeIds)
     * {
     * HashMap<String, Integer> employees = new HashMap<>();
     * for (String pinNumber : employeeIds) {
     * try {
     * ResultSet rs = database.executeQuery("SELECT * FROM employee WHERE pin = " +
     * pinNumber);
     * while (rs.next()) {
     * if (employees.containsKey(rs.getString("name"))) {
     * menuItems.put(rs.getString("name"),
     * menuItems.get(rs.getString("name")) + 1);
     * } else {
     * menuItems.put(rs.getString("name"), 1);
     * }
     * }
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     * return menuItems;
     * }
     */

    private void setUpTable() {
        // define TableView columns
        randomID.setCellValueFactory(cellData -> cellData.getValue().randomIDProperty());

        employeeName.setCellValueFactory(cellData -> cellData.getValue().employeeNameProperty());

        role.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        employeePin.setCellValueFactory(cellData -> cellData.getValue().employeePinProperty());

        // generate list of employees
        ObservableList<EmployeeRow> employees = getEmployees();

        // add data to table
        employeeTable.setItems(employees);

    }

    private ObservableList<EmployeeRow> getEmployees() {
        ObservableList<EmployeeRow> employees = FXCollections.observableArrayList();
        try {
            ResultSet rs = database.executeQuery("SELECT * FROM employee");
            while (rs.next()) {
                Integer randomID = rs.getInt("id");
                String employeeName = rs.getString("name");
                String role = rs.getString("role");
                Integer employeePin = rs.getInt("pin");

                EmployeeRow employeeRow = new EmployeeRow(randomID, employeeName, role,
                        employeePin);
                employees.add(employeeRow);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return employees;
    }

    @FXML
    void navButtonClicked(ActionEvent event) throws IOException {
        sceneSwitch = new SceneSwitch(session);
        sceneSwitch.switchScene(event);
    }
}