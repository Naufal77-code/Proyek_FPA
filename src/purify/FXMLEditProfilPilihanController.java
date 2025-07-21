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

    @FXML private Button btnUbahUsername;
    @FXML private Button btnUbahSandi;
    @FXML private Button btnBatal;

    @FXML
    private void handleUbahUsername(ActionEvent event) {
        // Buka jendela "Ubah Username" di atas jendela ini
        openSubWindow(event, "FXMLUbahUsername.fxml", "Ubah Username");
    }

    @FXML
    private void handleUbahSandi(ActionEvent event) {
        // Buka jendela "Ubah Sandi" di atas jendela ini
        openSubWindow(event, "FXMLUbahSandi.fxml", "Ubah Sandi");
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        // Cukup tutup jendela saat ini
        closeCurrentWindow(event);
    }

    private void openSubWindow(ActionEvent event, String fxmlFile, String title) {
        try {
            // Dapatkan jendela "Pilih Opsi Edit" saat ini untuk dijadikan pemilik
            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Muat FXML untuk jendela baru
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Siapkan Stage untuk jendela baru
            Stage newStage = new Stage();
            newStage.setTitle(title);
            newStage.initModality(Modality.APPLICATION_MODAL);
            
            // Atur pemiliknya ke jendela "Pilih Opsi Edit"
            newStage.initOwner(ownerStage);
            
            newStage.setScene(new Scene(root));
            
            // Tampilkan jendela baru dan tunggu sampai pengguna menutupnya
            // Jendela "Pilih Opsi Edit" akan otomatis tidak bisa diklik selama ini
            newStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeCurrentWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}