package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * Controller untuk menangani logika perubahan sandi pengguna.
 * Digunakan oleh antarmuka FXMLUbahSandi.fxml.
 */
public class FXMLUbahSandiController {

    /**
     * Input field untuk sandi lama pengguna.
     */
    @FXML
    private PasswordField sandiLamaField;

    /**
     * Input field untuk sandi baru pengguna.
     */
    @FXML
    private PasswordField sandiBaruField;

    /**
     * Input field untuk konfirmasi sandi baru pengguna.
     */
    @FXML
    private PasswordField konfirmasiSandiField;

    /**
     * Tombol untuk membatalkan proses perubahan sandi.
     */
    @FXML
    private Button btnBatal;

    /**
     * Tombol untuk menyimpan perubahan sandi.
     */
    @FXML
    private Button btnSimpan;

    /**
     * Handler saat tombol Simpan diklik.
     * Melakukan validasi kecocokan sandi baru dan konfirmasi,
     * lalu memanggil metode `ubahSandi` dari `ManajemenPengguna`.
     *
     * @param event Event dari tombol Simpan.
     */
    @FXML
    private void handleSimpan(ActionEvent event) {
        String sandiLama = sandiLamaField.getText();
        String sandiBaru = sandiBaruField.getText();
        String konfirmasiSandi = konfirmasiSandiField.getText();

        // Validasi: sandi baru tidak boleh kosong dan harus sama dengan konfirmasi
        if (sandiBaru.isEmpty() || !sandiBaru.equals(konfirmasiSandi)) {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Sandi baru dan konfirmasi tidak cocok.");
            return;
        }

        // Ambil instance ManajemenPengguna dan coba ubah sandi
        ManajemenPengguna mu = ManajemenPengguna.getInstance();
        boolean isSuccess = mu.ubahSandi(sandiLama, sandiBaru);

        // Tampilkan hasil
        if (isSuccess) {
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Sandi berhasil diubah!");
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Gagal", "Sandi lama salah atau terjadi kesalahan.");
        }
    }

    /**
     * Handler saat tombol Batal diklik.
     * Menutup jendela tanpa menyimpan perubahan.
     *
     * @param event Event dari tombol Batal.
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
     * Menampilkan notifikasi (alert) kepada pengguna.
     *
     * @param type    Tipe alert (INFORMATION, ERROR, dll.)
     * @param title   Judul alert.
     * @param message Isi pesan alert.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // Tidak ada header
        alert.setContentText(message);
        alert.showAndWait(); // Tampilkan dialog
    }
}
