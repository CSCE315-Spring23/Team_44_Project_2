package Controller.Reports;


import java.io.IOException;
import java.sql.ResultSet;
import Items.SalesTogetherRow;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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

    @FXML
    TextField startDateText;

    @FXML
    TextField endDateText;

    @FXML
    Button goButton;

    @FXML
    TableView<SalesTogetherRow> salesTogetherTable;

    @FXML
    TableColumn<SalesTogetherRow, String> menuItem1Col;

    @FXML
    TableColumn<SalesTogetherRow, String> menuItem2Col;

    @FXML
    TableColumn<SalesTogetherRow, Long> numberSoldCol;


    public SalesTogetherReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
    }

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

    public void onGoClick(final ActionEvent event) throws IOException {
        final String startDate = startDateText.getText();
        final String endDate = endDateText.getText();

        System.out.println(startDate + " " + endDate);
        // format check for dates
        if (!startDate.matches("\\d{4}-\\d{2}-\\d{2}")
                || !endDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid date format");
            return;
        }

        this.generateReport(startDate, endDate);
    }

    private void setUpSalesTogetherTable() {
        this.menuItem1Col.setCellValueFactory(cellData -> cellData.getValue().getMenuItem1());
        this.menuItem2Col.setCellValueFactory(cellData -> cellData.getValue().getMenuItem2());
        this.numberSoldCol.setCellValueFactory(cellData -> cellData.getValue().getNumSold());
    }

    private void generateReport(final String startDate, final String endDate) {
        this.salesTogetherTable.getItems().clear();

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

        final ResultSet rs = database.executeQuery(query);
        try {
            while (rs.next()) {
                final SalesTogetherRow row = new SalesTogetherRow(rs.getString("menuitem1"),
                        rs.getString("menuitem2"), rs.getLong("numSold"));
                salesTogetherTable.getItems().add(row);
            }

            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
