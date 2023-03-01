import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

    @FXML
    private Label label;
    @FXML
    private TextArea textbox;

    public void initialize() {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " + javaVersion + ".");

        textbox.setText("Hello World");
    }
}