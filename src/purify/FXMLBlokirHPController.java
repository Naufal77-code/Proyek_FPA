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

public class FXMLBlokirHPController implements Initializable {

    @FXML
    private TextField durasiField;
    @FXML
    private ComboBox<String> satuanWaktuComboBox;
    @FXML
    private TextField kodeDaruratField;
    @FXML
    private TextField aktivitasField;
    @FXML
    private Button btnBlokir;
    @FXML
    private Button btnMainMenu;

    private final RiwayatBlokirList riwayatList = RiwayatBlokirList.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        satuanWaktuComboBox.setItems(FXCollections.observableArrayList("Detik", "Menit", "Jam"));
        satuanWaktuComboBox.setValue("Menit");
    }

    @FXML
    private void handleBlokir(ActionEvent event) {
        try {
            validateInputs();
            long durasiValue = Long.parseLong(durasiField.getText().trim());
            String satuan = satuanWaktuComboBox.getValue();
            long durasiInSeconds = convertToSeconds(durasiValue, satuan);

            String kodeDarurat = kodeDaruratField.getText().trim();
            String aktivitas = aktivitasField.getText().trim();

            DetoxSession.getInstance().startDetox(durasiInSeconds, aktivitas, kodeDarurat);
            openBlockingScreen();

        } catch (NumberFormatException e) {
            showAlert("Error", "Durasi harus berupa angka!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert("Error", e.getMessage());
        }
    }

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

    public void addToRiwayat(String status) {
    DetoxSession session = DetoxSession.getInstance();
    int nextNumber = riwayatList.getData().size() + 1;
    riwayatList.setData(
            nextNumber,
            session.getFormattedWaktuMulai(),
            session.getDurasiInMinutes(),
            status,
            session.getAktivitas());
    clearInputFields();
    riwayatList.saveToXML(); // Pastikan data tersimpan
}

    private void openBlockingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBlokirStatus.fxml"));
            Parent root = loader.load();
            FXMLBlokirStatusController controller = loader.getController();
            controller.setMainController(this);
            Stage currentStage = (Stage) btnBlokir.getScene().getWindow();
            currentStage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman blokir! Pastikan file FXMLBlokirStatus.fxml sudah benar.");
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
            showAlert("Error", "Gagal membuka halaman utama!");
        }
    }

    private void clearInputFields() {
        durasiField.clear();
        kodeDaruratField.clear();
        aktivitasField.clear();
    }

    // Di dalam kelas FXMLBlokirHPController
    public void setPreset(String durasi, String satuan) {
        durasiField.setText(durasi);
        satuanWaktuComboBox.setValue(satuan);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}