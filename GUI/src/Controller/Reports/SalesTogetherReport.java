package Controller.Reports;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import Items.SalesTogetherRow;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.DatabaseUtils;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * Controller for the Sales Together window
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
public class SalesTogetherReport {
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
     * {@link TextField} Text field for User specified start date
     */

    @FXML
    private DatePicker startDate;

    /**
     * {@link TextField} Text field for User specified end date
     */
    @FXML
    private DatePicker endDate;

    /**
     * {@link Button} Button to generate what sales together report between {@link #startDate} and
     * {@link #endDate}
     */
    @FXML
    Button goButton;

    /**
     * {@link TableView} Table to display what sales together report
     */
    @FXML
    private TableView<SalesTogetherRow> salesTogetherTable;

    /**
     * {@link TableColumn} Column to display menu item 1
     */
    @FXML
    private TableColumn<SalesTogetherRow, String> menuItem1Col;

    /**
     * {@link TableColumn} Column to display menu item 2
     */
    @FXML
    private TableColumn<SalesTogetherRow, String> menuItem2Col;

    /**
     * {@link TableColumn} Column to display number sold
     */
    @FXML
    private TableColumn<SalesTogetherRow, Long> numberSoldCol;

    /**
     * Constructor for SalesTogetherReport
     *
     * @param session {@link SessionData} of current session
     */
    public SalesTogetherReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    /**
     * Sets up GUI for SalesTogetherReport
     */
    public void initialize() {

        this.setUpSalesTogetherTable();

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
    }

    /**
     * Navigates to the scene specified by the button clicked
     *
     * @param event {@link ActionEvent} of {@link Button} in the navigation bar
     * @throws IOException if loading a window fails
     */
    public void navButtonClicked(final ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Generates the sales together report after button click
     *
     * @param event {@link ActionEvent} of {@link #goButton}
     */
    public void onGoClick(final ActionEvent event) {
        final LocalDate sDate = this.startDate.getValue();
        if (sDate == null) {
            System.out.println("No start date");
            return;
        }

        final LocalDate eDate = this.endDate.getValue();
        if (eDate == null) {
            System.out.println("No end date");
            return;
        }

        final String startDate = sDate.format(DatabaseUtils.DATE_FORMAT);
        final String endDate = eDate.format(DatabaseUtils.DATE_FORMAT);

        System.out.println(startDate + " " + endDate);
        this.generateReport(startDate, endDate);
    }

    /**
     * Sets up the {@link #salesTogetherTable}
     */
    private void setUpSalesTogetherTable() {
        this.menuItem1Col.setCellValueFactory(cellData -> cellData.getValue().getMenuItem1());
        this.menuItem2Col.setCellValueFactory(cellData -> cellData.getValue().getMenuItem2());
        this.numberSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    /**
     * Generates what sales together report between {@code startDate} and {@code endDate}
     *
     * @param startDate {@link String} start date
     * @param endDate {@link String} end date
     */
    private void generateReport(final String startDate, final String endDate) {
        this.salesTogetherTable.getItems().clear();

        // query to get what sales together report
        // %1$s = sold item database, %2$s = menu item database, %3$s = order database
        // %4$s = start date, %5$s = end date
        final String query = String.format(
                "SELECT mi1.name AS menuitem1, mi2.name as menuitem2, COUNT(*) AS numSold"
                        + " FROM %1$s si1"
                        + " JOIN %1$s si2 ON si1.orderid = si2.orderid AND si1.menuid < si2.menuid"
                        + " JOIN %2$s mi1 ON si1.menuid = mi1.id"
                        + " JOIN %2$s mi2 ON si2.menuid = mi2.id"
                        + " JOIN %3$s oi ON si1.orderid = oi.id"
                        + " WHERE Date(oi.date) >= '%4$s' AND Date(oi.date) <= '%5$s'"
                        + " AND mi1.id != mi2.id" + " GROUP BY mi1.name, mi2.name"
                        + " ORDER BY numSold DESC",
                DatabaseNames.SOLD_ITEM_DATABASE, DatabaseNames.MENU_ITEM_DATABASE,
                DatabaseNames.ORDER_ITEM_DATABASE, startDate, endDate);

        try {
            final ResultSet rs = database.executeQuery(query);

            // add each row to the table
            while (rs.next()) {
                final SalesTogetherRow row = new SalesTogetherRow(rs.getString("menuitem1"),
                        rs.getString("menuitem2"), rs.getLong("numSold"));
                salesTogetherTable.getItems().add(row);
            }

            rs.close();
        } catch (final SQLException e) {
            e.printStackTrace();
            return;
        }
    }
}
