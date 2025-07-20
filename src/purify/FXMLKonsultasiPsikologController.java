package purify; 
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLKonsultasiPsikologController implements Initializable {

    @FXML
    private TableView<Psikolog> psikologTable;
    @FXML
    private TableColumn<Psikolog, String> colNama;
    @FXML
    private TableColumn<Psikolog, String> colSpesialisasi;
    @FXML
    private TableColumn<Psikolog, String> colStatus;

    @FXML
    private Button btnMulaiChat;
    @FXML
    private Button btnJadwalkan;
    @FXML
    private Button btnLihatRiwayatKonsultasi; 
    @FXML
    private Button btnKembali;

    private PsikologList psikologList; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colSpesialisasi.setCellValueFactory(new PropertyValueFactory<>("spesialisasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        psikologList = new PsikologList();
        psikologList.initializeDefaultPsikolog(); 

        psikologTable.setItems(psikologList.getData());

        btnMulaiChat.setOnAction(this::handleMulaiChat);
        btnJadwalkan.setOnAction(this::handleJadwalkanKonsultasi);
        btnLihatRiwayatKonsultasi.setOnAction(this::handleLihatRiwayatKonsultasi); 
        btnKembali.setOnAction(this::handleKembali);

        btnMulaiChat.setDisable(true);
        btnJadwalkan.setDisable(true);

        psikologTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                btnMulaiChat.setDisable(false);
                btnJadwalkan.setDisable(false);
            } else {
                btnMulaiChat.setDisable(true);
                btnJadwalkan.setDisable(true);
            }
        });
    }

    @FXML
    private void handleMulaiChat(ActionEvent event) {
        Psikolog selectedPsikolog = psikologTable.getSelectionModel().getSelectedItem();
        if (selectedPsikolog == null) {
            showAlert("Peringatan", "Pilih psikolog terlebih dahulu untuk memulai chat.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLChatWindow.fxml"));
            Parent root = loader.load();

            FXMLChatWindowController chatController = loader.getController();
            chatController.setSelectedPsikolog(selectedPsikolog);
            chatController.setMainController(this); 

            Stage currentStage = (Stage) btnMulaiChat.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Chat dengan " + selectedPsikolog.getNama());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka jendela chat: " + e.getMessage());
        }
    }

    @FXML
    private void handleJadwalkanKonsultasi(ActionEvent event) {
        Psikolog selectedPsikolog = psikologTable.getSelectionModel().getSelectedItem();
        if (selectedPsikolog == null) {
            showAlert("Peringatan", "Pilih psikolog terlebih dahulu untuk menjadwalkan konsultasi.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLJadwalKonsultasi.fxml"));
            Parent root = loader.load();

            FXMLJadwalKonsultasiController jadwalController = loader.getController();
            jadwalController.setSelectedPsikolog(selectedPsikolog);
            jadwalController.setMainController(this);

            Stage currentStage = (Stage) btnJadwalkan.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Jadwalkan Konsultasi dengan " + selectedPsikolog.getNama());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka jendela jadwal konsultasi: " + e.getMessage());
        }
    }

    @FXML
    private void handleLihatRiwayatKonsultasi(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLRiwayatKonsultasi.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) btnLihatRiwayatKonsultasi.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Digital Detox - Riwayat Konsultasi"); 

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman riwayat konsultasi: " + e.getMessage());
        }
    }

  @FXML
private void handleKembali(ActionEvent event) {
    Stage currentStage = (Stage) btnKembali.getScene().getWindow();

    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLMainMenu.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Purify - Digital Detox");
        stage.show();
        currentStage.close();
    } catch (IOException e) {
        showAlert("Error", "Gagal kembali ke main menu: " + e.getMessage());
    }
}


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void refreshPsikologTable() {
        psikologList.saveToXML(); 
        psikologTable.setItems(psikologList.getData());
        psikologTable.refresh();
    }
}