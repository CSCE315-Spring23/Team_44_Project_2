package Controller.Reports;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;

import Items.ZRow;
import java.sql.Date;
import java.io.IOException;
import java.lang.Double;
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
     * {@link TableColumn} to display the date the
     * z report is created
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

    @FXML
    private Button createZReport;

    @FXML
    private Button viewXReport;

    public XZReport(final SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    public void initialize() {
        this.setUpZTable();
        this.updateZTable();
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

    public void zReportButtonClicked(ActionEvent event) {
        ZTextBox.setText(createReport(true));
        addZReport();
        updateZTable();
    }

    public void xReportButtonClicked(ActionEvent event) {
        String xReportText = createReport(false);
        ZTextBox.setText(xReportText);
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

    private String createReport(boolean isZReport) {
        String totalSales = getTotalSalesSinceZReport();
        final String employee = DatabaseUtils.getEmployeeName(database, session.employeeId);
        final long millis = System.currentTimeMillis();
        Date dateCreated = new java.sql.Date(millis);
        String dateString = dateCreated.toString();
        final Long orderID = DatabaseUtils.getLastId(this.database, DatabaseNames.ORDER_ITEM_DATABASE);

        String reportType = "X";
        if(isZReport){
            reportType = "Z";
        }
        String textToDisplay = String.format("%s Report:\n" +
                                            "Total Sales Since Z Report: %s\n" + 
                                            "Employee: %s\n" + 
                                            "Date: %s\n" + 
                                            "Since orderID: %s\n", 
                                            reportType, totalSales, employee, dateString, orderID);
        return textToDisplay;
    }

    private String getTotalSalesSinceZReport() {
        Double totalSales = 0.0;
        try {
            final ResultSet zResultSet = this.getLastZReport();
            if (zResultSet.next() == false) {
                return "0.00";
            }

            final int previousOrderID = zResultSet.getInt("orderID");
            final String costQuery = String.format("SELECT total_cost FROM orderitem WHERE id > %s", previousOrderID);

            final ResultSet rs = database.executeQuery(costQuery);
            while (rs.next()) {
                totalSales = totalSales + Double.parseDouble(rs.getString("total_cost"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Total Sales Output:" + Double.toString(totalSales));
        return Double.toString(totalSales);
    }

    private ResultSet getLastZReport() {
        return database.executeQuery("SELECT * FROM zreport ORDER BY reportid DESC LIMIT 1");
    }

    private ObservableList<ZRow> getZRows() {
        final ObservableList<ZRow> zRows = FXCollections.observableArrayList();
        final String query = String.format("SELECT * FROM %s ORDER BY reportid",
                DatabaseNames.ZREPORT_DATABASE);
        final ResultSet rs = database.executeQuery(query);
        try {
            while (rs.next()) {
                final Long reportID = rs.getLong("reportid");
                final String totalSales = rs.getString("totalsales");
                final String employee = rs.getString("employee");
                final Long orderID = rs.getLong("orderid");
                final Date dateCreated = rs.getDate("datecreated");
                zRows.add(
                        new ZRow(reportID, totalSales, employee, orderID, dateCreated));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return zRows;
    }

    private void addZReport() {
        try {
            final long millis = System.currentTimeMillis();
            Date dateCreated = new java.sql.Date(millis);
            final Long orderID = DatabaseUtils.getLastId(this.database, DatabaseNames.ORDER_ITEM_DATABASE);
            final Long reportID = this.getLastZReport().getLong("reportid") + 1;
            final String totalSales = this.getTotalSalesSinceZReport();
            final String employee = DatabaseUtils.getEmployeeName(database, session.employeeId);

            final String query = String.format(
                    /* TODO: The %s may need '\ %s '\ for employee name, and ? for Date */
                    "INSERT INTO %s (reportid, totalsales, employee, orderid, datecreated) VALUES (%d, %s, %s, %d, ?)",
                    DatabaseNames.ZREPORT_DATABASE, reportID, totalSales, employee, orderID, dateCreated);
            System.out.println("SQL Query: " + query);
            this.database.executeUpdate(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getZReportParams() {
        try {
            final long millis = System.currentTimeMillis();
            Date dateCreated = new java.sql.Date(millis);
            String.format("The date: %tY-%tm-%td", dateCreated);

            final String orderID = Long
                    .toString(DatabaseUtils.getLastId(this.database, DatabaseNames.ORDER_ITEM_DATABASE));
            final String reportID = Long.toString(this.getLastZReport().getLong("reportid") + 1);
            final String totalSales = this.getTotalSalesSinceZReport();
            final String employee = DatabaseUtils.getEmployeeName(database, session.employeeId);
            final String zString = String.format("%s %s %s %s %s", reportID, totalSales, employee, orderID,
                    dateCreated);
            System.out.println(zString);
            return zString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Did not get Z-Report Params Successfully");
        return "-1";
    }

}
