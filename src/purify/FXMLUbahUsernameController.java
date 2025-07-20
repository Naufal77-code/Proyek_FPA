package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLUbahUsernameController {
    
    @FXML private TextField usernameField;
    @FXML private Button btnBatal;
    @FXML private Button btnSimpan;

    @FXML
    private void handleSimpan(ActionEvent event) {
        String newUsername = usernameField.getText().trim();

        if (newUsername.isEmpty() || newUsername.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Username baru minimal harus 4 karakter.");
            return;
        }

        ManajemenPengguna mu = ManajemenPengguna.getInstance();
        
        // Panggil metode ubahUsername yang baru (tidak perlu username lama)
        boolean isSuccess = mu.ubahUsername(newUsername);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Username berhasil diubah!");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Username '" + newUsername + "' sudah digunakan atau terjadi kesalahan.");
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