package purify;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class FXMLStatistikController implements Initializable {

    @FXML private BarChart<String, Number> durasiChart;
    @FXML private TableView<RiwayatBlokir> riwayatTable;
    @FXML private TableColumn<RiwayatBlokir, Integer> colNomor;
    @FXML private TableColumn<RiwayatBlokir, String> colTanggal;
    @FXML private TableColumn<RiwayatBlokir, Integer> colDurasi;
    @FXML private TableColumn<RiwayatBlokir, String> colStatus;
    @FXML private TableColumn<RiwayatBlokir, String> colAktivitas;
    @FXML private TextField nomorField;
    @FXML private TextField aktivitasBaruField;
    @FXML private Button btnEdit;
    @FXML private Button btnKembali;
    @FXML private ComboBox<String> periodeComboBox;
   

    private RiwayatBlokirList riwayatList;
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");
    private final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupButtonActions();
        setupTableSelectionListener();
        setupPeriodComboBox();
    }

    private void setupPeriodComboBox() {
        periodeComboBox.setItems(FXCollections.observableArrayList(
            "Harian (Per Jam)",
            "Harian (Per Hari)", 
            "Mingguan",
            "Bulanan"
        ));
        periodeComboBox.getSelectionModel().select(1); // Default: Harian (Per Hari)
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
        btnKembali.setOnAction(this::handleKembali);
    }

    private void setupTableSelectionListener() {
        riwayatTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    fillFieldsFromSelectedRow(newSelection);
                }
            });
    }

    public void setRiwayatList(RiwayatBlokirList riwayatList) {
        this.riwayatList = riwayatList;
        riwayatTable.setItems(riwayatList.getData());
        updateChart();
    }

    private void updateChart() {
        durasiChart.getData().clear();
        String selectedPeriod = periodeComboBox.getSelectionModel().getSelectedItem();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Durasi Blokir");

        switch (selectedPeriod) {
            case "Harian (Per Jam)":
                updateHourlyChart(series);
                durasiChart.setTitle("Durasi Blokir Per Jam");
                break;
            case "Harian (Per Hari)":
                updateDailyChart(series);
                durasiChart.setTitle("Durasi Blokir Per Hari");
                break;
            case "Mingguan":
                updateWeeklyChart(series);
                durasiChart.setTitle("Durasi Blokir Per Minggu");
                break;
            case "Bulanan":
                updateMonthlyChart(series);
                durasiChart.setTitle("Durasi Blokir Per Bulan");
                break;
        }

        durasiChart.getData().add(series);
        styleChartBars(series, "#f38f04ff");
    }

    private void updateHourlyChart(XYChart.Series<String, Number> series) {
        Map<String, Integer> hourStats = riwayatList.getData().stream()
            .collect(Collectors.groupingBy(
                r -> {
                    LocalDateTime dateTime = LocalDateTime.parse(r.getTanggalMulai(), inputFormatter);
                    return dateTime.format(hourFormatter);
                },
                Collectors.summingInt(RiwayatBlokir::getDurasi)
            ));

        hourStats.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>(entry.getKey(), entry.getValue())
            ));
    }

     private void updateDailyChart(XYChart.Series<String, Number> series) {
        Map<LocalDate, Integer> dailyStats = riwayatList.getData().stream()
            .collect(Collectors.groupingBy(
                r -> LocalDate.parse(r.getTanggalMulai().split(" ")[0], 
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                Collectors.summingInt(RiwayatBlokir::getDurasi)
            ));
        dailyStats.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>(entry.getKey().format(dayFormatter), entry.getValue())
            ));
    }    

    private void updateWeeklyChart(XYChart.Series<String, Number> series) {
        Map<YearWeek, Integer> weeklyStats = riwayatList.getData().stream()
            .collect(Collectors.groupingBy(
                r -> {
                    LocalDate date = LocalDate.parse(r.getTanggalMulai().split(" ")[0], 
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return YearWeek.from(date);
                },
                Collectors.summingInt(RiwayatBlokir::getDurasi)
            ));

        weeklyStats.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>("Minggu " + entry.getKey().week() + "/" + entry.getKey().year(), 
                                 entry.getValue())
            ));
    }

    private void updateMonthlyChart(XYChart.Series<String, Number> series) {
        Map<YearMonth, Integer> monthlyStats = riwayatList.getData().stream()
            .collect(Collectors.groupingBy(
                r -> {
                    LocalDate date = LocalDate.parse(r.getTanggalMulai().split(" ")[0], 
                        DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return YearMonth.from(date);
                },
                Collectors.summingInt(RiwayatBlokir::getDurasi)
            ));

        monthlyStats.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(
                new XYChart.Data<>(entry.getKey().format(monthFormatter), entry.getValue())
            ));
    }

    private static class YearWeek implements Comparable<YearWeek> {
        private final int year;
        private final int week;

        public YearWeek(int year, int week) {
            this.year = year;
            this.week = week;
        }

        public static YearWeek from(LocalDate date) {
            return new YearWeek(date.getYear(), date.get(WeekFields.ISO.weekOfYear()));
        }

        public int year() { return year; }
        public int week() { return week; }

        @Override
        public int compareTo(YearWeek other) {
            if (this.year != other.year) {
                return Integer.compare(this.year, other.year);
            }
            return Integer.compare(this.week, other.week);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            YearWeek yearWeek = (YearWeek) o;
            return year == yearWeek.year && week == yearWeek.week;
        }

        @Override
        public int hashCode() {
            return Objects.hash(year, week);
        }
    }

    private void styleChartBars(XYChart.Series<String, Number> series, String color) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-bar-fill: " + color + ";");
            }
        }
    }

    private void fillFieldsFromSelectedRow(RiwayatBlokir selectedRow) {
        nomorField.setText(String.valueOf(selectedRow.getNomor()));
        aktivitasBaruField.setText(selectedRow.getAktivitas());
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        try {
            int nomor = Integer.parseInt(nomorField.getText().trim());
            String aktivitasBaru = aktivitasBaruField.getText().trim();

            if (nomor <= 0 || nomor > riwayatList.getData().size()) {
                showAlert("Error", "Nomor riwayat tidak valid!");
                return;
            }

            if (aktivitasBaru.isEmpty()) {
                showAlert("Error", "Aktivitas baru tidak boleh kosong!");
                return;
            }

            riwayatList.editAktivitas(nomor, aktivitasBaru);
            showAlert("Success", "Aktivitas berhasil diubah!");
            clearFields();

        } catch (NumberFormatException e) {
            showAlert("Error", "Nomor harus berupa angka!");
        }
    }

    @FXML
    private void handleKembali(ActionEvent event) {
        Stage currentStage = (Stage) btnKembali.getScene().getWindow();
        currentStage.close();
    }

    private void clearFields() {
        nomorField.clear();
        aktivitasBaruField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}