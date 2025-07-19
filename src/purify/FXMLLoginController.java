package purify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class FXMLLoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button btnLogin;

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (ManajemenPengguna.getInstance().login(username, password)) {
            // Jika login berhasil, buka Main Menu
            Parent root = FXMLLoader.load(getClass().getResource("FXMLMainMenu.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Purify - Digital Detox");
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau sandi salah.");
        }
    }

    @FXML
    private void handleLinkDaftar() throws IOException {
        // Buka popup registrasi
        Parent root = FXMLLoader.load(getClass().getResource("FXMLRegister.fxml"));
        Stage popupStage = new Stage();
        popupStage.setTitle("Daftar Akun Baru");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(btnLogin.getScene().getWindow());
        popupStage.setScene(new Scene(root));
        popupStage.showAndWait();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}