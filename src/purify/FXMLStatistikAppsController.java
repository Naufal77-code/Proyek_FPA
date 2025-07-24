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

    // Grafik batang untuk menampilkan statistik durasi blokir per aplikasi
    @FXML
    private BarChart<String, Number> statistikChart;

    // Sumbu X untuk nama aplikasi
    @FXML
    private CategoryAxis xAxis;

    // Sumbu Y untuk total durasi blokir (dalam menit)
    @FXML
    private NumberAxis yAxis;

    // Tabel riwayat blokir aplikasi
    @FXML
    private TableView<RiwayatBlokirApps> riwayatTable;

    // Kolom nomor urut
    @FXML
    private TableColumn<RiwayatBlokirApps, Integer> colNo;

    // Kolom tanggal blokir
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colTanggal;

    // Kolom durasi blokir dalam menit
    @FXML
    private TableColumn<RiwayatBlokirApps, Integer> colDurasi;

    // Kolom status sesi blokir
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colStatus;

    // Kolom aktivitas yang diblokir
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colAktivitas;

    // Kolom aplikasi yang diblokir
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colApps;

    // Tombol untuk menghapus riwayat yang dipilih
    @FXML
    private Button btnHapus;

    // Tombol untuk memuat ulang data dari file XML
    @FXML
    private Button btnRefresh;

    // Tombol untuk kembali ke tampilan sebelumnya
    @FXML
    private Button btnKembali;

    // Stage tampilan sebelumnya, digunakan saat tombol kembali ditekan
    private Stage previousStage;

    // Daftar riwayat blokir aplikasi, singleton
    private static RiwayatBlokirAppsList riwayatList = RiwayatBlokirAppsList.getInstance();

    // Inisialisasi saat controller dimuat
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns(); // Menyiapkan kolom tabel
        setupButtonActions(); // Menetapkan aksi tombol
        if (riwayatList == null) {
            riwayatList = RiwayatBlokirAppsList.getInstance();
        }
        riwayatList.loadFromXML(); // Memuat data dari file XML
        refreshData(); // Menampilkan data
    }

    // Menentukan bagaimana nilai tiap kolom diambil dari objek RiwayatBlokirApps
    private void setupTableColumns() {
        colNo.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colDurasi.setCellValueFactory(cellData -> {
            int durasiDetik = cellData.getValue().getDurasi();
            int durasiMenit = (int) Math.ceil(durasiDetik / 60.0);
            return new javafx.beans.property.SimpleIntegerProperty(durasiMenit).asObject();
        });
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
        colApps.setCellValueFactory(new PropertyValueFactory<>("appsBlokir"));
    }

    // Menetapkan aksi untuk tombol Hapus, Refresh, dan Kembali
    private void setupButtonActions() {
        btnHapus.setOnAction(this::handleHapusRiwayat);
        btnRefresh.setOnAction(this::handleRefresh);
        btnKembali.setOnAction(this::handleKembali);
    }

    // Menyimpan referensi ke stage sebelumnya
    public void setPreviousStage(Stage previousStage) {
        this.previousStage = previousStage;
    }

    // Memperbarui data di tabel dan grafik
    private void refreshData() {
        if (riwayatList == null)
            return;
        ObservableList<RiwayatBlokirApps> data = riwayatList.getData();
        riwayatTable.setItems(data);
        updateStatistics();
    }

    // Handler tombol Refresh: Memuat ulang data dari file XML
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

    // Handler tombol Hapus: Menghapus riwayat yang dipilih dari tabel dan file XML
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

    // Handler tombol Kembali: Menampilkan stage sebelumnya dan menutup stage saat
    // ini
    @FXML
    private void handleKembali(ActionEvent event) {
        if (previousStage != null) {
            previousStage.show();
            Stage currentStage = (Stage) btnKembali.getScene().getWindow();
            currentStage.close();
        }
    }

    // Memperbarui grafik statistik durasi blokir per aplikasi
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
        if (appDurations.isEmpty())
            return;

        List<Map.Entry<String, Integer>> sortedApps = appDurations.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Durasi Blokir (menit)");

        for (Map.Entry<String, Integer> entry : sortedApps) {
            int durasiMenit = (int) Math.ceil(entry.getValue() / 60.0);
            series.getData().add(new XYChart.Data<>(entry.getKey(), durasiMenit));
        }
        statistikChart.getData().add(series);
    }

    // Menampilkan alert informasi kepada pengguna
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}