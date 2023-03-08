package Utils;

import java.io.IOException;
import Controller.LoginController;
import Controller.EditMenuController;
import Controller.InventoryController;
import Controller.EmployeeController;
import Controller.OrderController;
import Controller.OrderHistoryController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SceneSwitch {
    private SessionData session;

    private LoginController loginController;
    private OrderController orderController;
    private OrderHistoryController orderHistoryController;
    private InventoryController inventoryController;
    private EmployeeController employeeController;
    private EditMenuController editMenuController;

    public SceneSwitch(SessionData session) {
        this.session = session;
        loginController = new LoginController(session);
        orderController = new OrderController(session);
        orderHistoryController = new OrderHistoryController(session);
        inventoryController = new InventoryController(session);
        employeeController = new EmployeeController(session);
        editMenuController = new EditMenuController(session);
    }

    public void LoginTransition(ActionEvent event, SessionData session) throws IOException {
        // Pass session object from login page to all scenes
        SceneSwitch login = new SceneSwitch(session);
        System.out.println("Login Page Initialized");
        Button b = (Button) event.getSource();

        // Load Order Scene from login page
        String buttonID = b.getId();
        System.out.println(buttonID);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/OrderScene.fxml"));
        loader.setController(orderController);
        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 800);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    public void switchScene(ActionEvent event) throws IOException {
        Button b = (Button) event.getSource();
        String buttonID = b.getId();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/OrderScene.fxml"));

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
            case "logoutButton":
                System.out.println("Logout button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/Login.fxml"));
                loader.setController(loginController);
                break;
            default:
                break;
        }

        Parent root = loader.load();
        Scene scene = new Scene(root, 1200, 800);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
