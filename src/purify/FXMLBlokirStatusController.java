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

/**
 * Controller untuk tampilan status sesi detoksifikasi yang sedang berjalan.
 * Menampilkan timer mundur dan memungkinkan pengguna membatalkan dengan kode
 * darurat.
 */
public class FXMLBlokirStatusController implements Initializable {

    // Label yang menampilkan sisa waktu dalam format jam:menit:detik
    @FXML
    private Label timerLabel;

    // Field tempat pengguna bisa memasukkan kode darurat untuk membatalkan detoks
    @FXML
    private TextField kodeDaruratField;

    // Tombol untuk membatalkan sesi detoks jika kode benar
    @FXML
    private Button btnBatalkan;

    // Timeline untuk menjalankan countdown timer per detik
    private Timeline timeline;

    // Waktu sisa dalam detik
    private long remainingSeconds;

    // Referensi ke controller utama (FXMLBlokirHP) untuk update riwayat
    private FXMLBlokirHPController mainController;

    /**
     * Dipanggil saat FXML dimuat. Menginisialisasi timer berdasarkan sesi detoks
     * aktif.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DetoxSession session = DetoxSession.getInstance();
        remainingSeconds = session.getRemainingTimeInSeconds(); // Ambil waktu sisa dari sesi
        setupTimer(); // Siapkan logika timer
        startTimer(); // Mulai hitung mundur
    }

    /**
     * Membuat timeline yang akan berjalan tiap 1 detik untuk menurunkan sisa waktu.
     * Jika waktu habis, sesi dianggap berhasil.
     */
    private void setupTimer() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            if (remainingSeconds > 0) {
                remainingSeconds--;
                updateTimerDisplay();
            } else {
                timeline.stop();
                // Sesi selesai secara alami → dianggap BERHASIL
                completeDetox("BERHASIL", DetoxSession.getInstance().getDurasiInSeconds());
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Memulai timer mundur dan langsung menampilkan waktu awal.
     */
    private void startTimer() {
        updateTimerDisplay();
        timeline.play();
    }

    /**
     * Mengupdate tampilan label timer agar menampilkan jam:menit:detik dari waktu
     * sisa.
     */
    private void updateTimerDisplay() {
        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        String timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        timerLabel.setText(timeText);
    }

    /**
     * Event handler saat tombol 'Batalkan' ditekan.
     * Validasi input kode darurat dan batalkan sesi jika benar.
     */
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
            // Hitung durasi aktual yang sudah berjalan
            long actualDuration = session.getActualElapsedSeconds();
            completeDetox("GAGAL", actualDuration);
        } else {
            showAlert("Error", "Kode darurat salah!");
        }
    }

    /**
     * Menyelesaikan sesi detoks dan mencatat hasilnya.
     * 
     * @param status            "BERHASIL" atau "GAGAL"
     * @param durationInSeconds durasi sesi yang tercatat
     */
    private void completeDetox(String status, long durationInSeconds) {
        DetoxSession.getInstance().endDetox(); // Reset sesi
        if (mainController != null) {
            mainController.addToRiwayat(status, durationInSeconds); // Tambah ke riwayat
        }
        returnToBlokirHP(); // Kembali ke halaman utama
    }

    /**
     * Navigasi kembali ke halaman FXMLBlokirHP.fxml setelah sesi selesai.
     */
    private void returnToBlokirHP() {
        if (timeline != null) {
            timeline.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirHP.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) timerLabel.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter untuk menyimpan referensi ke controller utama agar bisa mengupdate
     * data.
     */
    public void setMainController(FXMLBlokirHPController controller) {
        this.mainController = controller;
    }

    /**
     * Menampilkan alert popup sederhana kepada pengguna.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
