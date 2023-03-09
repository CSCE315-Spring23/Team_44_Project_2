import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The old version of main
 * 
 * @deprecated
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
public class OldMain extends Application {
    /**
     * Load the graphical user interface
     * 
     * @param primaryStage initial {@link Stage}
     * @throws Exception when the gui fails to load
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("./FXML/OrderHistory.fxml"));
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
}
