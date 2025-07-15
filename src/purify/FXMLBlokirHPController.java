package purify;

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

    @FXML private TextField durasiField;
    @FXML private TextField kodeDaruratField;
    @FXML private TextField aktivitasField;
    @FXML private Button btnBlokir;
    @FXML private Button btnStatistik;

    private static final RiwayatBlokirList riwayatList = new RiwayatBlokirList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inisialisasi jika diperlukan
    }

    @FXML
    private void handleBlokir(ActionEvent event) {
        try {
            validateInputs();
            int durasi = Integer.parseInt(durasiField.getText().trim());
            String kodeDarurat = kodeDaruratField.getText().trim();
            String aktivitas = aktivitasField.getText().trim();

            DetoxSession.getInstance().startDetox(durasi, aktivitas, kodeDarurat);
            openBlockingScreen();

        } catch (NumberFormatException e) {
            showAlert("Error", "Durasi harus berupa angka!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleStatistik(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLStatistik.fxml"));
            Parent root = loader.load();

            FXMLStatistikController controller = loader.getController();
            controller.setRiwayatList(riwayatList);

            Stage stage = new Stage();
            stage.setTitle("Statistik Digital Detox");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman statistik!");
        }
    }

   public void addToRiwayat(String status) {
        DetoxSession session = DetoxSession.getInstance();
        int nextNumber = riwayatList.getData().size() + 1;
        riwayatList.setData(
            nextNumber,
            session.getFormattedWaktuMulai(),
            session.getDurasi(),
            status,
            session.getAktivitas()
        );
        clearInputFields();
    }

    private void validateInputs() {
        if (durasiField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Durasi tidak boleh kosong!");
        }
        if (kodeDaruratField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode darurat tidak boleh kosong!");
        }
    }

    private void openBlockingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirStatus.fxml")); 
            Parent root = loader.load();

            FXMLBlokirStatusController controller = loader.getController();
            controller.setMainController(this);

            Stage currentStage = (Stage) btnBlokir.getScene().getWindow();
            currentStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman blokir!");
        }
    }

    private void clearInputFields() {
        durasiField.clear();
        kodeDaruratField.clear();
        aktivitasField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}