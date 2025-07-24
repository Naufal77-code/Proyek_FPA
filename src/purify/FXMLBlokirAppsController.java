package purify;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLBlokirAppsController implements Initializable {

    // === Deklarasi Komponen UI (diambil dari FXML) ===

    @FXML
    private TextField durasiField; // Field untuk input durasi sesi detox
    @FXML
    private ComboBox<String> satuanWaktuComboBox; // Dropdown untuk memilih satuan waktu (detik, menit, jam)
    @FXML
    private TextField kodeDaruratField; // Field untuk input kode darurat agar sesi detox bisa dihentikan
    @FXML
    private TextField aktivitasField; // Field untuk deskripsi aktivitas saat detox
    @FXML
    private Button btnBlokir; // Tombol untuk memulai sesi blokir
    @FXML
    private Button btnMainMenu; // Tombol untuk kembali ke menu utama
    @FXML
    private ListView<String> lvAplikasiTersedia; // Daftar aplikasi yang bisa dipilih untuk diblokir
    @FXML
    private ListView<String> lvAplikasiDipilih; // Daftar aplikasi yang telah dipilih untuk diblokir
    @FXML
    private Button btnTambahLainnya; // Tombol untuk menambah aplikasi lain secara manual
    @FXML
    private Button btnPindahKanan; // Tombol untuk memindahkan aplikasi terpilih ke daftar blokir
    @FXML
    private Button btnPindahSemuaKanan; // Tombol untuk memindahkan semua aplikasi ke daftar blokir
    @FXML
    private Button btnPindahKiri; // Tombol untuk mengembalikan aplikasi dari daftar blokir ke daftar tersedia
    @FXML
    private Button btnPindahSemuaKiri; // Tombol untuk mengembalikan semua aplikasi ke daftar tersedia

    // === Atribut Logika Program ===

    private static final RiwayatBlokirAppsList riwayatList = RiwayatBlokirAppsList.getInstance(); // Singleton untuk
                                                                                                  // mencatat riwayat
                                                                                                  // sesi blokir
    private ObservableList<String> aplikasiTersedia = FXCollections.observableArrayList(); // List aplikasi yang bisa
                                                                                           // dipilih
    private ObservableList<String> aplikasiDipilih = FXCollections.observableArrayList(); // List aplikasi yang telah
                                                                                          // dipilih

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inisialisasi UI awal
        satuanWaktuComboBox.setItems(FXCollections.observableArrayList("Detik", "Menit", "Jam"));
        satuanWaktuComboBox.setValue("Menit"); // Default satuan waktu

        List<String> daftarAplikasiDefault = Arrays.asList(
                "Instagram", "TikTok", "Facebook", "Twitter/X", "WhatsApp",
                "Telegram", "YouTube", "Netflix", "Spotify", "Snapchat",
                "Discord", "Twitch");
        aplikasiTersedia.setAll(daftarAplikasiDefault); // Set aplikasi default
        lvAplikasiTersedia.setItems(aplikasiTersedia);
        lvAplikasiDipilih.setItems(aplikasiDipilih);

        // Izinkan pemilihan banyak aplikasi sekaligus
        lvAplikasiTersedia.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvAplikasiDipilih.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void handleBlokir(ActionEvent event) {
        // Validasi input dan memulai sesi detox
        try {
            String durasiText = durasiField.getText().trim();
            if (durasiText.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Durasi tidak boleh kosong!");
                return;
            }

            int durasiValue = Integer.parseInt(durasiText);
            if (durasiValue <= 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Durasi harus lebih dari 0!");
                return;
            }

            String satuan = satuanWaktuComboBox.getValue();
            int durasiDetik = convertToSeconds(durasiValue, satuan);

            String kodeDarurat = kodeDaruratField.getText().trim();
            if (kodeDarurat.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Kode darurat tidak boleh kosong!");
                return;
            }

            if (aplikasiDipilih.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Pilih minimal satu aplikasi untuk diblokir!");
                return;
            }

            List<String> selectedApps = new ArrayList<>(aplikasiDipilih);

            String aktivitas = aktivitasField.getText().trim();
            if (aktivitas.isEmpty()) {
                aktivitas = "Aktivitas tidak ada"; // Default jika kosong
            }

            DetoxAppsSession.getInstance().startDetox(durasiDetik, aktivitas, kodeDarurat, selectedApps);
            openBlockingScreen(); // Pindah ke tampilan status blokir

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Durasi harus berupa angka!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void openBlockingScreen() {
        // Menampilkan halaman status pemblokiran aplikasi
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirAppsStatus.fxml"));
            Parent root = loader.load();
            FXMLBlokirAppsStatusController controller = loader.getController();
            controller.setMainController(this);
            Stage currentStage = (Stage) btnBlokir.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman blokir!");
        }
    }

    public void addToRiwayat(String status, int actualDurationInSeconds) {
        // Menyimpan riwayat sesi detox ke dalam list riwayat
        DetoxAppsSession session = DetoxAppsSession.getInstance();
        riwayatList.addData(
                session.getFormattedWaktuMulai(),
                actualDurationInSeconds,
                status,
                session.getAktivitas(),
                session.getSelectedAppsString());
        clearFields(); // Reset semua input
    }

    private void clearFields() {
        // Menghapus semua input agar siap untuk sesi baru
        durasiField.clear();
        kodeDaruratField.clear();
        aktivitasField.clear();
        aplikasiDipilih.clear();
    }

    @FXML
    private void handlePindahKanan() {
        // Pindahkan aplikasi yang dipilih ke daftar blokir
        ObservableList<String> selected = lvAplikasiTersedia.getSelectionModel().getSelectedItems();
        if (selected != null) {
            aplikasiDipilih.addAll(selected);
            aplikasiTersedia.removeAll(selected);
        }
    }

    @FXML
    private void handlePindahKiri() {
        // Kembalikan aplikasi dari daftar blokir ke daftar tersedia
        ObservableList<String> selected = lvAplikasiDipilih.getSelectionModel().getSelectedItems();
        if (selected != null) {
            aplikasiTersedia.addAll(selected);
            aplikasiDipilih.removeAll(selected);
        }
    }

    @FXML
    private void handlePindahSemuaKanan() {
        // Pindahkan semua aplikasi ke daftar blokir
        aplikasiDipilih.addAll(aplikasiTersedia);
        aplikasiTersedia.clear();
    }

    @FXML
    private void handlePindahSemuaKiri() {
        // Kembalikan semua aplikasi ke daftar tersedia
        aplikasiTersedia.addAll(aplikasiDipilih);
        aplikasiDipilih.clear();
    }

    @FXML
    private void handleTambahAplikasiLain(ActionEvent event) {
        // Menampilkan dialog untuk menambahkan aplikasi manual
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLTambahAplikasiLain.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Tambah Aplikasi Lain");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Button) event.getSource()).getScene().getWindow());

            FXMLTambahAplikasiLainController controller = loader.getController();
            controller.setStage(popupStage);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            String namaAplikasiBaru = controller.getNamaAplikasi();
            if (namaAplikasiBaru != null && !namaAplikasiBaru.isEmpty()
                    && !aplikasiTersedia.contains(namaAplikasiBaru)
                    && !aplikasiDipilih.contains(namaAplikasiBaru)) {
                aplikasiTersedia.add(namaAplikasiBaru);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka jendela tambah aplikasi.");
        }
    }

    private int convertToSeconds(int value, String unit) {
        // Mengonversi durasi ke satuan detik
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

    @FXML
    private void handleMainMenu(ActionEvent event) {
        // Kembali ke halaman menu utama
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/purify/FXMLMainMenu.fxml"));
            Stage currentStage = (Stage) btnMainMenu.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Purify - Digital Detox");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman utama!");
        }
    }

    public void setPreset(String durasi, String satuan, List<String> appsToBlock) {
        // Menetapkan nilai preset ke field jika pengguna kembali dari halaman preset
        durasiField.setText(durasi);
        satuanWaktuComboBox.setValue(satuan);
        aplikasiDipilih.addAll(appsToBlock);
        aplikasiTersedia.removeAll(appsToBlock);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        // Utilitas untuk menampilkan popup peringatan atau informasi
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}