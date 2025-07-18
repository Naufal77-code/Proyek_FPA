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

    @FXML
    private BarChart<String, Number> statistikChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private TableView<RiwayatBlokirApps> riwayatTable;

    @FXML
    private TableColumn<RiwayatBlokirApps, Integer> colNo;

    @FXML
    private TableColumn<RiwayatBlokirApps, String> colTanggal;

    @FXML
    private TableColumn<RiwayatBlokirApps, Integer> colDurasi;

    @FXML
    private TableColumn<RiwayatBlokirApps, String> colStatus;

    @FXML
    private TableColumn<RiwayatBlokirApps, String> colAktivitas;

    @FXML
    private TableColumn<RiwayatBlokirApps, String> colApps;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnKembali;

    private RiwayatBlokirAppsList riwayatList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xAxis.setLabel("Aplikasi");
        yAxis.setLabel("Total Durasi Blokir (menit)");

        colNo.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colDurasi.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
        colApps.setCellValueFactory(new PropertyValueFactory<>("appsBlokir"));

        btnRefresh.setOnAction(this::handleRefresh);
        btnKembali.setOnAction(this::handleKembali);

    }

    public void setRiwayatList(RiwayatBlokirAppsList riwayatList) {
        this.riwayatList = riwayatList;
        riwayatTable.setItems(riwayatList.getData());
        updateStatistics();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        if (riwayatList == null) {
            showAlert("Kesalahan", "Data riwayat belum tersedia.");
            return;
        }

        riwayatList.loadFromXML();
        riwayatTable.setItems(riwayatList.getData());
        riwayatTable.refresh();
        updateStatistics();
        showAlert("Informasi", "Data berhasil diperbarui!");
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
        confirmation.setContentText("Apakah Anda yakin ingin menghapus riwayat yang dipilih?");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            riwayatList.getData().remove(selected);
            riwayatList.saveToXML(); // Pastikan data diperbarui di XML
            riwayatTable.refresh();
            updateStatistics();
            showAlert("Informasi", "Riwayat yang dipilih berhasil dihapus.");
        }
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        Stage stage = (Stage) btnKembali.getScene().getWindow();
        stage.close();
    }

    private void updateStatistics() {
        if (riwayatList == null)
            return;

        ObservableList<RiwayatBlokirApps> data = riwayatList.getData();
        Map<String, Integer> appDurations = new HashMap<>();

        for (RiwayatBlokirApps riwayat : data) {
            String[] apps = (riwayat.getAppsBlokir() != null && !riwayat.getAppsBlokir().trim().isEmpty())
                    ? riwayat.getAppsBlokir().split(", ")
                    : new String[] { "Tidak ada aplikasi" };
            for (String app : apps) {
                appDurations.put(app, appDurations.getOrDefault(app, 0) + riwayat.getDurasi());
            }
        }

        List<Map.Entry<String, Integer>> sortedApps = appDurations.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        statistikChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Durasi Blokir");

        for (Map.Entry<String, Integer> entry : sortedApps) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        statistikChart.getData().add(series);
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
