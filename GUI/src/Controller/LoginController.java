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
import javafx.scene.control.ToggleButton;

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

    @FXML // fx:id="backspace"
    private Button backspace; // Value injected by FXMLLoader

    @FXML // fx:id="showPin"
    private ToggleButton showPin; // Value injected by FXMLLoader

    @FXML // fx:id="pinBox"
    private TextField pinBox; // Value injected by FXMLLoader

    // represents current typed PIN
    private String pinNumber = "";

    // hiding the PIN
    private Boolean isShowingPin = false;

    // Max PIN Length
    private final int MAX_PIN_LENGTH = 4;

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
     * Connection to the database
     *
     * @see DatabaseConnect
     */
    public Order order;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        database = databaseInitializer();
    }

    private void updatePin() {
        pinBox.setText(isShowingPin ? pinNumber : "●".repeat(pinNumber.length()));
        pinBox.positionCaret(pinNumber.length());
    }

    public void setPin(ActionEvent ae) {
        if (this.pinNumber.length() < MAX_PIN_LENGTH) {
            this.pinNumber = this.pinNumber + ((Button) ae.getSource()).getText();
            this.updatePin();
        }
    }

    public void onPinBoxTyped() {
        int strLength = pinBox.getText().length();
        if (0 <= strLength && strLength <= MAX_PIN_LENGTH) {
            if (isShowingPin) {
                pinNumber = pinBox.getText();
            } else {
                if (pinNumber.length() < strLength) {
                    pinNumber = pinNumber + pinBox.getText().charAt(strLength - 1);
                } else {
                    pinNumber = pinNumber.substring(0, strLength);
                }
            }
        }
        this.updatePin();
    }

    public void onBackspace(ActionEvent ae) {
        if (pinNumber.length() > 0) {
            pinNumber = pinNumber.substring(0, pinNumber.length() - 1);
            this.updatePin();
        }
    }

    public void onShowPin(ActionEvent ae) {
        this.isShowingPin = ((ToggleButton) ae.getSource()).isSelected();
        this.updatePin();
    }

    public DatabaseConnect databaseInitializer() {
        final String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        final String username = DatabaseLoginInfo.username;
        final String password = DatabaseLoginInfo.password;

        final DatabaseConnect database =
                new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();

        return database;
    }

    public SessionData loginInitializer() {
        final long id = this.getEmployeeId();

        // sessionDataObject will be passed starting from LoginPage
        SessionData newSession = new SessionData(this.databaseInitializer(), id, new Order(id));

        return newSession;
    }

    @FXML
    public void loginButtonClicked(ActionEvent event) throws IOException {
        try {
            String sqlQuery = String.format("SELECT * FROM %s WHERE pin= '" + pinNumber + "'",
                    DatabaseNames.EMPLOYEE_DATABASE);
            // System.out.println(sqlQuery);

            // System.out.println(database);
            ResultSet loginQuery = database.executeQuery(sqlQuery);
            if (!loginQuery.next()) {
                System.out.println("Invalid PIN");
            } else {
                this.session = this.loginInitializer();
                this.sceneSwitch = new SceneSwitch(session);
                this.sceneSwitch.LoginTransition(event, session);
                System.out.println("Login authenticated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public long getEmployeeId() {
        long ret = -1l;
        try {
            ResultSet rs = database
                    .executeQuery(String.format("SELECT id FROM %s WHERE pin = '" + pinNumber + "'",
                            DatabaseNames.EMPLOYEE_DATABASE));
            if (rs.next()) {
                ret = rs.getLong("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
