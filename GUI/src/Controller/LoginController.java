package Controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import Items.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.DatabaseNames;
import Utils.DatabaseUtils;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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
     * Max number of digits that PIN can hold
     */
    private static final int MAX_PIN_LENGTH = 4;

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
     * {@link Button} that triggers {@link #loginButtonClicked(ActionEvent)}
     */
    @FXML
    private Button login;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num0;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num1;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num2;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num3;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num4;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num5;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num6;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num7;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num8;

    /**
     * {@link Button} that triggers {@link #setPin(ActionEvent)}
     */
    @FXML
    private Button num9;

    /**
     * {@link Button} that tiggers {@link #onBackspace(ActionEvent)}
     */
    @FXML
    private Button backspace;

    /**
     * {@link ToggleButton} that triggers {@link #onShowPin(ActionEvent)}
     */
    @FXML
    private ToggleButton showPin;

    /**
     * {@link TextField} to input the PIN
     */
    @FXML
    private TextField pinBox;

    /**
     * Represents current typed PIN
     */
    private String pinNumber = "";

    /**
     * Boolean determining if the pin is hidden
     */
    private boolean isShowingPin = false;

    /**
     * Constructor
     */
    public LoginController() {}

    /**
     * Constructor
     * 
     * @param session Session's Information
     */
    public LoginController(final SessionData session) {
        this.session = session;
        this.database = this.session.database;
    }

    /**
     * Inialize the connection to the database.
     */
    public void initialize() {
        this.database = this.databaseInitializer();
        this.pinBox.setText("");
    }

    /**
     * Update the {@link #pinBox}
     */
    private void updatePin() {
        final int pinLength = this.pinNumber.length();
        this.pinBox.setText(this.isShowingPin ? this.pinNumber : "●".repeat(pinLength));
        this.pinBox.positionCaret(pinLength);
    }

    /**
     * Sets the {@link #pinNumber} when a pin input {@link Button} is pressed
     * 
     * @param event {@link ActionEvent} of the pin input {@link Button}
     */
    public void setPin(final ActionEvent event) {
        final Button button = (Button) event.getSource();
        if (this.pinNumber.length() < MAX_PIN_LENGTH) {
            this.pinNumber += button.getText();
            this.updatePin();
        }
    }

    /**
     * Handle typing directly into {@link #pinBox}
     * 
     * @param event {@link KeyEvent} of key pressed
     */
    public void onPinBoxTyped(final KeyEvent event) {
        final int strLength = pinBox.getText().length();
        if (strLength < 0)
            return;
        if (strLength > MAX_PIN_LENGTH) {
            this.pinBox.setText(this.pinBox.getText().substring(0, MAX_PIN_LENGTH));
            return;
        }

        if (this.isShowingPin) {
            this.pinNumber = this.pinBox.getText();
        } else {
            final char keyTyped = (char) event.getCode().getCode();
            if (this.pinNumber.length() < strLength)
                this.pinNumber += keyTyped;
            else
                this.pinNumber = this.pinNumber.substring(0, strLength);
        }

        this.updatePin();
    }

    /**
     * Handles pressing {@link #backspace}
     * 
     * @param ae {@link ActionEvent} of {@link #backspace}
     */
    public void onBackspace(final ActionEvent ae) {
        if (!this.pinNumber.isEmpty()) {
            this.pinNumber = this.pinNumber.substring(0, this.pinNumber.length() - 1);
            this.updatePin();
        }
    }

    /**
     * Toggles between showing and hidding the pin
     * 
     * @param ae {@link ActionEvent} of {@link #showPin}
     */
    public void onShowPin(final ActionEvent ae) {
        this.isShowingPin = ((ToggleButton) ae.getSource()).isSelected();
        this.updatePin();
    }

    /**
     * Initalize the connection to the database
     * 
     * @return {@link DatabaseConnect}
     */
    public DatabaseConnect databaseInitializer() {
        final String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        final String username = DatabaseLoginInfo.username;
        final String password = DatabaseLoginInfo.password;

        final DatabaseConnect database =
                new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();

        return database;
    }

    /**
     * Retreives {@link SessionData} of the employee who logged in
     * 
     * @return {@link SessionData}
     */
    public SessionData loginInitializer() {
        final long id = this.getEmployeeId();

        // sessionDataObject will be passed starting from LoginPage
        final Order order = new Order(id,
                DatabaseUtils.getLastId(this.database, DatabaseNames.ORDER_ITEM_DATABASE) + 1l);
        return new SessionData(this.databaseInitializer(), id, order);
    }

    /**
     * Log into the database and switch scenes.
     * 
     * @param event {@link ActionEvent} of {@link #login}
     * @throws IOException if loading the new window failed
     */
    @FXML
    public void loginButtonClicked(ActionEvent event) throws IOException {
        final String sqlQuery = String.format("SELECT * FROM %s WHERE pin=\'%s\'",
                DatabaseNames.EMPLOYEE_DATABASE, this.pinNumber);
        final ResultSet loginQuery = database.executeQuery(sqlQuery);
        try {
            if (!loginQuery.next()) {
                System.err.println("Invalid PIN");
                return;
            }

            this.session = this.loginInitializer();
            this.sceneSwitch = new SceneSwitch(session);
            this.sceneSwitch.LoginTransition(event, session);
            System.out.println("Login authenticated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Determine the ID of the employee that logged in.
     * 
     * @return ID of the employee
     */
    public long getEmployeeId() {
        final String query = String.format("SELECT id FROM %s WHERE pin = \'%s\'",
                DatabaseNames.EMPLOYEE_DATABASE, this.pinNumber);
        final ResultSet rs = this.database.executeQuery(query);
        final long ret;
        try {
            ret = rs.next() ? rs.getLong("id") : -1l;
            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
            return -1l;
        }
        return ret;
    }
}
