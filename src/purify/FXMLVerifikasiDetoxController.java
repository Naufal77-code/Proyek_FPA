package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller untuk tampilan verifikasi detox.
 * Bertugas menangani interaksi pengguna dengan jendela verifikasi,
 * seperti menutup jendela saat tombol diklik.
 */
public class FXMLVerifikasiDetoxController {

    /**
     * Tombol untuk menutup jendela verifikasi detox.
     * Diinisialisasi secara otomatis melalui FXML.
     */
    @FXML
    private Button btnTutup;

    /**
     * Method event handler saat tombol "Tutup" ditekan.
     * Fungsinya adalah menutup jendela saat ini.
     *
     * @param event Event yang dipicu saat tombol ditekan.
     */
    @FXML
    private void handleTutup(ActionEvent event) {
        // Mendapatkan stage (jendela) dari tombol dan menutupnya
        Stage stage = (Stage) btnTutup.getScene().getWindow();
        stage.close();
    }
}
