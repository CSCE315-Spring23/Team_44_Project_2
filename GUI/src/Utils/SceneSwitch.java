package Utils;

import java.io.IOException;
import Controller.EditMenuController;
import Controller.InventoryController;
import Controller.OrderController;
import Controller.OrderHistoryController;
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

    // private LoginController loginController;

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

    // private EmployeeController employeeController;

    /**
     * {@link EditMenuController} to load the menu editting window
     */
    private EditMenuController editMenuController;

    /**
     * Constructor
     * 
     * @param session {@link SessionData} to send information between the various windows
     */
    public SceneSwitch(SessionData session) {
        this.session = session;

        // loginController = new LoginController(this.session);
        orderController = new OrderController(this.session);
        orderHistoryController = new OrderHistoryController(this.session);
        inventoryController = new InventoryController(this.session);
        // employeeController = new EmployeeController(this.session);
        editMenuController = new EditMenuController(this.session);
    }

    /**
     * Loads a the new window bassed on the navigation button pressed.
     * 
     * @param event {@link ActionEvent} passed when pressing a button
     * @throws IOException if the new window failed to load
     */
    public void switchScene(ActionEvent event) throws IOException {
        Button b = (Button) event.getSource();
        String buttonID = b.getId();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML/OrderHistory.fxml"));

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
                // loader = new FXMLLoader(getClass().getResource("../FXML/Employee.fxml"));
                // loader.setController(employeeController);
                break;
            case "editMenuButton":
                System.out.println("Edit Menu button clicked");
                loader = new FXMLLoader(getClass().getResource("../FXML/EditMenu.fxml"));
                loader.setController(editMenuController);
                break;
            case "logoutButton":
                System.out.println("Logout button clicked");
                // loader = new FXMLLoader(getClass().getResource("../FXML/Login.fxml"));
                // loader.setController(loginController);
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
