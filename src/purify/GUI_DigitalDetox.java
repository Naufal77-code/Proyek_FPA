package tes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_DigitalDetox extends Application {

    @Override
    public void start(Stage stage) throws Exception {
         Parent root = FXMLLoader.load(getClass().getResource("/tes/FXMLDocument.fxml"));
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Digital Detox - Blokir HP");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}