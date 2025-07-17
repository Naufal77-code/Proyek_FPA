package purify;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
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

    // Variabel untuk komponen UI yang diinjeksi dari file FXML
    @FXML private BarChart<String, Number> durasiChart;
    @FXML private TableView<RiwayatBlokir> riwayatTable;
    @FXML private ComboBox<String> periodeComboBox;
    @FXML private TableColumn<RiwayatBlokir, Integer> colNomor;
    @FXML private TableColumn<RiwayatBlokir, String> colTanggal;
    @FXML private TableColumn<RiwayatBlokir, Integer> colDurasi;
    @FXML private TableColumn<RiwayatBlokir, String> colStatus;
    @FXML private TableColumn<RiwayatBlokir, String> colAktivitas;
    @FXML private TextField nomorField;
    @FXML private TextField aktivitasBaruField;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;
    @FXML private Button btnKembali;
    @FXML private String previousScreen;
    @FXML private Stage previousStage;
    
    // Variabel untuk komponen navigasi periode
    @FXML private HBox dayNavigationBox;
    @FXML private Button btnPreviousDay;
    @FXML private Button btnNextDay;
    @FXML private Label dayLabel;
    @FXML private HBox weekNavigationBox;
    @FXML private Button btnPreviousWeek;
    @FXML private Button btnNextWeek;
    @FXML private Label weekRangeLabel;
    @FXML private HBox yearNavigationBox;
    @FXML private Button btnPreviousYear;
    @FXML private Button btnNextYear;
    @FXML private Label yearLabel;
    @FXML private CategoryAxis dayAxis;
    @FXML private NumberAxis durationAxis;

    // Variabel untuk menyimpan list data riwayat
    private RiwayatBlokirList riwayatList;
    // Variabel untuk menyimpan tanggal acuan saat ini untuk navigasi
    private LocalDate currentDisplayDate;

    // Variabel untuk mengubah format tanggal dan waktu menjadi string
    private final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter simpleHourFormatter = DateTimeFormatter.ofPattern("HH:00");
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Metode ini dijalankan secara otomatis saat FXML selesai dimuat.
     * Berfungsi untuk melakukan setup awal komponen UI.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentDisplayDate = LocalDate.now(); 
        setupTableColumns();
        setupPeriodComboBox();
        setupButtonActions();
        setupTableSelectionListener();
    }    
    
    /**
     * Menerima data riwayat dari scene sebelumnya dan memulai pembaruan UI.
     * @param riwayatList Objek yang berisi semua data riwayat.
     */
    public void setRiwayatList(RiwayatBlokirList riwayatList, String previousScreen, Stage previousStage) {
        this.riwayatList = riwayatList;
        this.previousScreen = previousScreen;
        this.previousStage = previousStage;
        riwayatTable.setItems(riwayatList.getData());
        updateChart();
    }
    
    /**
     * Mengisi ComboBox dengan pilihan periode (Harian, Mingguan, Bulanan).
     */
    private void setupPeriodComboBox() {
        periodeComboBox.setItems(FXCollections.observableArrayList("Harian (Per Jam)", "Mingguan", "Bulanan"));
        periodeComboBox.getSelectionModel().select("Harian (Per Jam)");
        periodeComboBox.setOnAction(e -> updateChart());
    }

    /**
     * Menghubungkan setiap kolom di TableView dengan properti dari objek RiwayatBlokir.
     */
    private void setupTableColumns() {
        colNomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        colDurasi.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
    }

    /**
     * Mendaftarkan event handler untuk setiap aksi tombol.
     */
    private void setupButtonActions() {
        btnEdit.setOnAction(this::handleEdit);
        btnDelete.setOnAction(this::handleDelete);
        btnKembali.setOnAction(this::handleKembali);
        btnPreviousDay.setOnAction(e -> navigateDay(-1));
        btnNextDay.setOnAction(e -> navigateDay(1));
        btnPreviousWeek.setOnAction(e -> navigateWeek(-1));
        btnNextWeek.setOnAction(e -> navigateWeek(1));
        btnPreviousYear.setOnAction(e -> navigateYear(-1));
        btnNextYear.setOnAction(e -> navigateYear(1));
    }
    
    /**
     * Menambahkan listener yang akan mengisi field input saat sebuah baris di tabel dipilih.
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

    /**
     * Mengubah tanggal acuan maju/mundur sebanyak satu hari.
     * @param direction -1 untuk mundur, 1 untuk maju.
     */
    private void navigateDay(int direction) {
        currentDisplayDate = currentDisplayDate.plusDays(direction);
        updateChart();
    }

    /**
     * Mengubah tanggal acuan maju/mundur sebanyak satu minggu.
     * @param direction -1 untuk mundur, 1 untuk maju.
     */
    private void navigateWeek(int direction) {
        currentDisplayDate = currentDisplayDate.plusWeeks(direction);
        updateChart();
    }

    /**
     * Mengubah tanggal acuan maju/mundur sebanyak satu tahun.
     * @param direction -1 untuk mundur, 1 untuk maju.
     */
    private void navigateYear(int direction) {
        currentDisplayDate = currentDisplayDate.plusYears(direction);
        updateChart();
    }
    
    /**
     * Metode utama untuk memperbarui chart berdasarkan periode yang dipilih.
     */
    private void updateChart() {
        if (riwayatList == null) return;
        durasiChart.getData().clear();
        String selectedPeriod = periodeComboBox.getSelectionModel().getSelectedItem();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Durasi (menit)");

        boolean isDaily = "Harian (Per Jam)".equals(selectedPeriod);
        boolean isWeekly = "Mingguan".equals(selectedPeriod);
        boolean isMonthly = "Bulanan".equals(selectedPeriod);

        dayNavigationBox.setVisible(isDaily);
        dayNavigationBox.setManaged(isDaily);
        weekNavigationBox.setVisible(isWeekly);
        weekNavigationBox.setManaged(isWeekly);
        yearNavigationBox.setVisible(isMonthly);
        yearNavigationBox.setManaged(isMonthly);

        switch (selectedPeriod) {
            case "Harian (Per Jam)": updateHourlyChart(series); break;
            case "Mingguan": updateWeeklyChart(series); break;
            case "Bulanan": updateMonthlyChart(series); break;
        }
        durasiChart.getData().add(series);
    }
    
    /**
     * Membuat data untuk chart harian, menampilkan 24 jam untuk tanggal yang dipilih.
     * @param series Objek series chart untuk diisi data.
     */
    private void updateHourlyChart(XYChart.Series<String, Number> series) {
        dayAxis.setLabel("Jam");
        dayLabel.setText(currentDisplayDate.format(fullDateFormatter));

        Map<Integer, Integer> existingData = riwayatList.getData().stream()
            .map(r -> LocalDateTime.parse(r.getTanggalMulai(), inputFormatter))
            .filter(ldt -> ldt.toLocalDate().equals(currentDisplayDate))
            .collect(Collectors.groupingBy(
                LocalDateTime::getHour,
                Collectors.summingInt(ldt -> riwayatList.getData().stream()
                    .filter(rb -> LocalDateTime.parse(rb.getTanggalMulai(), inputFormatter).equals(ldt))
                    .mapToInt(RiwayatBlokir::getDurasi)
                    .findFirst().orElse(0)
                )
            ));

        for (int hour = 0; hour < 24; hour++) {
            int totalDuration = existingData.getOrDefault(hour, 0);
            String hourLabel = LocalTime.of(hour, 0).format(simpleHourFormatter);
            series.getData().add(new XYChart.Data<>(hourLabel, totalDuration));
        }
    }

    /**
     * Membuat data untuk chart mingguan, menampilkan 7 hari (Senin-Minggu).
     * @param series Objek series chart untuk diisi data.
     */
    private void updateWeeklyChart(XYChart.Series<String, Number> series) {
        dayAxis.setLabel("Hari dalam Seminggu");
        
        LocalDate monday = currentDisplayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = currentDisplayDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        weekRangeLabel.setText(String.format("%s - %s", monday.format(dayFormatter), sunday.format(dayFormatter)));

        Map<DayOfWeek, Integer> weeklyData = riwayatList.getData().stream()
            .filter(r -> {
                LocalDate tgl = LocalDate.parse(r.getTanggalMulai().split(" ")[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                return !tgl.isBefore(monday) && !tgl.isAfter(sunday);
            })
            .collect(Collectors.groupingBy(
                r -> LocalDate.parse(r.getTanggalMulai().split(" ")[0], DateTimeFormatter.ofPattern("dd/MM/yyyy")).getDayOfWeek(),
                Collectors.summingInt(RiwayatBlokir::getDurasi)
            ));

        DayOfWeek[] daysOfWeek = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY };
        for (DayOfWeek day : daysOfWeek) {
            String dayName = day.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            int totalDuration = weeklyData.getOrDefault(day, 0);
            series.getData().add(new XYChart.Data<>(dayName, totalDuration));
        }
    }

    /**
     * Membuat data untuk chart bulanan, menampilkan 12 bulan untuk tahun yang dipilih.
     * @param series Objek series chart untuk diisi data.
     */
    private void updateMonthlyChart(XYChart.Series<String, Number> series) {
        dayAxis.setLabel("Bulan");
        int year = currentDisplayDate.getYear();
        yearLabel.setText(String.valueOf(year));

        Map<Month, Integer> monthlyData = riwayatList.getData().stream()
            .map(r -> LocalDateTime.parse(r.getTanggalMulai(), inputFormatter))
            .filter(ldt -> ldt.getYear() == year)
            .collect(Collectors.groupingBy(
                LocalDateTime::getMonth,
                Collectors.summingInt(ldt -> riwayatList.getData().stream()
                    .filter(rb -> LocalDateTime.parse(rb.getTanggalMulai(), inputFormatter).equals(ldt))
                    .mapToInt(RiwayatBlokir::getDurasi)
                    .findFirst().orElse(0)
                )
            ));

        for (Month month : Month.values()) {
            String monthName = month.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            int totalDuration = monthlyData.getOrDefault(month, 0);
            series.getData().add(new XYChart.Data<>(monthName, totalDuration));
        }
    }
    
    
    /**
     * Dijalankan saat tombol 'Edit' ditekan.
     */
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
    
    /**
     * Dijalankan saat tombol 'Delete' ditekan.
     */
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

    /**
     * Dijalankan saat tombol 'Kembali' ditekan untuk menutup window.
     */
    

@FXML
private void handleKembali(ActionEvent event) {
        Stage currentStage = (Stage) btnKembali.getScene().getWindow();
        
        if ("mainMenu".equals(previousScreen)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMainMenu.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Purify - Main Menu");
                stage.show();
                currentStage.close();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal kembali ke main menu");
            }
        } else if ("blokirHP".equals(previousScreen)) {
            // Kembali ke stage blokir HP yang asli
            previousStage.show();
            currentStage.close();
        }
    }
    
    /**
     * Metode helper untuk membersihkan field input setelah aksi.
     */
    private void clearFields() {
        nomorField.clear();
        aktivitasBaruField.clear();
        riwayatTable.getSelectionModel().clearSelection();
    }

    /**
     * Metode helper untuk menampilkan dialog Alert.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}