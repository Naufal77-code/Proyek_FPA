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
   private final RiwayatBlokirList riwayatList = RiwayatBlokirList.getInstance();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirApps.fxml"));
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

    @FXML
    private void handleKomunitas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLKomunitas.fxml"));
            Parent root = loader.load();

            //FXMLKomunitasController controller = loader.getController();
            //controller.loadPosts(); // Load posts when opening komunitas

            Stage stage = new Stage();
            stage.setTitle("Komunitas");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) komunitas.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Komunitas screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleKonsultasiPsikolog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLKonsultasiPsikolog.fxml"));
            Parent root = loader.load();

            //FXMLKomunitasController controller = loader.getController();
            //controller.loadPosts(); // Load posts when opening komunitas

            Stage stage = new Stage();
            stage.setTitle("Konsultasi Psikolog");
            stage.setScene(new Scene(root));
            stage.show();

            // Close the current window
            Stage currentStage = (Stage) komunitas.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to open Konsultasi Psikolog screen");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}