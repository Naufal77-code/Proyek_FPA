package purify;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLRegisterController {

    // Field input untuk username pengguna yang ingin mendaftar
    @FXML
    private TextField usernameField;

    // Field input untuk sandi (password) pengguna
    @FXML
    private PasswordField passwordField;

    // Field input untuk konfirmasi sandi agar cocok dengan passwordField
    @FXML
    private PasswordField confirmPasswordField;

    // Tombol yang akan men-trigger proses registrasi ketika diklik
    @FXML
    private Button btnDaftar;

    /**
     * Method yang menangani aksi saat tombol "Daftar" ditekan.
     * Melakukan validasi input, kemudian memproses registrasi menggunakan
     * ManajemenPengguna.
     */
    @FXML
    private void handleDaftar() {
        // Ambil nilai input dari field dan hilangkan spasi berlebih
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // ========== Bagian 1: Validasi Input ==========

        // Validasi jika field username atau password kosong
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username dan sandi tidak boleh kosong.");
            return;
        }

        // Validasi panjang minimal username
        if (username.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username minimal harus 4 karakter.");
            return;
        }

        // Validasi kecocokan password dan konfirmasi password
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Sandi dan konfirmasi sandi tidak cocok.");
            return;
        }

        // ========== Bagian 2: Proses Registrasi ==========

        // Panggil method register dari singleton ManajemenPengguna
        boolean isSuccess = ManajemenPengguna.getInstance().register(username, password);

        if (isSuccess) {
            // Tampilkan notifikasi berhasil
            showAlert(Alert.AlertType.INFORMATION, "Registrasi Berhasil", "Akun berhasil dibuat. Silakan login.");
            // Tutup jendela registrasi setelah berhasil
            Stage stage = (Stage) btnDaftar.getScene().getWindow();
            stage.close();
        } else {
            // Tampilkan pesan jika username sudah digunakan
            showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username '" + username + "' sudah digunakan.");
        }
    }

    /**
     * Menampilkan popup alert kepada pengguna.
     * Digunakan untuk menunjukkan hasil validasi atau proses registrasi.
     *
     * @param type    Tipe alert (INFORMATION, ERROR, dll.)
     * @param title   Judul dari alert
     * @param message Pesan utama dari alert
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Tidak menampilkan header tambahan
        alert.setContentText(message);
        alert.showAndWait(); // Menunggu pengguna menutup alert
    }
}