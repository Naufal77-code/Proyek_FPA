package purify;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLRegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button btnDaftar;

    @FXML
    private void handleDaftar() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // 1. Validasi Input
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username dan sandi tidak boleh kosong.");
            return;
        }

        if (username.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username minimal harus 4 karakter.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Sandi dan konfirmasi sandi tidak cocok.");
            return;
        }

        // 2. Proses Registrasi
        boolean isSuccess = ManajemenPengguna.getInstance().register(username, password);

        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil", "Akun berhasil dibuat. Silakan login.");
            // Tutup popup setelah berhasil
            Stage stage = (Stage) btnDaftar.getScene().getWindow();
            stage.close();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username '" + username + "' sudah digunakan.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}