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
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLBlokirAppsController implements Initializable {

    @FXML private TextField durasiField;
    @FXML private ComboBox<String> satuanWaktuComboBox;
    @FXML private TextField kodeDaruratField;
    @FXML private TextField aktivitasField;
    @FXML private Button btnBlokir;
    @FXML private Button btnMainMenu;

    // Komponen UI Baru
    @FXML private ListView<String> lvAplikasiTersedia;
    @FXML private ListView<String> lvAplikasiDipilih;
    @FXML private Button btnTambahLainnya;
    @FXML private Button btnPindahKanan;
    @FXML private Button btnPindahSemuaKanan;
    @FXML private Button btnPindahKiri;
    @FXML private Button btnPindahSemuaKiri;

    private static final RiwayatBlokirAppsList riwayatList = RiwayatBlokirAppsList.getInstance();
    
    private ObservableList<String> aplikasiTersedia = FXCollections.observableArrayList();
    private ObservableList<String> aplikasiDipilih = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inisialisasi ComboBox
        satuanWaktuComboBox.setItems(FXCollections.observableArrayList("Detik", "Menit", "Jam"));
        satuanWaktuComboBox.setValue("Menit");

        // Inisialisasi daftar aplikasi
        List<String> daftarAplikasiDefault = Arrays.asList(
            "Instagram", "TikTok", "Facebook", "Twitter/X", "WhatsApp", 
            "Telegram", "YouTube", "Netflix", "Spotify", "Snapchat", 
            "Discord", "Twitch"
        );
        aplikasiTersedia.setAll(daftarAplikasiDefault);

        // Atur ListView
        lvAplikasiTersedia.setItems(aplikasiTersedia);
        lvAplikasiDipilih.setItems(aplikasiDipilih);
        
        // Izinkan multiple selection
        lvAplikasiTersedia.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        lvAplikasiDipilih.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void handleBlokir(ActionEvent event) {
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
                aktivitas = "Aktivitas tidak ada";
            }

            DetoxAppsSession.getInstance().startDetox(durasiDetik, aktivitas, kodeDarurat, selectedApps);
            openBlockingScreen();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Durasi harus berupa angka!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    private void openBlockingScreen() {
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

    public void addToRiwayat(String status) {
        DetoxAppsSession session = DetoxAppsSession.getInstance();
        riwayatList.addData(
                session.getFormattedWaktuMulai(),
                session.getDurasi(),
                status,
                session.getAktivitas(),
                session.getSelectedAppsString());
        clearFields();
    }

    private void clearFields() {
        durasiField.clear();
        kodeDaruratField.clear();
        aktivitasField.clear();
        aplikasiDipilih.clear(); // Hapus semua aplikasi yang dipilih
    }

    // --- Logika Baru untuk Memindahkan Aplikasi ---
    @FXML
    private void handlePindahKanan() {
        ObservableList<String> selected = lvAplikasiTersedia.getSelectionModel().getSelectedItems();
        if (selected != null) {
            aplikasiDipilih.addAll(selected);
            aplikasiTersedia.removeAll(selected);
        }
    }

    @FXML
    private void handlePindahKiri() {
        ObservableList<String> selected = lvAplikasiDipilih.getSelectionModel().getSelectedItems();
        if (selected != null) {
            aplikasiTersedia.addAll(selected);
            aplikasiDipilih.removeAll(selected);
        }
    }

    @FXML
    private void handlePindahSemuaKanan() {
        aplikasiDipilih.addAll(aplikasiTersedia);
        aplikasiTersedia.clear();
    }

    @FXML
    private void handlePindahSemuaKiri() {
        aplikasiTersedia.addAll(aplikasiDipilih);
        aplikasiDipilih.clear();
    }

    @FXML
    private void handleTambahAplikasiLain(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLTambahAplikasiLain.fxml"));
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.setTitle("Tambah Aplikasi Lain");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(((Button)event.getSource()).getScene().getWindow());
            
            FXMLTambahAplikasiLainController controller = loader.getController();
            controller.setStage(popupStage);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            // Ambil nama aplikasi setelah popup ditutup
            String namaAplikasiBaru = controller.getNamaAplikasi();
            if (namaAplikasiBaru != null && !namaAplikasiBaru.isEmpty() && !aplikasiTersedia.contains(namaAplikasiBaru) && !aplikasiDipilih.contains(namaAplikasiBaru)) {
                aplikasiTersedia.add(namaAplikasiBaru);
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka jendela tambah aplikasi.");
        }
    }

    // --- Metode Bantuan ---
    private int convertToSeconds(int value, String unit) {
        switch (unit) {
            case "Detik": return value;
            case "Jam": return value * 3600;
            case "Menit":
            default: return value * 60;
        }
    }

    @FXML
    private void handleMainMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLMainMenu.fxml"));
            Stage currentStage = (Stage) btnMainMenu.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Purify - Digital Detox");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman utama!");
        }
    }

    // Di dalam kelas FXMLBlokirAppsController
    public void setPreset(String durasi, String satuan, List<String> appsToBlock) {
        durasiField.setText(durasi);
        satuanWaktuComboBox.setValue(satuan);

    // Pindahkan aplikasi sesuai preset
     aplikasiDipilih.addAll(appsToBlock);
     aplikasiTersedia.removeAll(appsToBlock);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}