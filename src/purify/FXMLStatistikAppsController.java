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

    // Line 18: Grafik batang untuk menampilkan statistik durasi blokir per aplikasi.
    @FXML
    private BarChart<String, Number> statistikChart;

    // Line 22: Sumbu X (horizontal) pada grafik batang, digunakan untuk menampilkan nama-nama aplikasi.
    @FXML
    private CategoryAxis xAxis;

    // Line 26: Sumbu Y (vertikal) pada grafik batang, digunakan untuk menampilkan total durasi blokir dalam menit.
    @FXML
    private NumberAxis yAxis;

    // Line 30: Tabel yang menampilkan daftar riwayat blokir aplikasi secara detail.
    @FXML
    private TableView<RiwayatBlokirApps> riwayatTable;

    // Line 34: Kolom pada tabel untuk menampilkan nomor urut riwayat blokir.
    @FXML
    private TableColumn<RiwayatBlokirApps, Integer> colNo;

    // Line 38: Kolom pada tabel untuk menampilkan tanggal mulai blokir.
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colTanggal;

    // Line 42: Kolom pada tabel untuk menampilkan durasi blokir dalam satuan menit.
    @FXML
    private TableColumn<RiwayatBlokirApps, Integer> colDurasi;

    // Line 46: Kolom pada tabel untuk menampilkan status sesi blokir (misalnya, "Selesai", "Berjalan").
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colStatus;

    // Line 50: Kolom pada tabel untuk menampilkan aktivitas yang terkait dengan sesi blokir.
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colAktivitas;

    // Line 54: Kolom pada tabel untuk menampilkan daftar aplikasi yang diblokir dalam satu sesi.
    @FXML
    private TableColumn<RiwayatBlokirApps, String> colApps;

    // Line 58: Tombol untuk menghapus riwayat blokir aplikasi yang saat ini dipilih di tabel.
    @FXML
    private Button btnHapus;

    // Line 62: Tombol untuk memuat ulang data riwayat blokir aplikasi dari file XML.
    @FXML
    private Button btnRefresh;

    // Line 66: Tombol untuk kembali ke tampilan (Stage) sebelumnya.
    @FXML
    private Button btnKembali;

    // Line 69: Variabel untuk menyimpan referensi ke Stage sebelumnya. Digunakan saat tombol 'Kembali' ditekan.
    private Stage previousStage;

    // Line 72: Instance tunggal (singleton) dari RiwayatBlokirAppsList yang mengelola data riwayat blokir aplikasi.
    private static RiwayatBlokirAppsList riwayatList = RiwayatBlokirAppsList.getInstance();

    /**
     * Line 76: Metode inisialisasi yang dipanggil secara otomatis saat FXML dimuat.
     * Menyiapkan komponen UI dan memuat data awal.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Line 80: Memanggil metode untuk mengatur kolom-kolom tabel.
        setupTableColumns();
        // Line 81: Memanggil metode untuk menetapkan aksi pada tombol-tombol.
        setupButtonActions();
        // Line 82: Memastikan instance riwayatList sudah ada (jika belum, ambil instance singleton).
        if (riwayatList == null) {
            riwayatList = RiwayatBlokirAppsList.getInstance();
        }
        // Line 85: Memuat data riwayat blokir dari file XML.
        riwayatList.loadFromXML();
        // Line 86: Memperbarui tampilan data di tabel dan grafik.
        refreshData();
    }

    /**
     * Line 90: Mengatur CellValueFactory untuk setiap kolom tabel.
     * Ini menentukan properti mana dari objek RiwayatBlokirApps yang akan ditampilkan di setiap kolom.
     */
    private void setupTableColumns() {
        // Line 93: Mengatur kolom Nomor untuk menampilkan properti "nomor".
        colNo.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        // Line 95: Mengatur kolom Tanggal untuk menampilkan properti "tanggalMulai".
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        // Line 97: Mengatur kolom Durasi. Mengkonversi durasi dari detik ke menit (dibulatkan ke atas).
        colDurasi.setCellValueFactory(cellData -> {
            int durasiDetik = cellData.getValue().getDurasi();
            int durasiMenit = (int) Math.ceil(durasiDetik / 60.0);
            return new javafx.beans.property.SimpleIntegerProperty(durasiMenit).asObject();
        });
        // Line 103: Mengatur kolom Status untuk menampilkan properti "status".
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Line 105: Mengatur kolom Aktivitas untuk menampilkan properti "aktivitas".
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
        // Line 107: Mengatur kolom Aplikasi untuk menampilkan properti "appsBlokir".
        colApps.setCellValueFactory(new PropertyValueFactory<>("appsBlokir"));
    }

    /**
     * Line 112: Menetapkan aksi (event handler) untuk tombol-tombol yang ada di tampilan.
     */
    private void setupButtonActions() {
        // Line 115: Menetapkan metode handleHapusRiwayat sebagai aksi untuk tombol Hapus.
        btnHapus.setOnAction(this::handleHapusRiwayat);
        // Line 117: Menetapkan metode handleRefresh sebagai aksi untuk tombol Refresh.
        btnRefresh.setOnAction(this::handleRefresh);
        // Line 119: Menetapkan metode handleKembali sebagai aksi untuk tombol Kembali.
        btnKembali.setOnAction(this::handleKembali);
    }

    /**
     * Line 123: Metode setter untuk mengatur Stage sebelumnya.
     * Digunakan oleh controller lain untuk memberikan referensi Stage dari mana tampilan ini dibuka.
     *
     * @param previousStage Stage dari tampilan sebelumnya.
     */
    public void setPreviousStage(Stage previousStage) {
        // Line 126: Menyimpan referensi Stage sebelumnya.
        this.previousStage = previousStage;
    }

    /**
     * Line 130: Memperbarui data yang ditampilkan di tabel dan grafik statistik.
     */
    private void refreshData() {
        // Line 133: Memeriksa apakah riwayatList sudah diinisialisasi.
        if (riwayatList == null)
            return;
        // Line 136: Mendapatkan ObservableList dari data riwayat blokir.
        ObservableList<RiwayatBlokirApps> data = riwayatList.getData();
        // Line 137: Mengatur item di riwayatTable dengan data terbaru.
        riwayatTable.setItems(data);
        // Line 138: Memperbarui grafik statistik.
        updateStatistics();
    }

    /**
     * Line 142: Handler untuk tombol Refresh. Memuat ulang data dari file XML dan memperbarui tampilan.
     *
     * @param event Objek ActionEvent yang dihasilkan dari klik tombol.
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        // Line 146: Memuat data dari file XML dan menyimpan status keberhasilan.
        boolean loaded = riwayatList.loadFromXML();
        // Line 147: Menampilkan alert berdasarkan hasil pemuatan data.
        if (loaded) {
            showAlert("Informasi", "Data berhasil diperbarui!");
        } else {
            showAlert("Kesalahan", "Gagal memuat data dari file.");
        }
        // Line 152: Memanggil refreshData untuk memperbarui tabel dan grafik dengan data yang baru dimuat.
        refreshData();
    }

    /**
     * Line 156: Handler untuk tombol Hapus. Menghapus riwayat yang dipilih dari tabel dan juga dari file XML.
     *
     * @param event Objek ActionEvent yang dihasilkan dari klik tombol.
     */
    @FXML
    private void handleHapusRiwayat(ActionEvent event) {
        // Line 160: Mendapatkan item (riwayat) yang saat ini dipilih di tabel.
        RiwayatBlokirApps selected = riwayatTable.getSelectionModel().getSelectedItem();
        // Line 161: Memeriksa apakah ada item yang dipilih.
        if (selected == null) {
            // Line 163: Menampilkan alert jika tidak ada baris yang dipilih.
            showAlert("Informasi", "Silakan pilih baris riwayat yang ingin dihapus.");
            return;
        }
        // Line 167: Membuat dialog konfirmasi sebelum menghapus riwayat.
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        // Line 169: Mengatur judul dialog konfirmasi.
        confirmation.setTitle("Konfirmasi Hapus");
        // Line 171: Mengatur header teks dialog menjadi null (tidak ada header khusus).
        confirmation.setHeaderText(null);
        // Line 173: Mengatur konten pesan dialog.
        confirmation.setContentText("Apakah Anda yakin ingin menghapus riwayat ini?");
        // Line 175: Menampilkan dialog konfirmasi dan menunggu respons pengguna.
        Optional<ButtonType> result = confirmation.showAndWait();
        // Line 176: Memeriksa apakah pengguna mengklik tombol OK.
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Line 178: Menghapus riwayat yang dipilih dari ObservableList.
            riwayatList.getData().remove(selected);
            // Line 179: Menyimpan perubahan ke file XML.
            riwayatList.saveToXML();
            // Line 180: Memperbarui tampilan data di tabel dan grafik.
            refreshData();
            // Line 181: Menampilkan alert bahwa riwayat berhasil dihapus.
            showAlert("Informasi", "Riwayat berhasil dihapus.");
        }
    }

    /**
     * Line 186: Handler untuk tombol Kembali. Menampilkan Stage sebelumnya dan menutup Stage saat ini.
     *
     * @param event Objek ActionEvent yang dihasilkan dari klik tombol.
     */
    @FXML
    private void handleKembali(ActionEvent event) {
        // Line 190: Memeriksa apakah ada Stage sebelumnya yang tersimpan.
        if (previousStage != null) {
            // Line 192: Menampilkan Stage sebelumnya.
            previousStage.show();
            // Line 193: Mendapatkan referensi ke Stage saat ini.
            Stage currentStage = (Stage) btnKembali.getScene().getWindow();
            // Line 195: Menutup Stage saat ini.
            currentStage.close();
        }
    }

    /**
     * Line 200: Memperbarui grafik batang yang menampilkan total durasi blokir untuk setiap aplikasi.
     */
    private void updateStatistics() {
        // Line 203: Mengosongkan semua data dari grafik statistik sebelum menambahkan yang baru.
        statistikChart.getData().clear();
        // Line 204: Memeriksa apakah riwayatList kosong atau null. Jika ya, keluar dari metode.
        if (riwayatList == null || riwayatList.getData().isEmpty()) {
            return;
        }

        // Line 209: Membuat HashMap untuk menyimpan total durasi blokir untuk setiap aplikasi.
        Map<String, Integer> appDurations = new HashMap<>();
        // Line 210: Mengiterasi setiap riwayat blokir aplikasi.
        for (RiwayatBlokirApps riwayat : riwayatList.getData()) {
            // Line 212: Memastikan daftar aplikasi yang diblokir tidak null.
            if (riwayat.getAppsBlokir() != null) {
                // Line 214: Memisahkan nama-nama aplikasi jika ada lebih dari satu (dipisahkan koma dan spasi).
                String[] apps = riwayat.getAppsBlokir().split(", ");
                // Line 216: Mengiterasi setiap aplikasi dalam daftar.
                for (String app : apps) {
                    // Line 218: Menambahkan durasi blokir riwayat saat ini ke total durasi aplikasi yang sesuai.
                    // Jika aplikasi belum ada di peta, tambahkan dengan durasi ini; jika sudah ada, jumlahkan.
                    appDurations.merge(app, riwayat.getDurasi(), Integer::sum);
                }
            }
        }
        // Line 223: Jika tidak ada data durasi aplikasi, keluar dari metode.
        if (appDurations.isEmpty())
            return;

        // Line 226: Mengurutkan daftar aplikasi berdasarkan total durasi blokir secara menurun.
        List<Map.Entry<String, Integer>> sortedApps = appDurations.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Line 231: Membuat seri data baru untuk grafik batang.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // Line 232: Menetapkan nama untuk seri data.
        series.setName("Total Durasi Blokir (menit)");

        // Line 235: Mengisi seri data dengan data aplikasi yang sudah diurutkan.
        for (Map.Entry<String, Integer> entry : sortedApps) {
            // Line 237: Mengkonversi total durasi dari detik ke menit (dibulatkan ke atas).
            int durasiMenit = (int) Math.ceil(entry.getValue() / 60.0);
            // Line 239: Menambahkan titik data ke seri grafik (nama aplikasi dan durasi dalam menit).
            series.getData().add(new XYChart.Data<>(entry.getKey(), durasiMenit));
        }
        // Line 241: Menambahkan seri data ke grafik statistik.
        statistikChart.getData().add(series);
    }

    /**
     * Line 246: Metode utility untuk menampilkan alert dialog informasi kepada pengguna.
     *
     * @param title Judul yang akan ditampilkan di alert dialog.
     * @param message Pesan konten yang akan ditampilkan di alert dialog.
     */
    private void showAlert(String title, String message) {
        // Line 249: Membuat instance Alert dengan tipe INFORMASI.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // Line 251: Mengatur judul alert.
        alert.setTitle(title);
        // Line 253: Mengatur header teks alert menjadi null (tidak ada header).
        alert.setHeaderText(null);
        // Line 255: Mengatur konten pesan alert.
        alert.setContentText(message);
        // Line 257: Menampilkan alert dan menunggu hingga pengguna menutupnya.
        alert.showAndWait();
    }
}