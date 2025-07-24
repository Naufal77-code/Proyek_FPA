package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class FXMLEditProfilPilihanController {

    // Tombol untuk membuka jendela perubahan username
    @FXML
    private Button btnUbahUsername;

    // Tombol untuk membuka jendela perubahan sandi
    @FXML
    private Button btnUbahSandi;

    // Tombol untuk membatalkan dan menutup jendela saat ini
    @FXML
    private Button btnBatal;

    /**
     * Method ini dipanggil saat tombol "Ubah Username" diklik.
     * Ia akan membuka jendela baru yang berisi form untuk mengubah username.
     */
    @FXML
    private void handleUbahUsername(ActionEvent event) {
        openSubWindow(event, "FXMLUbahUsername.fxml", "Ubah Username");
    }

    /**
     * Method ini dipanggil saat tombol "Ubah Sandi" diklik.
     * Ia akan membuka jendela baru yang berisi form untuk mengubah sandi.
     */
    @FXML
    private void handleUbahSandi(ActionEvent event) {
        openSubWindow(event, "FXMLUbahSandi.fxml", "Ubah Sandi");
    }

    /**
     * Method ini dipanggil saat tombol "Batal" diklik.
     * Ia akan menutup jendela pilihan edit saat ini.
     */
    @FXML
    private void handleBatal(ActionEvent event) {
        closeCurrentWindow(event);
    }

    /**
     * Method utilitas untuk membuka jendela baru berdasarkan file FXML yang
     * diberikan.
     * Jendela ini akan bersifat modal, artinya pengguna tidak bisa kembali ke
     * jendela sebelumnya
     * sampai jendela baru ditutup.
     *
     * @param event    Event klik tombol, digunakan untuk mengetahui jendela asal.
     * @param fxmlFile Nama file FXML yang akan dimuat.
     * @param title    Judul dari jendela baru.
     */
    private void openSubWindow(ActionEvent event, String fxmlFile, String title) {
        try {
            // Ambil stage (jendela) dari event sumber
            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Muat isi FXML dari file yang ditentukan
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Buat jendela baru dan atur sebagai modal
            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(ownerStage);
            newStage.setScene(new Scene(root));

            // Tampilkan jendela baru dan hentikan interaksi dengan jendela utama hingga
            // ditutup
            newStage.showAndWait();

        } catch (IOException e) {
            // Tangani jika file FXML tidak ditemukan atau tidak dapat dimuat
            e.printStackTrace();
        }
    }

    /**
     * Method utilitas untuk menutup jendela saat ini.
     *
     * @param event Event tombol yang digunakan untuk menutup jendela terkait.
     */
    private void closeCurrentWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
