package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller untuk menangani penambahan aplikasi baru secara manual.
 * Biasanya dipakai saat pengguna ingin menambahkan aplikasi yang tidak
 * terdeteksi otomatis.
 */
public class FXMLTambahAplikasiLainController {

    /**
     * Input field untuk nama aplikasi yang akan ditambahkan.
     */
    @FXML
    private TextField namaAplikasiField;

    /**
     * Tombol untuk mengonfirmasi penambahan aplikasi.
     */
    @FXML
    private Button btnTambah;

    /**
     * Tombol untuk membatalkan proses penambahan aplikasi.
     */
    @FXML
    private Button btnBatal;

    /**
     * Referensi ke Stage jendela saat ini, agar dapat ditutup setelah proses
     * selesai.
     */
    private Stage stage;

    /**
     * Variabel yang menyimpan nama aplikasi hasil input pengguna.
     */
    private String namaAplikasi;

    /**
     * Setter untuk menyimpan objek Stage eksternal ke dalam controller.
     * Biasanya dipanggil dari kelas pemanggil sebelum window ditampilkan.
     *
     * @param stage Stage yang sedang ditampilkan.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Getter untuk mengambil nama aplikasi setelah jendela ditutup.
     * Nilainya akan null jika pengguna membatalkan.
     *
     * @return Nama aplikasi yang dimasukkan pengguna.
     */
    public String getNamaAplikasi() {
        return namaAplikasi;
    }

    /**
     * Handler untuk tombol "Tambah".
     * Menyimpan nama aplikasi yang dimasukkan lalu menutup jendela.
     *
     * @param event Event tombol Tambah.
     */
    @FXML
    private void handleTambah(ActionEvent event) {
        namaAplikasi = namaAplikasiField.getText().trim(); // Simpan nama aplikasi dari input
        if (stage != null) {
            stage.close(); // Tutup jendela setelah input disimpan
        }
    }

    /**
     * Handler untuk tombol "Batal".
     * Mengatur namaAplikasi menjadi null agar tidak ada data dikembalikan, lalu
     * menutup jendela.
     *
     * @param event Event tombol Batal.
     */
    @FXML
    private void handleBatal(ActionEvent event) {
        namaAplikasi = null; // Pastikan tidak menyimpan input jika dibatalkan
        if (stage != null) {
            stage.close(); // Tutup jendela
        }
    }
}
