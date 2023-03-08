package Controller;

import Items.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.SceneSwitch;
import Utils.SessionData;
import Utils.DatabaseNames;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Controller for the Login Screen
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

public class LoginController {
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

    @FXML // fx:id="login"
    private Button login; // Value injected by FXMLLoader

    @FXML // fx:id="num0"
    private Button num0; // Value injected by FXMLLoader

    @FXML // fx:id="num1"
    private Button num1; // Value injected by FXMLLoader

    @FXML // fx:id="num2"
    private Button num2; // Value injected by FXMLLoader

    @FXML // fx:id="num3"
    private Button num3; // Value injected by FXMLLoader

    @FXML // fx:id="num4"
    private Button num4; // Value injected by FXMLLoader

    @FXML // fx:id="num5"
    private Button num5; // Value injected by FXMLLoader

    @FXML // fx:id="num6"
    private Button num6; // Value injected by FXMLLoader

    @FXML // fx:id="num7"
    private Button num7; // Value injected by FXMLLoader

    @FXML // fx:id="num8"
    private Button num8; // Value injected by FXMLLoader

    @FXML // fx:id="num9"
    private Button num9; // Value injected by FXMLLoader

    @FXML // fx:id="pinBox"
    private TextField pinBox; // Value injected by FXMLLoader

    int pinNumber;

    public LoginController() {}

    /**
     * Constructor
     * 
     * @param session Session's Information
     */
    public LoginController(SessionData session) {
        this.session = session;
    }

    /**
     * * Connection to the database
     *
     * @see DatabaseConnect
     */

    public Order order;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        database = databaseInitializer();
    }

    @FXML
    public void setPin(ActionEvent ae) {
        String pinNum = ((Button) ae.getSource()).getText();
        pinBox.setText(pinBox.getText() + pinNum);
    }

    public DatabaseConnect databaseInitializer() {
        DatabaseConnect database;
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();

        return database;
    }

    public SessionData loginInitializer() {

        int id = getEmployeeId();

        // sessionDataObject will be passed starting from LoginPage
        SessionData newSession =
                new SessionData(databaseInitializer(), id, new Order(id));

        return newSession;
    }

    @FXML
    public void loginButtonClicked(ActionEvent event) throws IOException {
        this.pinNumber = Integer.parseInt(pinBox.getText());
        System.out.println(this.pinNumber);
        try {
            String sqlQuery =
                    String.format("SELECT * FROM %s WHERE pin= '" + Integer.toString(pinNumber) + "'", DatabaseNames.EMPLOYEE_DATABASE);
            // System.out.println(sqlQuery);

            // System.out.println(database);
            ResultSet loginQuery = database.executeQuery(sqlQuery);
            if (!loginQuery.next()) {
                System.out.println("Invalid PIN");
            } else {
                this.session = loginInitializer();
                this.sceneSwitch = new SceneSwitch(session);
                this.sceneSwitch.LoginTransition(event, session);
                System.out.println("Login authenticated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public int getEmployeeId(){
        int ret = -1;
        try {
            ResultSet rs = database.executeQuery(String.format("SELECT id FROM %s WHERE pin = '" + Integer.toString(pinNumber) + "'", DatabaseNames.EMPLOYEE_DATABASE));
            if(rs.next()){
                ret = rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
