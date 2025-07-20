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

public class FXMLRiwayatKonsultasiController implements Initializable {

    @FXML
    private TableView<ChatRecord> chatHistoryTable;
    @FXML
    private TableColumn<ChatRecord, String> colChatPsikologNama;
    @FXML
    private TableColumn<ChatRecord, String> colChatWaktuMulai;
    @FXML
    private TableColumn<ChatRecord, String> colChatRingkasan;

    @FXML
    private TableView<Appointment> appointmentTable;
    @FXML
    private TableColumn<Appointment, String> colJadwalPsikologNama;
    @FXML
    private TableColumn<Appointment, String> colJadwalTanggal;
    @FXML
    private TableColumn<Appointment, String> colJadwalWaktu;
    @FXML
    private TableColumn<Appointment, String> colJadwalLokasi;
    @FXML
    private TableColumn<Appointment, String> colJadwalStatus;

    @FXML
    private Button btnKembali;

    private ChatHistoryList chatHistoryList;
    private AppointmentList appointmentList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatHistoryList = new ChatHistoryList();
        appointmentList = new AppointmentList();

        colChatPsikologNama.setCellValueFactory(new PropertyValueFactory<>("psikologNama"));
        colChatWaktuMulai.setCellValueFactory(new PropertyValueFactory<>("waktuMulaiChat"));
        colChatRingkasan.setCellValueFactory(new PropertyValueFactory<>("ringkasanChat"));
        chatHistoryTable.setItems(chatHistoryList.getData());

        colJadwalPsikologNama.setCellValueFactory(new PropertyValueFactory<>("psikologNama"));
        colJadwalTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJadwalWaktu.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        colJadwalLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colJadwalStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        appointmentTable.setItems(appointmentList.getData());

        btnKembali.setOnAction(this::handleKembali);
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLKonsultasiPsikolog.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) btnKembali.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Konsultasi dengan Psikolog"); 

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

