import Controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * This Main Class serves as the entry point for the POS System Application. It will initialize the
 * session and load to the login page
 * 
 * @since 2023-03-07
 * 
 * @version 2023-03-07
 * 
 * @author Dai, Kevin
 * @author Davis, Sloan
 * @author Kuppa Jayaram, Shreeman
 * @author Lai, Huy
 * @author Mao, Steven
 */
public class Main extends Application {
    /**
     * Load the graphical user interface
     * 
     * @param primaryStage initial {@link Stage}
     * @throws Exception when the gui fails to load
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        LoginController loginController = new LoginController();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("./FXML/Login.fxml"));
        loader.setController(loginController);

        Parent root = loader.load();

        primaryStage.setTitle("Chick-fil-A");
        primaryStage.getIcons().add(new Image("./resources/logo.png"));
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    /**
     * The classic main function
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Constructor
     */
    public Main() {}
}
