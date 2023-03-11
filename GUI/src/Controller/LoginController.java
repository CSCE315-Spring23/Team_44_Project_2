package Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import Items.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
        this.database = this.databaseInitializer();
    }

    private void updatePin() {
        this.pinBox
                .setText(this.isShowingPin ? this.pinNumber : "●".repeat(this.pinNumber.length()));
        this.pinBox.positionCaret(this.pinNumber.length());
    }

    public void setPin(final ActionEvent event) {
        final Button button = (Button) event.getSource();
        if (this.pinNumber.length() < MAX_PIN_LENGTH) {
            this.pinNumber += button.getText();
            this.updatePin();
        }
    }

    public void onPinBoxTyped(final KeyEvent event) {
        final char keyTyped = (char) event.getCode().getCode();

        final int strLength = pinBox.getText().length();
        if (0 <= strLength && strLength <= MAX_PIN_LENGTH) {
            if (this.isShowingPin) {
                this.pinNumber = this.pinBox.getText();
            } else {
                if (this.pinNumber.length() < strLength) {
                    this.pinNumber += keyTyped;
                } else {
                    this.pinNumber = this.pinNumber.substring(0, strLength);
                }
            }
        }
        this.updatePin();
    }

    public void onBackspace(final ActionEvent ae) {
        if (this.pinNumber.length() > 0) {
            this.pinNumber = this.pinNumber.substring(0, this.pinNumber.length() - 1);
            this.updatePin();
        }
    }

    public void onShowPin(final ActionEvent ae) {
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
            final String sqlQuery = String.format("SELECT * FROM %s WHERE pin=\'%s\'",
                    DatabaseNames.EMPLOYEE_DATABASE, this.pinNumber);
            // System.out.println(sqlQuery);

            // System.out.println(database);
            final ResultSet loginQuery = database.executeQuery(sqlQuery);
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
            ResultSet rs =
                    database.executeQuery(String.format("SELECT id FROM %s WHERE pin = \'%s\'",
                            DatabaseNames.EMPLOYEE_DATABASE, this.pinNumber));
            if (rs.next()) {
                ret = rs.getLong("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}
