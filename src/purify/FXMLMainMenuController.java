package purify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.net.URL;

import java.io.IOException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;

public class FXMLMainMenuController implements Initializable {
    private static final RiwayatBlokirList riwayatList = new RiwayatBlokirList();

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

    @FXML
    private void handleBlokirHP(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirHP.fxml"));
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
    private void HandleBlokirAplikasi(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirAplikasi.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Blokir Aplikasi");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) blokirAplikasi.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Blokir Aplikasi screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLihatStatistik(ActionEvent event) {
    try {
        Stage currentStage = (Stage) lihatStatistik.getScene().getWindow();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLStatistik.fxml"));
        Parent root = loader.load();

        FXMLStatistikController controller = loader.getController();
        controller.setRiwayatList(riwayatList, "mainMenu", currentStage);
        
        Stage stage = new Stage();
        stage.setTitle("Lihat Statistik");
        stage.setScene(new Scene(root));
        stage.show();

        currentStage.hide();
    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Statistik screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
    } 
}

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
