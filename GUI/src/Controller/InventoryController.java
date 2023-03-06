package Controller;

import Utils.DatabaseConnect;
import Utils.DatabaseLoginInfo;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class InventoryController {
    DatabaseConnect database;

    @FXML
    private TextArea inventory;

    public void initialize() {
        // database login info
        String dbConnectionString = DatabaseLoginInfo.dbConnectionString;
        String username = DatabaseLoginInfo.username;
        String password = DatabaseLoginInfo.password;

        database = new DatabaseConnect(dbConnectionString, username, password);
        database.setUpDatabase();
        System.out.println("Database Connection Established");
    }
}
