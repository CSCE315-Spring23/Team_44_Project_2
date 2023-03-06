package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.chart.Axis;
//import javafx.scene.control.*;

import java.util.ArrayList;

import Utils.SessionData;
import Utils.DatabaseConnect;

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

    private DatabaseConnect database;
    private SessionData session;

    private LineChart ordersPerDay;
    private LineChart pricePerOrder;

    @FXML
    private VBox chartStack;

    public DataTrendsController(SessionData session) {
        this.session = session;
        this.database = session.getDatabase();
    }

    public void initialize() {
        ArrayList<String> rawDates = getRecentDates();
        ArrayList<Integer> dates = decodeDates(rawDates);


        //orders per day
        ArrayList<Double> orderAverages = getOrderNumAveragesForDates(dates);
        final NumberAxis xAxisOrders = new NumberAxis();
        final NumberAxis yAxisOrders = new NumberAxis();

        ordersPerDay = new LineChart<Number, Number>(xAxisOrders, yAxisOrders);
        ordersPerDay.setTitle("Orders Per Day");

        XYChart.Series ordersSeries = new XYChart.Series();

        for(int i = 0; i < dates.size(); i++){
            Integer date = dates.get(i);
            Double curDayOrders = orderAverages.get(i);
            ordersSeries.getData().add(new XYChart.Data(date, curDayOrders));
        }

        ordersPerDay.getData().add(ordersSeries);
        chartStack.getChildren().add(ordersPerDay);

        //price per order
        ArrayList<Double> orderPricDoubles = getOrderAveragesForDates(dates);
        final NumberAxis xAxisPrices = new NumberAxis();
        final NumberAxis yAxisPrices = new NumberAxis();

        ordersPerDay = new LineChart<Number, Number>(xAxisPrices, yAxisPrices);
        ordersPerDay.setTitle("Price Per Order Per Day");




    }

    private ArrayList<Double> getOrderNumAveragesForDates(ArrayList<Integer> dates) {
        return null;
    }

    private ArrayList<Double> getOrderAveragesForDates(ArrayList<Integer> dates) {
        return null;
    }

    private ArrayList<String> getRecentDates() {
        ArrayList<String> dates = new ArrayList<>();

        return dates;
    }

    private ArrayList<Integer> decodeDates(ArrayList<String> rawDates){
        ArrayList<Integer> dates = new ArrayList<>();

        return dates;
    }

}
