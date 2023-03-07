package Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Utils.SessionData;
import Utils.DatabaseConnect;
import Utils.SceneSwitch;

public class EditMenuController {

    private DatabaseConnect database;
    private SessionData session;
    private ArrayList<String> menuItems;

    private SceneSwitch sceneSwitch;

    @FXML
    private ListView<String> menuList;

    @FXML
    private TextField menuIDText;

    @FXML
    private TextField menuNameText;

    @FXML
    private TextField menuCostText;

    @FXML
    private TextField menuNumSoldText;

    @FXML
    private CheckBox isDelete;

    @FXML
    private Button SubmitMenuChangeBtn;

    public EditMenuController() {

    }

    public EditMenuController(SessionData session) {
        this.session = session;
        this.database = session.database;
    }

    public SessionData getSession() {
        return this.session;
    }

    public void setSession(SessionData session) {
        this.session = session;
    }

    public void initialize() {
        ResultSet rs = getMenuItemsQuery();

        try {
            readMenuItems(rs);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("COULDN'T READ MENU ITEMS");
            e.printStackTrace();
        }

        addMenuItemsToListView();

        menuIDText.setText(null);
        menuNameText.setText(null);
        menuCostText.setText(null);
        menuNumSoldText.setText(null);
        isDelete.setSelected(false);
    }

    public void navButtonClicked(ActionEvent event) throws IOException {
        sceneSwitch = new SceneSwitch(session);
        sceneSwitch.switchScene(event);
    }

    private ResultSet getMenuItemsQuery() {
        String query = "SELECT * FROM menuitem";
        ResultSet rs = database.executeQuery(query);
        return rs;
    }

    private void readMenuItems(ResultSet rs) throws SQLException {
        menuItems = new ArrayList<>();
        if (rs == null) {
            menuItems.add("No Menu Items Retrieved");
            return;
        }
        while (rs.next()) {
            String curLine = "";
            curLine += "ID: " + Integer.valueOf(rs.getInt("id")).toString() + ", ";
            curLine += "name: " + rs.getString("name") + ", ";
            curLine += "cost: " + rs.getString("cost") + ", ";
            curLine += "numbersold: " + rs.getString("numbersold") + " ";
            menuItems.add(curLine);
        }
    }

    private void addMenuItemsToListView() {
        ObservableList<String> items = FXCollections.observableArrayList(menuItems);
        menuList.setItems(items);

    }



    @FXML
    private void submitMenuChange(ActionEvent e) {
        Integer itemID =
                (menuIDText.getText() == null) ? null : Integer.parseInt(menuIDText.getText());
        String itemName = menuNameText.getText();
        Double itemCost = (menuCostText.getText() == null) ? null
                : Double.parseDouble(menuCostText.getText());
        Integer itemNumSold = (menuNumSoldText.getText() == null) ? null
                : Integer.parseInt(menuNumSoldText.getText());

        if (isDelete.isSelected() == true) {
            deleteMenuItem(itemID);
        } else if (checkMenuItemExists(itemID) == true) {
            updateMenuItem(itemID, itemName, itemCost, itemNumSold);
        } else {
            addMenuItem(itemID, itemName, itemCost, itemNumSold);

        }
        initialize();

    }

    private boolean checkMenuItemExists(Integer itemID) {
        String query = "SELECT COUNT(*) FROM menuitem WHERE id = " + itemID.toString() + ";";
        ResultSet rs = database.executeQuery(query);

        try {
            if (rs.next()) {
                int has = rs.getInt(0);
                if (has == 0) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            return false;
        }
    }

    private void addMenuItem(Integer ID, String itemName, Double itemCost, Integer itemNumSold) {
        String query = "INSERT INTO menuitem (id, name, cost, numbersold) VALUES (" + ID.toString()
                + ", '" + itemName + "'," + itemCost.toString() + "," + itemNumSold.toString()
                + ");";
        System.out.println(query);
        database.executeQuery(query);
    }

    private void updateMenuItem(Integer itemID, String itemName, Double itemCost,
            Integer itemNumSold) {
        if (itemID == null) {
            return;
        }

        String query = "UPDATE menuitem " + "SET name = '" + itemName + "', cost = "
                + itemCost.toString() + ", numbersold = " + itemNumSold.toString() + " WHERE id = "
                + itemID.toString() + ";";

        System.out.println(query);
        database.executeQuery(query);
    }

    private void deleteMenuItem(Integer ID) {
        String query = "DELETE FROM menuitem WHERE id = " + ID.toString() + ";";
        System.err.println(query);
        database.executeQuery(query);

    }
}
