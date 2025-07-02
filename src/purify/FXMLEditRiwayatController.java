package tes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLEditRiwayatController implements Initializable {

    @FXML
    private TableView<RiwayatBlokir> riwayatTable;

    @FXML
    private TableColumn<RiwayatBlokir, Integer> colRiwayat;

    @FXML
    private TableColumn<RiwayatBlokir, String> colRiwayat1;

    @FXML
    private TableColumn<RiwayatBlokir, Integer> colRiwayat11;

    @FXML
    private TableColumn<RiwayatBlokir, String> colRiwayat111;

    @FXML
    private TableColumn<RiwayatBlokir, String> colRiwayat2;

    @FXML
    private TextField nomorField;

    @FXML
    private TextField aktivitasBaruField;

    @FXML
    private Button btnSubmit;

    private RiwayatBlokirList riwayatList;
    private FXMLDocumentController mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colRiwayat.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colRiwayat1.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colRiwayat11.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colRiwayat111.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRiwayat2.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));

        btnSubmit.setOnAction(this::handleSubmit);
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        String nomorText = nomorField.getText().trim();
        String aktivitasBaru = aktivitasBaruField.getText().trim();

        if (nomorText.isEmpty()) {
            showAlert("Error", "Nomor riwayat tidak boleh kosong!");
            return;
        }

        try {
            int nomor = Integer.parseInt(nomorText);
            
            if (nomor <= 0 || nomor > riwayatList.getData().size()) {
                showAlert("Error", "Nomor riwayat tidak valid!");
                return;
            }

            if (riwayatList != null) {
                riwayatList.editAktivitas(nomor, aktivitasBaru);
}
            
            if (mainController != null) {
                mainController.refreshTable();
            }

            showAlert("Success", "Aktivitas berhasil diubah!");
            
            Stage stage = (Stage) btnSubmit.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            showAlert("Error", "Nomor harus berupa angka!");
        }
    }

    public void setRiwayatList(RiwayatBlokirList riwayatList) {
        this.riwayatList = riwayatList;
        if (riwayatTable != null) {
            riwayatTable.setItems(riwayatList.getData());
        }
    }

    public void setMainController(FXMLDocumentController controller) {
        this.mainController = controller;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}