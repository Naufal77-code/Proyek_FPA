package purify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox; // Import CheckBox
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class FXMLLoginController {

    // Field teks untuk memasukkan username
    @FXML private TextField usernameField;

    // Field password khusus yang menyembunyikan input saat mengetik
    @FXML private PasswordField passwordField;

    // Tombol yang ditekan pengguna untuk memulai proses login
    @FXML private Button btnLogin;

    // Checkbox opsional yang memungkinkan pengguna menyimpan sesi login
    @FXML private CheckBox rememberMeCheck;

    /**
     * Method yang dipanggil saat tombol login ditekan.
     * Bertugas memverifikasi kredensial pengguna, menyimpan sesi jika perlu,
     * dan membuka halaman Main Menu jika login berhasil.
     */
    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText();  // Ambil teks dari field username
        String password = passwordField.getText();  // Ambil teks dari field password

        // Cek apakah kombinasi username & password valid
        if (ManajemenPengguna.getInstance().login(username, password)) {

            // Jika checkbox "Remember Me" dicentang, simpan username ke sesi
            if (rememberMeCheck.isSelected()) {
                SessionManager.saveSession(username);
            } else {
                SessionManager.clearSession(); // Jika tidak, pastikan sesi dibersihkan
            }

            // Login berhasil → alihkan ke halaman Main Menu
            Parent root = FXMLLoader.load(getClass().getResource("/purify/FXMLMainMenu.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow(); // Dapatkan jendela saat ini
            stage.setScene(new Scene(root));  // Atur scene baru untuk Main Menu
            stage.setTitle("Purify - Digital Detox");
        } else {
            // Tampilkan pesan error jika login gagal
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau sandi salah.");
        }
    }

    /**
     * Method yang dipanggil saat pengguna menekan link atau tombol untuk mendaftar.
     * Akan membuka halaman pendaftaran (register) sebagai popup modal.
     */
    @FXML
    private void handleLinkDaftar() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/purify/FXMLRegister.fxml")); // Load FXML daftar
        Stage popupStage = new Stage();
        popupStage.setTitle("Daftar Akun Baru");
        popupStage.initModality(Modality.APPLICATION_MODAL); // Buat jendela modal (blok input ke jendela utama)
        popupStage.initOwner(btnLogin.getScene().getWindow()); // Tetapkan owner popup ke jendela login
        popupStage.setScene(new Scene(root));
        popupStage.showAndWait(); // Tampilkan dan tunggu sampai user menutupnya
    }

    /**
     * Method pembantu untuk menampilkan alert (popup pesan) dengan tipe dan isi tertentu.
     *
     * @param type    Tipe alert (misalnya ERROR, INFORMATION)
     * @param title   Judul jendela alert
     * @param message Isi pesan alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Tidak ada header
        alert.setContentText(message); // Isi pesan
        alert.showAndWait(); // Tampilkan dan tunggu hingga ditutup
    }
}