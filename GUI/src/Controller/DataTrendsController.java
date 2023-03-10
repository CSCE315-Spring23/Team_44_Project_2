package Controller;

import java.io.IOException;
import java.util.ArrayList;
import Utils.DatabaseConnect;
import Utils.SceneSwitch;
import Utils.SessionData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * This class handles the data trends scene/tab of the GUI
 * 
 * @since 2023-03-06
 * @version 2023-03-06
 * 
 * @author Dai, Kevin
 * @author Davis, Sloan
 * @author Kuppa Jayaram, Shreeman
 * @author Lai, Huy
 * @author Mao, Steven
 */
public class DataTrendsController {
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

    // private LineChart ordersPerDay;
    // private LineChart pricePerOrder;

    @FXML
    private VBox chartStack;

    public DataTrendsController(SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    public void initialize() {
        // ArrayList<String> rawDates = getRecentDates();
        // ArrayList<Integer> dates = decodeDates(rawDates);

        // // orders per day
        // ArrayList<Double> orderAverages = getOrderNumAveragesForDates(dates);
        // final NumberAxis xAxisOrders = new NumberAxis();
        // final NumberAxis yAxisOrders = new NumberAxis();

        // ordersPerDay = new LineChart<Number, Number>(xAxisOrders, yAxisOrders);
        // ordersPerDay.setTitle("Orders Per Day");

        // XYChart.Series ordersSeries = new XYChart.Series();

        // for (int i = 0; i < dates.size(); i++) {
        //     Integer date = dates.get(i);
        //     Double curDayOrders = orderAverages.get(i);
        //     ordersSeries.getData().add(new XYChart.Data(date, curDayOrders));
        // }

        // ordersPerDay.getData().add(ordersSeries);
        // chartStack.getChildren().add(ordersPerDay);

        // // price per order
        // ArrayList<Double> orderPricDoubles = getOrderAveragesForDates(dates);
        // final NumberAxis xAxisPrices = new NumberAxis();
        // final NumberAxis yAxisPrices = new NumberAxis();

        // ordersPerDay = new LineChart<Number, Number>(xAxisPrices, yAxisPrices);
        // ordersPerDay.setTitle("Price Per Order Per Day");

    }

    /**
     * Handle switching scenes through the navigation bar
     *
     * @param event {@link ActionEvent} of the {@link Button} pressed
     * @throws IOException if loading the new GUI failed
     */
    public void navButtonClicked(ActionEvent event) throws IOException {
        this.sceneSwitch = new SceneSwitch(session);
        this.sceneSwitch.switchScene(event);
    }

    // private ArrayList<Double> getOrderNumAveragesForDates(ArrayList<Integer> dates) {
    //     return null;
    // }

    // private ArrayList<Double> getOrderAveragesForDates(ArrayList<Integer> dates) {
    //     return null;
    // }

    // private ArrayList<String> getRecentDates() {
    //     ArrayList<String> dates = new ArrayList<>();

    //     return dates;
    // }

    // private ArrayList<Integer> decodeDates(ArrayList<String> rawDates) {
    //     ArrayList<Integer> dates = new ArrayList<>();

    //     return dates;
    // }

}