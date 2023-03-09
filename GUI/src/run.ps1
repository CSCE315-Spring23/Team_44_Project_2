rm *.class
rm .\\**\\*.class
javac *.java --module-path "C:\\Program Files\\Java\\javafx-sdk-19.0.2.1\\lib" --add-modules javafx.controls,javafx.fxml
java --module-path "C:\\Program Files\\Java\\javafx-sdk-19.0.2.1\\lib" --add-modules javafx.controls,javafx.fxml -cp ";..\lib\postgresql-42.2.8.jar" Main.java