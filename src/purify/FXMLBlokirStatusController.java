package purify;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLBlokirStatusController implements Initializable {
    @FXML private Label timerLabel;
    @FXML private TextField kodeDaruratField;
    @FXML private Button btnBatalkan;
    
    private Timeline timeline;
    private long remainingSeconds;
    private FXMLBlokirHPController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DetoxSession session = DetoxSession.getInstance();
        remainingSeconds = session.getRemainingTimeInSeconds();
        setupTimer();
        startTimer();
    }

    private void setupTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                updateTimerDisplay();
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
        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerLabel.setText(timeText);
    }

    @FXML
    private void handleBatalkan(ActionEvent event) {
        String inputKode = kodeDaruratField.getText().trim();
        DetoxSession session = DetoxSession.getInstance();
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
        DetoxSession.getInstance().endDetox();
        if (mainController != null) {
            mainController.addToRiwayat(status);
        }
        returnToBlokirHP();
    }

    private void returnToBlokirHP() {
        if (timeline != null) {
            timeline.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBlokirHP.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) timerLabel.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainController(FXMLBlokirHPController controller) {
        this.mainController = controller;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}