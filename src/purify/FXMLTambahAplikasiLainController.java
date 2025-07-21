package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLTambahAplikasiLainController {

    @FXML private TextField namaAplikasiField;
    @FXML private Button btnTambah;
    @FXML private Button btnBatal;

    private Stage stage;
    private String namaAplikasi;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getNamaAplikasi() {
        return namaAplikasi;
    }

    @FXML
    private void handleTambah(ActionEvent event) {
        namaAplikasi = namaAplikasiField.getText().trim();
        if (stage != null) {
            stage.close();
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        namaAplikasi = null; // Pastikan tidak ada nama yang dikembalikan
        if (stage != null) {
            stage.close();
        }
    }
}