package Controller.Reports;

import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import Items.ZRow;
import Utils.DatabaseConnect;
import Utils.DatabaseNames;
import Utils.DatabaseUtils;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * Controller for the XZ Report window
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
public class XZReport {
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
     * {@link TableView} of {@link ZRow} to display Z Report Table
     *
     */
    @FXML
    private TableView<ZRow> ZReportTable;

    /**
     * {@link TableColumn} to display ID of the report
     *
     */
    @FXML
    private TableColumn<ZRow, Long> reportID;

    /**
     * {@link TableColumn} to display total sales
     *
     */
    @FXML
    private TableColumn<ZRow, String> totalSales;

    /**
     * {@link TableColumn} to display employee name
     *
     */
    @FXML
    private TableColumn<ZRow, String> employee;

    /**
     * {@link TableColumn} to display the date the z report is created
     *
     */
    @FXML
    private TableColumn<ZRow, Date> dateCreated;

    /**
     * {@link TableColumn} to display order ID
     *
     */
    @FXML
    private TableColumn<ZRow, Long> orderID;

    /**
     * {@link TextArea} to display order details
     *
     */
    @FXML
    private TextArea ZTextBox;

    /**
     * {@link Button} to create Z Report
     */
    @FXML
    private Button createZReport;

    /**
     * {@link Button} to view the X report
     */
    @FXML
    private Button viewXReport;

