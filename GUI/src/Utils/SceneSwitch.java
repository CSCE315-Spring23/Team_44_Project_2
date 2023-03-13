package Utils;

import java.io.IOException;
import Controller.*;
import Controller.Reports.*;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * The class handles switching scenes when the Navigation buttons are pressed.
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
public class SceneSwitch {
    /**
     * {@link SessionData} to pass information between the various scenes
     */
    private SessionData session;

    /**
     * {@link LoginController} to load the Order window
     */
    private LoginController loginController;

    /**
     * {@link OrderController} to load the Order window
     */
    private EmployeeController employeeController;

    /**
     * {@link OrderController} to load the Order window
     */
    private OrderController orderController;

    /**
     * {@link OrderHistoryController} to load the order history window
     */
    private OrderHistoryController orderHistoryController;

    /**
     * {@link InventoryController} to load the inventory window
     */
    private InventoryController inventoryController;

    /**
     * {@link EditMenuController} to load the menu editting window
     */
    private EditMenuController editMenuController;

    /**
     * {@link DataTrendsController} to load the menu editting window
     */
    private DataTrendsController dataTrendsController;

    private SalesReport salesReportController;

    /**
     * Constructor
     * 
     * @param session {@link SessionData} to send information between the various windows
     */
    public SceneSwitch(SessionData session) {
        this.session = session;
        this.loginController = new LoginController(this.session);
        this.orderController = new OrderController(this.session);
        this.orderHistoryController = new OrderHistoryController(this.session);
        this.inventoryController = new InventoryController(this.session);
        this.employeeController = new EmployeeController(this.session);
        this.editMenuController = new EditMenuController(this.session);
        this.dataTrendsController = new DataTrendsController(this.session);

        this.salesReportController = new SalesReport(this.session);
    }

    /**
     * Loads a the new window bassed on the navigation button pressed.
     * 
     * @param event {@link ActionEvent} passed when pressing a {@link Button}
     * @param session {@link SessionData} to pass between controllers
     * @throws IOException if the new window failed to load
     */
    public void LoginTransition(ActionEvent event, SessionData session) throws IOException {
        // Pass session object from login page to all scenes
        System.out.println("Login Page Initialized");
        // final Button b = (Button) event.getSource();
        // System.out.println(this.session.employeeId);

        // Load Order Scene from login page
        // String buttonID = b.getId();
        // System.out.println(buttonID);
        final FXMLLoader loader =
                new FXMLLoader(this.getClass().getResource("../FXML/OrderScene.fxml"));
        loader.setController(orderController);

        final Parent root = loader.load();
        final Scene scene = new Scene(root, 1200, 800);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Loads a new Controller based on which navigation button was pressed
     * 
     * @param event {@link ActionEvent} of the {@link Button} pressed
     * @throws IOException if loading the new window failed
     */
    public void switchScene(ActionEvent event) throws IOException {
        final Button b = (Button) event.getSource();
        final String buttonID = b.getId();

        FXMLLoader loader = null;

        switch (buttonID) {
            case "orderButton":
                System.out.println("Order button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/OrderScene.fxml"));
                loader.setController(orderController);
                break;
            case "orderHistoryButton":
                System.out.println("Order History button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/OrderHistory.fxml"));
                loader.setController(orderHistoryController);
                break;
            case "inventoryButton":
                System.out.println("Inventory button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/Inventory.fxml"));
                loader.setController(inventoryController);
                break;
            case "employeesButton":
                System.out.println("Employees button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/Employee.fxml"));
                loader.setController(employeeController);
                break;
            case "editMenuButton":
                System.out.println("Edit Menu button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/EditMenu.fxml"));
                loader.setController(editMenuController);
                break;
            case "dataTrendsButton":
                System.out.println("Data Trends button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/DataTrends.fxml"));
                loader.setController(dataTrendsController);
                break;
            case "logoutButton":
                System.out.println("Logout button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/Login.fxml"));
                loader.setController(loginController);
                break;
            default:
                throw new IllegalStateException("Invalid button pressed");
        }

        final Parent root = loader.load();
        final Scene scene = new Scene(root, 1200, 800);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void switchReportScene(ActionEvent event) throws IOException{
        final Button b = (Button) event.getSource();
        final String buttonID = b.getId();

        FXMLLoader loader = null;

        switch (buttonID) {
            case "salesReportButton":
                System.out.println("Sales Report button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/Reports/SalesReport.fxml"));
                loader.setController(salesReportController);
                break;
            default:
                throw new IllegalStateException("Invalid button pressed");
        }

        final Parent root = loader.load();
        final Scene scene = new Scene(root, 1200, 800);
        final Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
