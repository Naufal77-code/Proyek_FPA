package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class FXMLUbahSandiController {
    
    @FXML private PasswordField sandiLamaField;
    @FXML private PasswordField sandiBaruField;
    @FXML private PasswordField konfirmasiSandiField;
    @FXML private Button btnBatal;
    @FXML private Button btnSimpan;

    @FXML
    private void handleSimpan(ActionEvent event) {
        String sandiLama = sandiLamaField.getText();
        String sandiBaru = sandiBaruField.getText();
        String konfirmasiSandi = konfirmasiSandiField.getText();

        if (sandiBaru.isEmpty() || !sandiBaru.equals(konfirmasiSandi)) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Sandi baru dan konfirmasi tidak cocok.");
            return;
        }

        ManajemenPengguna mu = ManajemenPengguna.getInstance();
        
        // Panggil metode ubahSandi yang baru (tidak perlu username)
        boolean isSuccess = mu.ubahSandi(sandiLama, sandiBaru);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Sandi berhasil diubah!");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Sandi lama salah atau terjadi kesalahan.");
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnBatal.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}