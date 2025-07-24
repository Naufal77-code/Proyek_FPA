package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller untuk menangani tampilan FXMLBantuan.fxml, yang biasanya berisi
 * informasi bantuan atau panduan penggunaan aplikasi.
 */
public class FXMLBantuanController {

    // === Komponen UI ===

    @FXML
    private Button btnTutup;

    /**
     * Tombol untuk menutup jendela bantuan. Dikaitkan langsung dari file FXML.
     */

    // === Method ===

    @FXML
    private void handleTutup(ActionEvent event) {
        /**
         * Method ini dipanggil saat tombol "Tutup" diklik.
         * Tujuannya adalah untuk menutup jendela bantuan.
         * 
         * Langkah-langkah:
         * 1. Mengambil objek Stage (jendela) dari tombol yang sedang aktif.
         * 2. Memanggil `close()` untuk menutup jendela tersebut.
         */
        Stage stage = (Stage) btnTutup.getScene().getWindow(); // Ambil stage dari tombol
        stage.close(); // Tutup jendela
    }
}
