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

    // Label untuk menampilkan sisa waktu detoks dalam format jam:menit:detik
    @FXML
    private Label timerLabel;

    // TextField tempat pengguna memasukkan kode darurat untuk membatalkan detoks
    @FXML
    private TextField kodeDaruratField;

    // Tombol untuk membatalkan sesi detoks dengan memasukkan kode darurat
    @FXML
    private Button btnBatalkan;

    // Tabel yang menampilkan daftar aplikasi yang sedang diblokir
    @FXML
    private TableView<String> appsTableView;

    // Kolom dalam tabel untuk menampilkan nama aplikasi
    @FXML
    private TableColumn<String, String> colAppName;

    // Objek Timeline untuk menghitung mundur detoks secara real-time
    private Timeline timeline;

    // Menyimpan detik yang tersisa selama sesi detoks berlangsung
    private int remainingSeconds;

    // Referensi ke controller utama (FXMLBlokirAppsController) untuk update riwayat
    private FXMLBlokirAppsController mainController;

    // Inisialisasi saat FXML dimuat
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mengambil instance sesi detoks aplikasi
        DetoxAppsSession session = DetoxAppsSession.getInstance();
        remainingSeconds = session.getDurasi();

        // Mengatur bagaimana nama aplikasi ditampilkan dalam kolom tabel
        colAppName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()));
        // Mengisi tabel dengan daftar aplikasi dari sesi saat ini
        appsTableView.setItems(FXCollections.observableArrayList(session.getSelectedApps()));

        // Mengatur dan memulai timer countdown detoks
        setupTimer();
        startTimer();

        // Menghubungkan event handler tombol batalkan (jaga-jaga jika FXML tidak
        // otomatis inject)
        if (btnBatalkan != null) {
            btnBatalkan.setOnAction(this::handleBatalkan);
        }
    }

    // Mengatur timer untuk menghitung mundur setiap detik
    private void setupTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                updateTimerDisplay(); // update tampilan waktu
                remainingSeconds--; // kurangi waktu
            } else {
                timeline.stop(); // waktu habis, akhiri detoks
                completeDetox("BERHASIL", DetoxAppsSession.getInstance().getDurasi());
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // jalankan terus-menerus
    }

    // Memulai timer dan memperbarui tampilan awal waktu
    private void startTimer() {
        updateTimerDisplay();
        timeline.play();
    }

    // Menampilkan waktu detoks tersisa dalam format HH:MM:SS
    private void updateTimerDisplay() {
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;
        int seconds = remainingSeconds % 60;
        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerLabel.setText(timeText);
    }

    // Event handler untuk tombol batalkan, memverifikasi kode darurat
    @FXML
    private void handleBatalkan(ActionEvent event) {
        String inputKode = kodeDaruratField.getText().trim();
        DetoxAppsSession session = DetoxAppsSession.getInstance();

        if (inputKode.isEmpty()) {
            showAlert("Error", "Masukkan kode darurat!");
            return;
        }

        if (inputKode.equals(session.getKodeDarurat())) {
            timeline.stop(); // hentikan timer
            int actualDuration = (int) session.getActualElapsedSeconds(); // hitung waktu yang telah berlalu
            completeDetox("GAGAL", actualDuration); // batalkan detoks
        } else {
            showAlert("Error", "Kode darurat salah!");
        }
    }

    // Mengakhiri sesi detoks, memperbarui riwayat, dan kembali ke halaman
    // sebelumnya
    private void completeDetox(String status, int durationInSeconds) {
        DetoxAppsSession.getInstance().endDetox(); // tandai sesi selesai

        // Jika controller utama tersedia, simpan ke riwayat
        if (mainController != null) {
            mainController.addToRiwayat(status, durationInSeconds);
        }

        returnToMainMenu(); // kembali ke halaman utama blokir aplikasi
    }

    // Navigasi kembali ke halaman FXMLBlokirApps.fxml
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

    // Setter untuk menyimpan referensi controller utama (untuk akses ke method
    // riwayat)
    public void setMainController(FXMLBlokirAppsController controller) {
        this.mainController = controller;
    }

    // Menampilkan alert popup sederhana
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
