package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.util.ArrayList;

import Utils.SessionData;
import Utils.DatabaseConnect;

public class EditMenuController {

    private DatabaseConnect database;
    private SessionData session;

    public EditMenuController(SessionData session) {
        this.session = session;
        this.database = session.getDatabase();
    }

    public void initialize() {

    }

    private void getMenuItemsQuery(){

    }

}
