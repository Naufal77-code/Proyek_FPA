package purify;

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

    @FXML
    private Button btnHapus;

    private RiwayatBlokirList riwayatList;
    private FXMLDocumentController mainController;
    private RiwayatBlokir selectedRiwayat;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colRiwayat.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colRiwayat1.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colRiwayat11.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colRiwayat111.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRiwayat2.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));

        btnSubmit.setOnAction(this::handleSubmit);
        btnHapus.setOnAction(this::handleHapus);
        
        // Add table selection listener
        riwayatTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedRiwayat = newValue;
                // Auto-fill the fields when a row is selected
                nomorField.setText(String.valueOf(newValue.getNomor()));
                aktivitasBaruField.setText(newValue.getAktivitas());
                
                // Enable buttons when row is selected
                btnSubmit.setDisable(false);
                btnHapus.setDisable(false);
            } else {
                // Disable buttons when no row is selected
                btnSubmit.setDisable(true);
                btnHapus.setDisable(true);
            }
        });
        
        // Make the nomor field read-only since it's auto-filled
        nomorField.setEditable(false);
        nomorField.setStyle("-fx-background-color: #f0f0f0;");
        
        // Initially disable buttons until a row is selected
        btnSubmit.setDisable(true);
        btnHapus.setDisable(true);
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        if (selectedRiwayat == null) {
            showAlert("Error", "Pilih salah satu riwayat dari tabel terlebih dahulu!");
            return;
        }
        
        String aktivitasBaru = aktivitasBaruField.getText().trim();
        int nomor = selectedRiwayat.getNomor();

        if (riwayatList != null) {
            riwayatList.editAktivitas(nomor, aktivitasBaru);
        }
        
        if (mainController != null) {
            mainController.refreshTable();
        }

        showAlert("Success", "Aktivitas berhasil diubah!");
        
        // Clear selection and fields
        clearSelection();
        
        // Don't close the window so user can continue editing other records
        // Stage stage = (Stage) btnSubmit.getScene().getWindow();
        // stage.close();
    }

    @FXML
    private void handleHapus(ActionEvent event) {
        if (selectedRiwayat == null) {
            showAlert("Error", "Pilih salah satu riwayat dari tabel terlebih dahulu!");
            return;
        }
        
        // Show confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Riwayat");
        confirm.setContentText("Apakah Anda yakin ingin menghapus riwayat nomor " + selectedRiwayat.getNomor() + "?");
        
        // Wait for user response
        if (confirm.showAndWait().orElse(null) == javafx.scene.control.ButtonType.OK) {
            int nomor = selectedRiwayat.getNomor();
            
            if (riwayatList != null) {
                riwayatList.remove(nomor);
            }
            
            if (mainController != null) {
                mainController.refreshTable();
            }
            
            showAlert("Success", "Riwayat berhasil dihapus!");
            
            // Clear selection and fields
            clearSelection();
        }
    }

    private void clearSelection() {
        riwayatTable.getSelectionModel().clearSelection();
        selectedRiwayat = null;
        nomorField.clear();
        aktivitasBaruField.clear();
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