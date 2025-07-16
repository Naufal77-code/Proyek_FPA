package purify;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class FXMLStatistikController implements Initializable {

    // Komponen FXML yang sudah ada
    @FXML
    private BarChart<String, Number> durasiChart;
    @FXML
    private TableView<RiwayatBlokir> riwayatTable;
    @FXML
    private TableColumn<RiwayatBlokir, Integer> colNomor;
    @FXML
    private TableColumn<RiwayatBlokir, String> colTanggal;
    @FXML
    private TableColumn<RiwayatBlokir, Integer> colDurasi;
    @FXML
    private TableColumn<RiwayatBlokir, String> colStatus;
    @FXML
    private TableColumn<RiwayatBlokir, String> colAktivitas;
    @FXML
    private TextField nomorField;
    @FXML
    private TextField aktivitasBaruField;
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnKembali;
    @FXML
    private ComboBox<String> periodeComboBox;

    // Komponen FXML baru untuk navigasi minggu
    @FXML
    private HBox weekNavigationBox;
    @FXML
    private Button btnPreviousWeek;
    @FXML
    private Button btnNextWeek;
    @FXML
    private Label weekRangeLabel;

    private RiwayatBlokirList riwayatList;
    private LocalDate selectedWeekDate; // Menyimpan tanggal acuan untuk minggu yang ditampilkan

    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
    private final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("dd/MM HH:00");
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedWeekDate = LocalDate.now(); // Inisialisasi dengan tanggal hari ini

        setupTableColumns();
        setupPeriodComboBox();
        setupButtonActions();
        setupTableSelectionListener();
    }

    public void setRiwayatList(RiwayatBlokirList riwayatList) {
        this.riwayatList = riwayatList;
        riwayatTable.setItems(riwayatList.getData());
        updateChart();
    }

    private void setupPeriodComboBox() {
        // Ubah urutan item di sini
        periodeComboBox.setItems(FXCollections.observableArrayList(
                "Harian (Per Jam)", "Mingguan", "Bulanan"));
        // Atur default ke "Harian (Per Jam)"
        periodeComboBox.getSelectionModel().select("Harian (Per Jam)");
        periodeComboBox.setOnAction(e -> updateChart());
    }

    private void setupTableColumns() {
        colNomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colDurasi.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
    }

    private void setupButtonActions() {
        btnEdit.setOnAction(this::handleEdit);
        btnDelete.setOnAction(this::handleDelete);
        btnKembali.setOnAction(this::handleKembali);
        btnPreviousWeek.setOnAction(e -> navigateWeek(-1));
        btnNextWeek.setOnAction(e -> navigateWeek(1));
    }

    private void setupTableSelectionListener() {
        riwayatTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        nomorField.setText(String.valueOf(newSelection.getNomor()));
                        aktivitasBaruField.setText(newSelection.getAktivitas());
                    }
                });
    }

    private void navigateWeek(int direction) {
        selectedWeekDate = selectedWeekDate.plusWeeks(direction);
        updateChart();
    }

    private void updateChart() {
        if (riwayatList == null)
            return;
        durasiChart.getData().clear();
        String selectedPeriod = periodeComboBox.getSelectionModel().getSelectedItem();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Durasi (menit)");

        // Mengatur visibilitas navigasi minggu
        boolean isWeeklyView = "Mingguan".equals(selectedPeriod);
        weekNavigationBox.setVisible(isWeeklyView);
        weekNavigationBox.setManaged(isWeeklyView);

        switch (selectedPeriod) {
            case "Mingguan":
                updateWeeklyChart(series);
                break;
            case "Harian (Per Jam)":
                updateHourlyChart(series);
                break;

            case "Bulanan":
                updateMonthlyChart(series);
                break;
        }
        durasiChart.getData().add(series);
    }

    private void updateHourlyChart(XYChart.Series<String, Number> series) {
        durasiChart.getXAxis().setLabel("Jam");
        Map<LocalDateTime, Integer> hourStats = riwayatList.getData().stream()
                .collect(Collectors.groupingBy(
                        r -> LocalDateTime.parse(r.getTanggalMulai(), inputFormatter).withMinute(0).withSecond(0),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));
        hourStats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData()
                        .add(new XYChart.Data<>(entry.getKey().format(hourFormatter), entry.getValue())));
    }

    // --- METODE UTAMA YANG DIUBAH ---
    private void updateWeeklyChart(XYChart.Series<String, Number> series) {
        durasiChart.getXAxis().setLabel("Hari dalam Seminggu");

        // 1. Tentukan awal (Senin) dan akhir (Minggu) dari minggu yang dipilih
        LocalDate monday = selectedWeekDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = selectedWeekDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // 2. Update label rentang tanggal
        weekRangeLabel.setText(String.format("%s - %s", monday.format(dayFormatter), sunday.format(dayFormatter)));

        // 3. Filter riwayat hanya untuk minggu ini
        Map<DayOfWeek, Integer> weeklyData = riwayatList.getData().stream()
                .filter(r -> {
                    LocalDate tgl = LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return !tgl.isBefore(monday) && !tgl.isAfter(sunday);
                })
                .collect(Collectors.groupingBy(
                        r -> LocalDate
                                .parse(r.getTanggalMulai().split(" ")[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                .getDayOfWeek(),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));

        // 4. Buat data untuk setiap hari dari Senin hingga Minggu
        // Locale("id") untuk mendapatkan nama hari dalam Bahasa Indonesia
        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            int totalDuration = weeklyData.getOrDefault(day, 0);
            series.getData().add(new XYChart.Data<>(dayName, totalDuration));
        }
    }

    private void updateMonthlyChart(XYChart.Series<String, Number> series) {
        durasiChart.getXAxis().setLabel("Bulan");
        Map<YearMonth, Integer> monthlyStats = riwayatList.getData().stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                                DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));
        monthlyStats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> series.getData()
                        .add(new XYChart.Data<>(entry.getKey().format(monthFormatter), entry.getValue())));
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        try {
            int nomor = Integer.parseInt(nomorField.getText().trim());
            String aktivitasBaru = aktivitasBaruField.getText().trim();
            if (aktivitasBaru.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Aktivitas baru tidak boleh kosong!");
                return;
            }
            riwayatList.editAktivitas(nomor, aktivitasBaru);
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Aktivitas berhasil diubah!");
            riwayatTable.refresh();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Nomor harus berupa angka!");
        } catch (IndexOutOfBoundsException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Nomor riwayat tidak valid!");
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        try {
            int nomor = Integer.parseInt(nomorField.getText().trim());

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Konfirmasi Hapus");
            confirmation.setHeaderText("Anda akan menghapus riwayat nomor " + nomor);
            confirmation.setContentText("Apakah Anda yakin?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                riwayatList.remove(nomor);
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Riwayat berhasil dihapus!");
                updateChart();
                clearFields();
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Nomor harus berupa angka!");
        } catch (IndexOutOfBoundsException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Nomor riwayat tidak valid!");
        }
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        Stage stage = (Stage) btnKembali.getScene().getWindow();
        stage.close();
    }

    private void clearFields() {
        nomorField.clear();
        aktivitasBaruField.clear();
        riwayatTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}