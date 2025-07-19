package purify;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class FXMLStatistikAppsController implements Initializable {

    @FXML private BarChart<String, Number> statistikChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private TableView<RiwayatBlokirApps> riwayatTable;
    @FXML private TableColumn<RiwayatBlokirApps, Integer> colNo;
    @FXML private TableColumn<RiwayatBlokirApps, String> colTanggal;
    @FXML private TableColumn<RiwayatBlokirApps, Integer> colDurasi;
    @FXML private TableColumn<RiwayatBlokirApps, String> colStatus;
    @FXML private TableColumn<RiwayatBlokirApps, String> colAktivitas;
    @FXML private TableColumn<RiwayatBlokirApps, String> colApps;
    @FXML private Button btnHapus;
    @FXML private Button btnRefresh;
    @FXML private Button btnKembali;

    private Stage previousStage;
    private static RiwayatBlokirAppsList riwayatList = RiwayatBlokirAppsList.getInstance();
@Override
public void initialize(URL url, ResourceBundle rb) {
    setupTableColumns();
    setupButtonActions();

    // Pastikan riwayatList diinisialisasi
    if (riwayatList == null) {
        riwayatList = RiwayatBlokirAppsList.getInstance();
    }
    
    // Langsung muat data dari XML dan refresh tampilan
    riwayatList.loadFromXML();
    refreshData();
}

    private void setupTableColumns() {
        colNo.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colDurasi.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
        colApps.setCellValueFactory(new PropertyValueFactory<>("appsBlokir"));
    }

    private void setupButtonActions() {
        btnHapus.setOnAction(this::handleHapusRiwayat);
        btnRefresh.setOnAction(this::handleRefresh);
        btnKembali.setOnAction(this::handleKembali);
    }

    public void setRiwayatList(RiwayatBlokirAppsList riwayatList) {
    this.riwayatList = riwayatList;
    refreshData(); // Langsung refresh data saat di-set
}


    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }

    private void refreshData() {
        if (riwayatList == null) return;

        ObservableList<RiwayatBlokirApps> data = riwayatList.getData();
        riwayatTable.setItems(data);

        updateStatistics();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        boolean loaded = riwayatList.loadFromXML();
        if (loaded) {
            showAlert("Informasi", "Data berhasil diperbarui!");
        } else {
            showAlert("Kesalahan", "Gagal memuat data dari file.");
        }
        refreshData();
    }

    @FXML
    private void handleHapusRiwayat(ActionEvent event) {
        RiwayatBlokirApps selected = riwayatTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert("Informasi", "Silakan pilih baris riwayat yang ingin dihapus.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Konfirmasi Hapus");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Apakah Anda yakin ingin menghapus riwayat ini?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            riwayatList.getData().remove(selected);
            riwayatList.saveToXML();
            refreshData();
            showAlert("Informasi", "Riwayat berhasil dihapus.");
        }
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        if (previousStage != null) {
            previousStage.show();
            Stage currentStage = (Stage) btnKembali.getScene().getWindow();
            currentStage.close();
        }
    }

    private void updateStatistics() {
        statistikChart.getData().clear();

        if (riwayatList == null || riwayatList.getData().isEmpty()) {
            return;
        }

        Map<String, Integer> appDurations = new HashMap<>();

        for (RiwayatBlokirApps riwayat : riwayatList.getData()) {
            if (riwayat.getAppsBlokir() != null) {
                String[] apps = riwayat.getAppsBlokir().split(", ");
                for (String app : apps) {
                    appDurations.merge(app, riwayat.getDurasi(), Integer::sum);
                }
            }
        }

        if (appDurations.isEmpty()) return;

        List<Map.Entry<String, Integer>> sortedApps = appDurations.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Durasi Blokir (menit)");

        for (Map.Entry<String, Integer> entry : sortedApps) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        statistikChart.getData().add(series);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