    /**
     * Constructor
     * 
     * @param session {@link SessionData} passed in from {@link SceneSwitch}
     */
    public XZReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    /**
     * Initialize the graphical user interface
     */
    public void initialize() {
        this.setUpZTable();
        this.updateZTable();
        this.addRowOnClick();
        this.ZReportTable.refresh();

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
    public void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    /**
     * Create a Z Report
     * 
     * @param event {@link ActionEvent} of the {@link #createZReport}
     */
    public void zReportButtonClicked(final ActionEvent event) {
        this.ZTextBox.setText(this.createReport(true));
        this.addZReport();
        this.updateZTable();
    }

    /**
     * View the X Report
     * 
     * @param event {@link ActionEvent} of the {@link #viewXReport}
     */
    public void xReportButtonClicked(final ActionEvent event) {
        final String xReportText = this.createReport(false);
        this.ZTextBox.setText(xReportText);
    }

    /**
     * Update the {@link #ZReportTable}
     */
    private void updateZTable() {
        this.ZReportTable.setItems(this.getZRows());
        this.ZReportTable.refresh();
    }

    /**
     * Sets up the table to display order history
     */
    private void setUpZTable() {
        // define TableView columns
        this.orderID.setCellValueFactory(cellData -> cellData.getValue().getOrderID());
        this.reportID.setCellValueFactory(cellData -> cellData.getValue().getReportID());
        this.totalSales.setCellValueFactory(cellData -> cellData.getValue().getTotalSales());
        this.dateCreated.setCellValueFactory(cellData -> cellData.getValue().getDateCreated());
        this.employee.setCellValueFactory(cellData -> cellData.getValue().getEmployee());
    }

    /**
     * Create a Z or X Report
     * 
     * @param isZReport true if creating a Z report and false for X Report
     * @return the Report as a {@link String}
     */
    private String createReport(final boolean isZReport) {
        final String totalSales = this.getTotalSalesSinceZReport();
        final String employee =
                DatabaseUtils.getEmployeeName(this.database, this.session.employeeId);
        final long millis = System.currentTimeMillis();
        final Date dateCreated = new Date(millis);
        final String dateString = dateCreated.toLocalDate().format(DatabaseUtils.DATE_FORMAT);
        final Long orderID =
                DatabaseUtils.getLastId(this.database, DatabaseNames.ORDER_ITEM_DATABASE);

        final String reportType = isZReport ? "Z" : "X";
        final String textToDisplay = String.format(
                "%s Report:\n" + "Total Sales Since Z Report: %s\n" + "Employee: %s\n"
                        + "Date: %s\n" + "Since orderID: %s\n",
                reportType, totalSales, employee, dateString, orderID);
        return textToDisplay;
    }

    /**
     * Determines the total sales since the last z report was created
     * 
     * @return total sales as a {@link String}
     */
    private String getTotalSalesSinceZReport() {
        double totalSales = 0.0d;
        try {
            final ResultSet zResultSet = this.getLastZReport();
            if (!zResultSet.next()) {
                return "NaN";
            }

            final int previousOrderID = zResultSet.getInt("orderID");
            final String costQuery = String.format("SELECT total_cost FROM %s WHERE id > %s",
                    DatabaseNames.ORDER_ITEM_DATABASE, previousOrderID);

            final ResultSet rs = database.executeQuery(costQuery);
            while (rs.next()) {
                totalSales += Double.parseDouble(rs.getString("total_cost"));
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Total Sales Output: " + totalSales);
        return String.format("%.2f", totalSales);
    }

    /**
     * Returns the last Z Report as a {@link ResultSet}
     * 
     * @return the last Z Report
     */
    private ResultSet getLastZReport() {
        return this.database.executeQuery("SELECT * FROM zreport ORDER BY reportid DESC LIMIT 1");
    }

    /**
     * Creates a list to display in the {@link #ZReportTable}
     * 
     * @return {@link ObservableList} of {@link ZRow}
     */
    private ObservableList<ZRow> getZRows() {
        final ObservableList<ZRow> zRows = FXCollections.observableArrayList();
        final String query =
                String.format("SELECT * FROM %s ORDER BY reportid", DatabaseNames.ZREPORT_DATABASE);
        final ResultSet rs = this.database.executeQuery(query);
        try {
            while (rs.next()) {
                final Long reportID = rs.getLong("reportid");
                final String totalSales = rs.getString("totalsales");
                final String employee = rs.getString("employee");
                final Long orderID = rs.getLong("orderid");
                final Date dateCreated = rs.getDate("datecreated");
                zRows.add(new ZRow(reportID, totalSales, employee, orderID, dateCreated));
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return zRows;
    }

    /**
     * Adds a Z Report to the database
     */
    private void addZReport() {
        try {
            final long millis = System.currentTimeMillis();
            final Date dateCreated = new Date(millis);
            String dateString = dateCreated.toString();
            final Long orderID =
                    DatabaseUtils.getLastId(this.database, DatabaseNames.ORDER_ITEM_DATABASE);
            final String totalSales = this.getTotalSalesSinceZReport();
            final String employee = DatabaseUtils.getEmployeeName(database, session.employeeId);

            /* TODO: The %s may need '\ %s '\ for employee name, and ? for Date */
            final String query = String.format(
                    "INSERT INTO %s (totalsales, employee, orderid, datecreated) VALUES (%s,\'%s\', %d, \'%s\')",
                    DatabaseNames.ZREPORT_DATABASE, totalSales, employee, orderID, dateString);
            System.out.println("SQL Query: " + query);
            this.database.executeUpdate(query);
        } catch (Exception e) {
            System.err.println("BAD ADD ZReport");
            e.printStackTrace();
        }
    }

    /**
     * Adds interactibilitiy to each {@link TableRow} in the {@link #ZReportTable}
     */
    private void addRowOnClick() {
        this.ZReportTable.setRowFactory(tv -> {
            final TableRow<ZRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    final ZRow rowData = row.getItem();
                    final long id = rowData.getReportID().getValue();

                    String reportDetails = "No Report Selected";
                    final String query =
                            String.format("SELECT * FROM zreport WHERE reportid = %o", id);
                    System.out.println("QUERY: " + query);
                    final ResultSet rs = database.executeQuery(query);
                    try {
                        if (!rs.next()) {
                            this.ZTextBox.setText(reportDetails);
                            return;
                        }

                        final String totalSales = rs.getString("totalSales");
                        final String employee = rs.getString("employee");
                        final String dateString = rs.getString("datecreated");
                        final String orderID = rs.getString("orderid");

                        reportDetails = String.format(
                                "Z Report:\n" + "Total Sales Since Z Report: %s\n"
                                        + "Employee: %s\n" + "Date: %s\n" + "Since orderID: %s\n",
                                totalSales, employee, dateString, orderID);

                    } catch (final SQLException e) {
                        e.printStackTrace();
                    }

                    this.ZTextBox.setText(reportDetails);
                }
            });
            return row;
        });
    }
}
