package purify;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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


    private RiwayatBlokirList riwayatList;
    private LocalDate currentDisplayDate;

    private final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("id", "ID"));
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter simpleHourFormatter = DateTimeFormatter.ofPattern("HH:00");
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentDisplayDate = LocalDate.now(); 
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
        periodeComboBox.setItems(FXCollections.observableArrayList("Harian (Per Jam)", "Mingguan", "Bulanan"));
        periodeComboBox.getSelectionModel().select("Bulanan");
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
        btnPreviousDay.setOnAction(e -> navigateDay(-1));
        btnNextDay.setOnAction(e -> navigateDay(1));
        btnPreviousWeek.setOnAction(e -> navigateWeek(-1));
        btnNextWeek.setOnAction(e -> navigateWeek(1));
        btnPreviousYear.setOnAction(e -> navigateYear(-1));
        btnNextYear.setOnAction(e -> navigateYear(1));
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

    private void navigateDay(int direction) {
        currentDisplayDate = currentDisplayDate.plusDays(direction);
        updateChart();
    }

    private void navigateWeek(int direction) {
        currentDisplayDate = currentDisplayDate.plusWeeks(direction);
        updateChart();
    }

    private void navigateYear(int direction) {
        currentDisplayDate = currentDisplayDate.plusYears(direction);
        updateChart();
    }
    
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

        // Urutan hari Senin -> Minggu
        DayOfWeek[] daysOfWeek = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY };
        for (DayOfWeek day : daysOfWeek) {
            String dayName = day.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            int totalDuration = weeklyData.getOrDefault(day, 0);
            series.getData().add(new XYChart.Data<>(dayName, totalDuration));
        }
    }

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
    
    private static class YearWeek implements Comparable<YearWeek> {
        private final int year;
        private final int week;
        public YearWeek(int year, int week) { this.year = year; this.week = week; }
        public static YearWeek from(LocalDate date) { return new YearWeek(date.getYear(), date.get(WeekFields.ISO.weekOfYear())); }
        public int year() { return year; }
        public int week() { return week; }
        @Override
        public int compareTo(YearWeek other) {
            return Comparator.comparingInt(YearWeek::year).thenComparingInt(YearWeek::week).compare(this, other);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            YearWeek yearWeek = (YearWeek) o;
            return year == yearWeek.year && week == yearWeek.week;
        }
        @Override
        public int hashCode() { return Objects.hash(year, week); }
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