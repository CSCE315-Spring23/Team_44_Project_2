import Controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/*
 * 
 * Hello Everyone, this is a fake main class that shows how we can pass a controller that has the
 * session in the constructor. If you get an error REMOVE CONTROLLER FROM FXML.
 */

public class Main extends Application {

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

    public static void main(String[] args) {
        Application.launch(args);
    }
}
