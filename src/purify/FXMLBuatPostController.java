package purify;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLBuatPostController implements Initializable {

    // Menampilkan gambar avatar pengguna (placeholder jika tidak ada avatar asli)
    @FXML
    private ImageView avatarImageView;

    // Area input teks untuk menulis isi postingan komunitas
    @FXML
    private TextArea isiPostArea;

    // Tombol untuk mengirimkan postingan
    @FXML
    private Button btnKirim;

    // Referensi ke controller utama komunitas, agar bisa memanggil refresh setelah
    // posting
    private FXMLKomunitasController komunitasController;

    /**
     * Method `initialize` dipanggil otomatis saat FXML dimuat.
     * Di sini kita mengatur gambar avatar default saat tampilan pertama kali
     * muncul.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Mengambil dan menampilkan avatar placeholder
            Image avatar = new Image(getClass().getResourceAsStream("/icons/avatar_placeholder.png"));
            avatarImageView.setImage(avatar);
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar avatar di popup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setter untuk menerima referensi controller komunitas utama.
     * Digunakan agar bisa memanggil fungsi refresh dari sana.
     */
    public void setKomunitasController(FXMLKomunitasController controller) {
        this.komunitasController = controller;
    }

    /**
     * Menangani logika saat tombol "Kirim" ditekan:
     * - Validasi isi postingan
     * - Ambil nama penulis dari pengguna yang sedang login
     * - Simpan postingan ke `KomunitasDataList`
     * - Refresh tampilan post komunitas
     * - Tutup jendela popup
     */
    @FXML
    private void handleKirim() {
        String isi = isiPostArea.getText().trim();

        // Validasi agar postingan tidak kosong
        if (isi.isEmpty()) {
            showAlert("Error", "Postingan tidak boleh kosong.");
            return;
        }

        // Ambil pengguna yang sedang login
        Pengguna currentUser = ManajemenPengguna.getInstance().getCurrentUser();
        String penulis;

        // Jika user ada, gunakan namanya. Jika tidak, fallback ke "Anonim"
        if (currentUser != null) {
            penulis = currentUser.getNama();
        } else {
            penulis = "Anonim"; // fallback defensif, idealnya tidak terjadi
        }

        // Tambahkan posting ke data list komunitas
        KomunitasDataList.getInstance().tambahPost(isi, penulis);

        // Refresh tampilan post di halaman komunitas
        if (komunitasController != null) {
            komunitasController.refreshPosts();
        }

        // Tutup jendela ini
        Stage stage = (Stage) btnKirim.getScene().getWindow();
        stage.close();
    }

    /**
     * Menangani logika saat tombol "Batal" ditekan.
     * Hanya menutup jendela popup tanpa menyimpan apapun.
     */
    @FXML
    private void handleBatal() {
        Stage stage = (Stage) btnKirim.getScene().getWindow();
        stage.close();
    }

    /**
     * Menampilkan popup alert informasi.
     * Digunakan untuk validasi dan feedback kepada pengguna.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
