package purify;

import javafx.collections.FXCollections;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLBlokirAppsStatusController implements Initializable {

    @FXML private Label timerLabel;
    @FXML private TextField kodeDaruratField;
    @FXML private Button btnBatalkan;

    // [MODIFIKASI] Deklarasi untuk komponen baru
    @FXML private TableView<String> appsTableView;
    @FXML private TableColumn<String, String> colAppName;

    private Timeline timeline;
    private int remainingSeconds;
    private FXMLBlokirAppsController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DetoxAppsSession session = DetoxAppsSession.getInstance();
        remainingSeconds = session.getDurasi();

        // --- [MODIFIKASI] Setup untuk tabel aplikasi ---
        // Mengatur bagaimana setiap sel di kolom akan mendapatkan nilainya (dalam hal ini, string nama aplikasi itu sendiri)
        colAppName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));
        // Mengisi tabel dengan daftar aplikasi yang diblokir dari sesi
        appsTableView.setItems(FXCollections.observableArrayList(session.getSelectedApps()));

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
                completeDetox("BERHASIL", DetoxAppsSession.getInstance().getDurasi());
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
        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerLabel.setText(timeText);
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
            int actualDuration = (int) session.getActualElapsedSeconds();
            completeDetox("GAGAL", actualDuration);
        } else {
            showAlert("Error", "Kode darurat salah!");
        }
    }

    private void completeDetox(String status, int durationInSeconds) {
        DetoxAppsSession.getInstance().endDetox();
        if (mainController != null) {
            mainController.addToRiwayat(status, durationInSeconds);
        }
        returnToMainMenu();
    }

    private void returnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirApps.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) timerLabel.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke menu utama!");
        }
    }

    public void setMainController(FXMLBlokirAppsController controller) {
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