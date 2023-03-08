import Controller.OrderHistoryController;
import Items.Order;
import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import Utils.SessionData;
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

public class FakeMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConnect database;
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();

        SessionData session = new SessionData(database, 1, new Order(0));

        // EditMenuController menuController = new EditMenuController(session);
        OrderHistoryController orderHistoryController = new OrderHistoryController(session);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("./FXML/OrderHistory.fxml"));
        loader.setController(orderHistoryController);

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
