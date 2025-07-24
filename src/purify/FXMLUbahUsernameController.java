package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller untuk mengatur logika perubahan username pengguna.
 * Digunakan dalam tampilan Ubah Username (FXMLUbahUsername.fxml).
 */
public class FXMLUbahUsernameController {

    /**
     * Field input untuk username baru.
     */
    @FXML
    private TextField usernameField;

    /**
     * Tombol untuk membatalkan perubahan dan menutup jendela.
     */
    @FXML
    private Button btnBatal;

    /**
     * Tombol untuk menyimpan perubahan username.
     */
    @FXML
    private Button btnSimpan;

    /**
     * Handler saat tombol Simpan ditekan.
     * Melakukan validasi panjang username, kemudian mencoba menyimpan perubahan
     * melalui ManajemenPengguna.
     *
     * @param event Event aksi dari tombol Simpan.
     */
    @FXML
    private void handleSimpan(ActionEvent event) {
        // Ambil teks dari field dan hapus spasi di depan/belakang
        String newUsername = usernameField.getText().trim();

        // Validasi panjang minimal username
        if (newUsername.isEmpty() || newUsername.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Username baru minimal harus 4 karakter.");
            return;
        }

        // Ambil instance dari ManajemenPengguna dan ubah username
        ManajemenPengguna mu = ManajemenPengguna.getInstance();
        boolean isSuccess = mu.ubahUsername(newUsername); // Memanggil metode ubah username

        // Tampilkan notifikasi berdasarkan hasil
        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Username berhasil diubah!");
            closeWindow(); // Tutup jendela jika sukses
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal",
                    "Username '" + newUsername + "' sudah digunakan atau terjadi kesalahan.");
        }
    }

    /**
     * Handler saat tombol Batal ditekan.
     * Menutup jendela tanpa menyimpan perubahan.
     *
     * @param event Event aksi dari tombol Batal.
     */
    @FXML
    private void handleBatal(ActionEvent event) {
        closeWindow();
    }

    /**
     * Menutup jendela saat ini.
     */
    private void closeWindow() {
        Stage stage = (Stage) btnBatal.getScene().getWindow();
        stage.close();
    }

    /**
     * Menampilkan dialog notifikasi menggunakan JavaFX Alert.
     *
     * @param type    Tipe alert (ERROR, INFORMATION, dll).
     * @param title   Judul alert.
     * @param message Isi pesan yang akan ditampilkan.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Tidak ada header
        alert.setContentText(message);
        alert.showAndWait(); // Tampilkan dan tunggu respon user
    }
}
