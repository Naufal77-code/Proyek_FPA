package Cpurify;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLMainMenuController implements Initializable {

    @FXML
    private Button blokirHP;

    @FXML
    private Button blokirAplikasi;

    @FXML
    private Button lihatStatistik;

    @FXML
    private Button konsultasiPsikolog;

    @FXML
    private Button komunitas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization code here
        // This method is called after the FXML file has been loaded
        // You can set up UI components, event handlers, etc. here
        // For example, you might want to initialize buttons or labels
        System.out.println("Main Menu Controller Initialized");
    }

    @FXML
    private void handleBlokirHP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXMLpurify/FXMLBlokirHP.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Blokir HP");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) blokirHP.getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Blokir HP screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleBlokirAplikasi(ActionEvent event) {
    
    }

    @FXML
    private void handleLihatStatistik(ActionEvent event) {
        
    }

    @FXML
    private void handleKonsultasiPsikolog(ActionEvent event) {
        
    }

    @FXML
    private void handleKomunitas(ActionEvent event) {
        
    }
}
