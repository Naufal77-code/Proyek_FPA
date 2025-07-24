package purify;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
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

    // Komponen chart batang untuk menampilkan durasi pemblokiran
    @FXML
    private BarChart<String, Number> durasiChart;

    // Tabel untuk menampilkan riwayat pemblokiran
    @FXML
    private TableView<RiwayatBlokir> riwayatTable;

    // Dropdown untuk memilih periode tampilan statistik
    @FXML
    private ComboBox<String> periodeComboBox;

    // Kolom-kolom pada tabel riwayat
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

    // Field input untuk mengedit data
    @FXML
    private TextField nomorField;
    @FXML
    private TextField aktivitasBaruField;

    // Tombol-tombol untuk aksi edit, hapus, kembali, dan statistik aplikasi
    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnKembali;
    @FXML
    private Button btnStatistikApps;

    // Navigasi tampilan harian
    @FXML
    private HBox dayNavigationBox;
    @FXML
    private Button btnPreviousDay;
    @FXML
    private Button btnNextDay;
    @FXML
    private Label dayLabel;

    // Navigasi tampilan mingguan
    @FXML
    private HBox weekNavigationBox;
    @FXML
    private Button btnPreviousWeek;
    @FXML
    private Button btnNextWeek;
    @FXML
    private Label weekRangeLabel;

    // Navigasi tampilan tahunan/bulanan
    @FXML
    private HBox yearNavigationBox;
    @FXML
    private Button btnPreviousYear;
    @FXML
    private Button btnNextYear;
    @FXML
    private Label yearLabel;

    // Sumbu X dan Y pada chart
    @FXML
    private CategoryAxis dayAxis;
    @FXML
    private NumberAxis durationAxis;

    // Data utama yang digunakan untuk menampilkan riwayat
    private RiwayatBlokirList riwayatList;

    // Tanggal saat ini yang sedang ditampilkan
    private LocalDate currentDisplayDate;

    // Informasi tentang layar sebelumnya
    private String previousScreen;
    private Stage previousStage;

    // Formatter untuk menampilkan tanggal dengan format yang sesuai
    private final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy",
            new Locale("id", "ID"));
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter simpleHourFormatter = DateTimeFormatter.ofPattern("HH:00");
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Dipanggil otomatis saat FXML diload. Menyiapkan semua komponen awal.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentDisplayDate = LocalDate.now(); // Set tanggal tampilan awal ke hari ini
        setupTableColumns(); // Atur kolom tabel
        setupPeriodComboBox(); // Isi dan atur pilihan periode
        setupButtonActions(); // Atur aksi dari tombol-tombol
        setupTableSelectionListener(); // Atur listener ketika baris tabel dipilih
    }

    /**
     * Setter untuk data utama dan informasi layar sebelumnya
     */
    public void setRiwayatList(RiwayatBlokirList riwayatList, String previousScreen, Stage previousStage) {
        this.riwayatList = riwayatList;
        this.previousScreen = previousScreen;
        this.previousStage = previousStage;
        riwayatTable.setItems(riwayatList.getData()); // Tampilkan data ke tabel
        updateChart(); // Update chart sesuai data dan tanggal
    }

    /**
     * Setup isi pilihan pada ComboBox periode dan event-nya
     */
    private void setupPeriodComboBox() {
        periodeComboBox.setItems(FXCollections.observableArrayList("Harian (Per Jam)", "Mingguan", "Bulanan"));
        periodeComboBox.getSelectionModel().select("Harian (Per Jam)");
        periodeComboBox.setOnAction(e -> updateChart());
    }

    /**
     * Setup kolom-kolom pada tabel dengan property dari RiwayatBlokir
     */
    private void setupTableColumns() {
        colNomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colDurasi.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
    }

    /**
     * Setup aksi setiap tombol di layar
     */
    private void setupButtonActions() {
        btnEdit.setOnAction(this::handleEdit);
        btnDelete.setOnAction(this::handleDelete);
        btnKembali.setOnAction(this::handleKembali);
        btnStatistikApps.setOnAction(this::handleStatistikApps);
        btnPreviousDay.setOnAction(e -> navigateDay(-1));
        btnNextDay.setOnAction(e -> navigateDay(1));
        btnPreviousWeek.setOnAction(e -> navigateWeek(-1));
        btnNextWeek.setOnAction(e -> navigateWeek(1));
        btnPreviousYear.setOnAction(e -> navigateYear(-1));
        btnNextYear.setOnAction(e -> navigateYear(1));
    }

    /**
     * Setup listener ketika baris tabel dipilih agar field input terisi otomatis
     */
    private void setupTableSelectionListener() {
        riwayatTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        nomorField.setText(String.valueOf(newSelection.getNomor()));
                        aktivitasBaruField.setText(newSelection.getAktivitas());
                    }
                });
    }

    // Navigasi tampilan harian: +1 atau -1 hari
    private void navigateDay(int direction) {
        currentDisplayDate = currentDisplayDate.plusDays(direction);
        updateChart();
    }

    // Navigasi tampilan mingguan
    private void navigateWeek(int direction) {
        currentDisplayDate = currentDisplayDate.plusWeeks(direction);
        updateChart();
    }

    // Navigasi tampilan tahunan/bulanan
    private void navigateYear(int direction) {
        currentDisplayDate = currentDisplayDate.plusYears(direction);
        updateChart();
    }

    /**
     * Menentukan jenis chart yang akan ditampilkan berdasarkan periode
     */
    private void updateChart() {
        if (riwayatList == null)
            return;

        durasiChart.getData().clear();
        String selectedPeriod = periodeComboBox.getSelectionModel().getSelectedItem();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Durasi (menit)");

        // Atur visibilitas navigasi berdasarkan periode
        dayNavigationBox.setVisible(selectedPeriod.equals("Harian (Per Jam)"));
        dayNavigationBox.setManaged(selectedPeriod.equals("Harian (Per Jam)"));
        weekNavigationBox.setVisible(selectedPeriod.equals("Mingguan"));
        weekNavigationBox.setManaged(selectedPeriod.equals("Mingguan"));
        yearNavigationBox.setVisible(selectedPeriod.equals("Bulanan"));
        yearNavigationBox.setManaged(selectedPeriod.equals("Bulanan"));

        // Update data chart sesuai periode
        switch (selectedPeriod) {
            case "Harian (Per Jam)":
                updateHourlyChart(series);
                break;
            case "Mingguan":
                updateWeeklyChart(series);
                break;
            case "Bulanan":
                updateMonthlyChart(series);
                break;
        }

        durasiChart.getData().add(series);
    }

    // Mengisi chart dengan data per jam (harian)
    private void updateHourlyChart(XYChart.Series<String, Number> series) {
        dayAxis.setLabel("Jam");
        dayLabel.setText(currentDisplayDate.format(fullDateFormatter));

        Map<Integer, Integer> hourlyData = riwayatList.getData().stream()
                .filter(r -> LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")).equals(currentDisplayDate))
                .collect(Collectors.groupingBy(
                        r -> LocalTime.parse(r.getTanggalMulai().split(" ")[1],
                                DateTimeFormatter.ofPattern("HH:mm")).getHour(),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));

        for (int hour = 0; hour < 24; hour++) {
            String hourLabel = LocalTime.of(hour, 0).format(simpleHourFormatter);
            series.getData().add(new XYChart.Data<>(hourLabel, hourlyData.getOrDefault(hour, 0)));
        }
    }

    // Mengisi chart dengan data per hari dalam satu minggu
    private void updateWeeklyChart(XYChart.Series<String, Number> series) {
        dayAxis.setLabel("Hari dalam Seminggu");
        LocalDate monday = currentDisplayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = currentDisplayDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        weekRangeLabel.setText(String.format("%s - %s", monday.format(dayFormatter), sunday.format(dayFormatter)));

        Map<DayOfWeek, Integer> weeklyData = riwayatList.getData().stream()
                .filter(r -> {
                    LocalDate date = LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return !date.isBefore(monday) && !date.isAfter(sunday);
                })
                .collect(Collectors.groupingBy(
                        r -> LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")).getDayOfWeek(),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));

        for (DayOfWeek day : DayOfWeek.values()) {
            String dayName = day.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            series.getData().add(new XYChart.Data<>(dayName, weeklyData.getOrDefault(day, 0)));
        }
    }

    // Mengisi chart dengan data bulanan sepanjang tahun
    private void updateMonthlyChart(XYChart.Series<String, Number> series) {
        dayAxis.setLabel("Bulan");
        int year = currentDisplayDate.getYear();
        yearLabel.setText(String.valueOf(year));

        Map<Month, Integer> monthlyData = riwayatList.getData().stream()
                .filter(r -> Integer.parseInt(r.getTanggalMulai().split("/")[2].split(" ")[0]) == year)
                .collect(Collectors.groupingBy(
                        r -> Month.of(Integer.parseInt(r.getTanggalMulai().split("/")[1])),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));

        for (Month month : Month.values()) {
            String monthName = month.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            series.getData().add(new XYChart.Data<>(monthName, monthlyData.getOrDefault(month, 0)));
        }
    }

    // Event saat tombol edit ditekan
    @FXML
    private void handleEdit(ActionEvent event) {
        try {
            int nomor = Integer.parseInt(nomorField.getText().trim());
            String aktivitasBaru = aktivitasBaruField.getText().trim();

            if (aktivitasBaru.isEmpty()) {
                showAlert("Error", "Aktivitas baru tidak boleh kosong!");
                return;
            }

            riwayatList.editAktivitas(nomor, aktivitasBaru);
            showAlert("Sukses", "Aktivitas berhasil diubah!");
            riwayatTable.refresh();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Nomor harus berupa angka!");
        } catch (IndexOutOfBoundsException e) {
            showAlert("Error", "Nomor riwayat tidak valid!");
        }
    }

    // Event saat tombol hapus ditekan
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
                showAlert("Sukses", "Riwayat berhasil dihapus!");
                updateChart();
                clearFields();
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Nomor harus berupa angka!");
        } catch (IndexOutOfBoundsException e) {
            showAlert("Error", "Nomor riwayat tidak valid!");
        }
    }

    // Event saat tombol kembali ditekan
    @FXML
    private void handleKembali(ActionEvent event) {
        Stage currentStage = (Stage) btnKembali.getScene().getWindow();

        if ("mainMenu".equals(previousScreen)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLMainMenu.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Purify - Main Menu");
                stage.show();
                currentStage.close();
            } catch (IOException e) {
                showAlert("Error", "Gagal kembali ke main menu");
            }
        } else if (previousStage != null) {
            previousStage.show();
            currentStage.close();
        }
    }

    // Event saat tombol "Statistik Aplikasi" ditekan
    @FXML
    private void handleStatistikApps(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLStatistikApps.fxml"));
            Parent root = loader.load();

            FXMLStatistikAppsController controller = loader.getController();
            controller.setPreviousStage((Stage) btnStatistikApps.getScene().getWindow());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Statistik Aplikasi");
            stage.show();

            ((Stage) btnStatistikApps.getScene().getWindow()).hide();
        } catch (IOException e) {
            showAlert("Error", "Gagal membuka statistik aplikasi");
            e.printStackTrace();
        }
    }

    // Menghapus isi field dan reset seleksi tabel
    private void clearFields() {
        nomorField.clear();
        aktivitasBaruField.clear();
        riwayatTable.getSelectionModel().clearSelection();
    }

    // Menampilkan alert dengan pesan tertentu
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}