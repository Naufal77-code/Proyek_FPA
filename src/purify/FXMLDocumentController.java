package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

    @FXML
    private TextField durasiField;

    @FXML
    private TextField kodeDaruratField;

    @FXML
    private TextField aktivitasField;

    @FXML
    private Button btnBlokir;

    @FXML
    private Button btnEditRiwayat;

    @FXML
    private Button btnHapusRiwayat;

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

    private static RiwayatBlokirList riwayatList = new RiwayatBlokirList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colRiwayat.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colRiwayat1.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colRiwayat11.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colRiwayat111.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRiwayat2.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));

        riwayatTable.setItems(riwayatList.getData());

        btnEditRiwayat.setOnAction(this::handleEditRiwayat);
        btnHapusRiwayat.setOnAction(this::handleHapusRiwayat);

        
    }

    @FXML
    private void handleBlokir(ActionEvent event) {
        String durasiText = durasiField.getText().trim();
        String kodeDarurat = kodeDaruratField.getText().trim();
        String aktivitas = aktivitasField.getText().trim();

        if (durasiText.isEmpty()) {
            showAlert("Error", "Durasi tidak boleh kosong!");
            return;
        }

        try {
            int durasi = Integer.parseInt(durasiText);
            if (durasi <= 0) {
                showAlert("Error", "Durasi harus lebih dari 0!");
                return;
            }

            if (aktivitas.isEmpty()) {
                aktivitas = "Aktivitas tidak ada";
            }

            if (kodeDarurat.isEmpty()) {
                showAlert("Error", "Kode darurat tidak boleh kosong!");
                return;
            }

            DetoxSession.getInstance().startDetox(durasi, aktivitas, kodeDarurat);


            openBlockingScreen();

        } catch (NumberFormatException e) {
            showAlert("Error", "Durasi harus berupa angka!");
        }
    }

    private void handleEditRiwayat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLEditRiwayat.fxml"));
            Parent root = loader.load();

            FXMLEditRiwayatController controller = loader.getController();
            controller.setRiwayatList(riwayatList);
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Edit Riwayat Aktivitas");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman edit riwayat!");
        }
    }

    private void handleHapusRiwayat(ActionEvent event) {
        if (riwayatList.getData().isEmpty()) {
            showAlert("Info", "Tidak ada riwayat untuk dihapus!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi");
        confirm.setHeaderText("Hapus Riwayat Terlama");
        confirm.setContentText("Apakah Anda yakin ingin menghapus riwayat terlama?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            riwayatList.removeOldest();
            showAlert("Info", "Riwayat terlama berhasil dihapus!");
        }
    }

    private void openBlockingScreen() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirStatus.fxml")); 
        Parent root = loader.load();

        FXMLBlokirStatusController controller = loader.getController();
        controller.setMainController(this);

        Stage currentStage = (Stage) btnBlokir.getScene().getWindow();
        currentStage.setScene(new Scene(root));

    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Gagal membuka halaman blokir!");
    }
}

    public void addToRiwayat(String status) {
        DetoxSession session = DetoxSession.getInstance();
        int nomor = riwayatList.getData().size() + 1;
        
        riwayatList.setData(
            nomor,
            session.getFormattedWaktuMulai(),
            session.getDurasi(),
            status,
            session.getAktivitas()
        );

        durasiField.clear();
        kodeDaruratField.clear();
        aktivitasField.clear();
    }

    public void refreshTable() {
        riwayatTable.refresh();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}