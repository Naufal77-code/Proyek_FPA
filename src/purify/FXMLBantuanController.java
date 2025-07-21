package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class FXMLBantuanController {

    @FXML
    private Button btnTutup;

    @FXML
    private void handleTutup(ActionEvent event) {
        Stage stage = (Stage) btnTutup.getScene().getWindow();
        stage.close();
    }
}