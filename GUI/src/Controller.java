import java.sql.*;


import javafx.fxml.FXML;
import javafx.scene.control.*;


public class Controller {

    //database connection
    private Connection conn;


    @FXML
    private Label label;
    @FXML
    private TextArea textbox;

    public void initialize() {
        System.out.println("Initialized");
        setUpDatabase();
        textbox.setText(submitQuery("SElECT * FROM menuitem"));
    }


    public void setUpDatabase(){
        //database login info
        String teamNumber = "team_44";
        String dbName = "csce315331_" + teamNumber;
        String dbConnectionString = "jdbc:postgresql://csce-315-db.engr.tamu.edu/" + dbName;
        String username = "csce315331_team_44_master"; // change to your username
        String password = "ShreemanLikesDeepWork"; //nothing to see here
        
        try{
            conn = DriverManager.getConnection(dbConnectionString, username, password);
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error connecting to database");
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }


    public String submitQuery(String query){
        String ret = "";

        System.out.println(query);
        try{
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                ret += rs.getString("name") + "\n";
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("Error accesing database");
            System.exit(0);
        }
        System.out.println(ret);
        return ret;
    }
}