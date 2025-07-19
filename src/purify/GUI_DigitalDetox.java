package purify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_DigitalDetox extends Application {

    // Di dalam GUI_DigitalDetox.java
    @Override
    public void start(Stage stage) throws Exception {
        // Ganti FXMLMainMenu.fxml menjadi FXMLLogin.fxml
        Parent root = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));

        Scene scene = new Scene(root);

        stage.setTitle("Purify - Login"); // Ganti judul awal
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}