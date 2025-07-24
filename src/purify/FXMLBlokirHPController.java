package purify;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk halaman blokir HP pada aplikasi Purify.
 * Mengelola input durasi, kode darurat, aktivitas, serta navigasi ke halaman
 * status blokir.
 */
public class FXMLBlokirHPController implements Initializable {

    // Field input untuk durasi sesi detox
    @FXML
    private TextField durasiField;

    // ComboBox untuk memilih satuan waktu: Detik, Menit, atau Jam
    @FXML
    private ComboBox<String> satuanWaktuComboBox;

    // Field input kode darurat untuk membatalkan sesi detox
    @FXML
    private TextField kodeDaruratField;

    // Field input aktivitas yang akan dilakukan saat sesi detox
    @FXML
    private TextField aktivitasField;

    // Tombol untuk memulai sesi blokir
    @FXML
    private Button btnBlokir;

    // Tombol untuk kembali ke menu utama
    @FXML
    private Button btnMainMenu;

    // Singleton untuk mencatat riwayat sesi blokir
    private final RiwayatBlokirList riwayatList = RiwayatBlokirList.getInstance();

    /**
     * Inisialisasi ComboBox satuan waktu saat controller dimuat.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        satuanWaktuComboBox.setItems(FXCollections.observableArrayList("Detik", "Menit", "Jam"));
        satuanWaktuComboBox.setValue("Menit"); // Default satuan waktu
    }

    /**
     * Event handler untuk tombol "Blokir". Memulai sesi detox jika input valid.
     */
    @FXML
    private void handleBlokir(ActionEvent event) {
        try {
            validateInputs(); // Validasi semua input

            // Konversi durasi berdasarkan satuan yang dipilih
            long durasiValue = Long.parseLong(durasiField.getText().trim());
            String satuan = satuanWaktuComboBox.getValue();
            long durasiInSeconds = convertToSeconds(durasiValue, satuan);

            // Ambil input lainnya
            String kodeDarurat = kodeDaruratField.getText().trim();
            String aktivitas = aktivitasField.getText().trim();

            // Mulai sesi detox
            DetoxSession.getInstance().startDetox(durasiInSeconds, aktivitas, kodeDarurat);

            // Pindah ke layar status blokir
            openBlockingScreen();

        } catch (NumberFormatException e) {
            showAlert("Error", "Durasi harus berupa angka!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert("Error", e.getMessage());
        }
    }

    /**
     * Mengubah durasi menjadi detik sesuai satuan waktu yang dipilih.
     */
    private long convertToSeconds(long value, String unit) {
        switch (unit) {
            case "Detik":
                return value;
            case "Jam":
                return value * 3600;
            case "Menit":
            default:
                return value * 60;
        }
    }

    /**
     * Validasi input: durasi, kode darurat, dan satuan waktu tidak boleh kosong.
     */
    private void validateInputs() {
        if (durasiField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Durasi tidak boleh kosong!");
        }
        if (kodeDaruratField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode darurat tidak boleh kosong!");
        }
        if (satuanWaktuComboBox.getValue() == null) {
            throw new IllegalArgumentException("Satuan waktu harus dipilih!");
        }
    }

    /**
     * Menambahkan riwayat sesi detox ke daftar setelah sesi selesai.
     * 
     * @param status                  Status sesi (BERHASIL/GAGAL)
     * @param actualDurationInSeconds Durasi sebenarnya dalam detik
     */
    public void addToRiwayat(String status, long actualDurationInSeconds) {
        DetoxSession session = DetoxSession.getInstance();
        int nextNumber = riwayatList.getData().size() + 1;
        int durasiMenit = (int) Math.round(actualDurationInSeconds / 60.0);

        riwayatList.setData(
                nextNumber,
                session.getFormattedWaktuMulai(),
                durasiMenit,
                status,
                session.getAktivitas());

        clearInputFields(); // Kosongkan input setelah data dicatat
        riwayatList.saveToXML(); // Simpan ke file XML
    }

    /**
     * Membuka halaman FXMLBlokirStatus.fxml untuk menampilkan status blokir aktif.
     */
    private void openBlockingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirStatus.fxml"));
            Parent root = loader.load();

            // Ambil controller status dan kirim referensi controller utama
            FXMLBlokirStatusController controller = loader.getController();
            controller.setMainController(this);

            // Ganti scene
            Stage currentStage = (Stage) btnBlokir.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman blokir! Pastikan file FXMLBlokirStatus.fxml sudah benar.");
        }
    }

    /**
     * Event handler tombol "Main Menu". Kembali ke halaman utama aplikasi.
     */
    @FXML
    private void handleMainMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/purify/FXMLMainMenu.fxml"));
            Stage currentStage = (Stage) btnMainMenu.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Purify - Digital Detox");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman utama!");
        }
    }

    /**
     * Membersihkan semua input field agar siap digunakan kembali.
     */
    private void clearInputFields() {
        durasiField.clear();
        kodeDaruratField.clear();
        aktivitasField.clear();
    }

    /**
     * Mengatur preset input durasi dan satuan (digunakan jika ada tombol shortcut
     * durasi).
     */
    public void setPreset(String durasi, String satuan) {
        durasiField.setText(durasi);
        satuanWaktuComboBox.setValue(satuan);
    }

    /**
     * Menampilkan dialog informasi (alert) dengan judul dan pesan tertentu.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
