package purify;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLBlokirAppsStatusController implements Initializable {

    @FXML
    private Label timerLabel;

    @FXML
    private Label appsLabel;

    @FXML
    private TextField kodeDaruratField;

    @FXML
    private Button btnBatalkan;

    private Timeline timeline;
    private int remainingSeconds;
    private FXMLBlokirAppsController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DetoxAppsSession session = DetoxAppsSession.getInstance();
        remainingSeconds = session.getDurasi(); // Convert minutes to seconds

        // Tampilkan apps yang diblokir
        appsLabel.setText("Apps yang diblokir: " + session.getSelectedAppsString());

        setupTimer();
        startTimer();

        if (btnBatalkan != null) {
            btnBatalkan.setOnAction(this::handleBatalkan);
        }
    }

    private void setupTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                updateTimerDisplay();
                remainingSeconds--;
            } else {
                timeline.stop();
                completeDetox("BERHASIL");
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    private void startTimer() {
        updateTimerDisplay();
        timeline.play();
    }

    private void updateTimerDisplay() {
        int hours = remainingSeconds / 3600; 
        int minutes = (remainingSeconds % 3600) / 60; 
        int seconds = remainingSeconds % 60; 
        
        String timeText;
        if (hours > 0) {
            timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds); 
        } else {
            timeText = String.format("%02d:%02d", minutes, seconds); 
        }
        
        if (timerLabel != null) {
            timerLabel.setText(timeText);
        }
    }

    @FXML
    private void handleBatalkan(ActionEvent event) {
        String inputKode = kodeDaruratField.getText().trim();
        DetoxAppsSession session = DetoxAppsSession.getInstance();

        if (inputKode.isEmpty()) {
            showAlert("Error", "Masukkan kode darurat!");
            return;
        }

        if (inputKode.equals(session.getKodeDarurat())) {
            timeline.stop();
            completeDetox("GAGAL");
        } else {
            showAlert("Error", "Kode darurat salah!");
        }
    }

    private void completeDetox(String status) {
        DetoxAppsSession.getInstance().endDetox();
        
        if (mainController != null) {
            mainController.addToRiwayat(status);
        }

        returnToMainMenu();
    }

    private void returnToMainMenu() {
        try {
            FXMLLoader loader =new FXMLLoader(getClass().getResource("/Purify/FXMLBlokirApps.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) timerLabel.getScene().getWindow();
            currentStage.setScene(new Scene(root));
}catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke menu utama!");
    }
}
 public void setMainController(FXMLBlokirAppsController controller) {
        this.mainController = controller;
    }

    // Metode utilitas untuk menampilkan alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}