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

    // Line 35: Komponen chart batang untuk menampilkan durasi pemblokiran
    @FXML
    private BarChart<String, Number> durasiChart;

    // Line 39: Tabel untuk menampilkan riwayat pemblokiran
    @FXML
    private TableView<RiwayatBlokir> riwayatTable;

    // Line 43: Dropdown untuk memilih periode tampilan statistik
    @FXML
    private ComboBox<String> periodeComboBox;

    // Line 47: Kolom untuk nomor riwayat pemblokiran
    @FXML
    private TableColumn<RiwayatBlokir, Integer> colNomor;
    // Line 49: Kolom untuk tanggal mulai pemblokiran
    @FXML
    private TableColumn<RiwayatBlokir, String> colTanggal;
    // Line 51: Kolom untuk durasi pemblokiran
    @FXML
    private TableColumn<RiwayatBlokir, Integer> colDurasi;
    // Line 53: Kolom untuk status pemblokiran
    @FXML
    private TableColumn<RiwayatBlokir, String> colStatus;
    // Line 55: Kolom untuk aktivitas yang diblokir
    @FXML
    private TableColumn<RiwayatBlokir, String> colAktivitas;

    // Line 59: Field input untuk nomor riwayat yang akan diedit/dihapus
    @FXML
    private TextField nomorField;
    // Line 61: Field input untuk aktivitas baru saat mengedit riwayat
    @FXML
    private TextField aktivitasBaruField;

    // Line 65: Tombol untuk mengedit riwayat pemblokiran
    @FXML
    private Button btnEdit;
    // Line 67: Tombol untuk menghapus riwayat pemblokiran
    @FXML
    private Button btnDelete;
    // Line 69: Tombol untuk kembali ke layar sebelumnya
    @FXML
    private Button btnKembali;
    // Line 71: Tombol untuk menuju layar statistik aplikasi
    @FXML
    private Button btnStatistikApps;

    // Line 75: Kontainer HBox untuk navigasi tampilan harian (sebelumnya/berikutnya)
    @FXML
    private HBox dayNavigationBox;
    // Line 77: Tombol untuk melihat statistik hari sebelumnya
    @FXML
    private Button btnPreviousDay;
    // Line 79: Tombol untuk melihat statistik hari berikutnya
    @FXML
    private Button btnNextDay;
    // Line 81: Label untuk menampilkan tanggal yang sedang dilihat dalam tampilan harian
    @FXML
    private Label dayLabel;

    // Line 85: Kontainer HBox untuk navigasi tampilan mingguan (sebelumnya/berikutnya)
    @FXML
    private HBox weekNavigationBox;
    // Line 87: Tombol untuk melihat statistik minggu sebelumnya
    @FXML
    private Button btnPreviousWeek;
    // Line 89: Tombol untuk melihat statistik minggu berikutnya
    @FXML
    private Button btnNextWeek;
    // Line 91: Label untuk menampilkan rentang minggu yang sedang dilihat dalam tampilan mingguan
    @FXML
    private Label weekRangeLabel;

    // Line 95: Kontainer HBox untuk navigasi tampilan tahunan/bulanan (sebelumnya/berikutnya)
    @FXML
    private HBox yearNavigationBox;
    // Line 97: Tombol untuk melihat statistik tahun sebelumnya
    @FXML
    private Button btnPreviousYear;
    // Line 99: Tombol untuk melihat statistik tahun berikutnya
    @FXML
    private Button btnNextYear;
    // Line 101: Label untuk menampilkan tahun yang sedang dilihat dalam tampilan bulanan
    @FXML
    private Label yearLabel;

    // Line 105: Sumbu X pada chart batang (kategori seperti jam, hari, bulan)
    @FXML
    private CategoryAxis dayAxis;
    // Line 107: Sumbu Y pada chart batang (nilai durasi)
    @FXML
    private NumberAxis durationAxis;

    // Line 110: Objek yang menyimpan dan mengelola daftar riwayat pemblokiran
    private RiwayatBlokirList riwayatList;

    // Line 113: Tanggal saat ini yang sedang ditampilkan pada chart dan navigasi
    private LocalDate currentDisplayDate;

    // Line 116: Nama layar FXML sebelumnya dari mana layar ini diakses
    private String previousScreen;
    // Line 118: Objek Stage dari layar sebelumnya, digunakan untuk kembali
    private Stage previousStage;

    // Line 121: Formatter untuk menampilkan tanggal lengkap (e.g., "25 Juli 2025")
    private final DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy",
            new Locale("id", "ID"));
    // Line 124: Formatter untuk menampilkan tanggal singkat (e.g., "25/07")
    private final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM");
    // Line 126: Formatter untuk menampilkan jam dalam format "HH:00"
    private final DateTimeFormatter simpleHourFormatter = DateTimeFormatter.ofPattern("HH:00");
    // Line 128: Formatter untuk parsing input tanggal dan waktu (e.g., "dd/MM/yyyy HH:mm")
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Line 133: Dipanggil otomatis saat FXML diload. Menyiapkan semua komponen awal.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param rb The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Line 137: Set tanggal tampilan awal ke hari ini
        currentDisplayDate = LocalDate.now();
        // Line 138: Atur kolom tabel
        setupTableColumns();
        // Line 139: Isi dan atur pilihan periode
        setupPeriodComboBox();
        // Line 140: Atur aksi dari tombol-tombol
        setupButtonActions();
        // Line 141: Atur listener ketika baris tabel dipilih
        setupTableSelectionListener();
    }

    /**
     * Line 146: Setter untuk data utama (daftar riwayat blokir) dan informasi layar sebelumnya.
     * Digunakan untuk menginisialisasi controller setelah FXML dimuat.
     *
     * @param riwayatList Objek RiwayatBlokirList yang berisi data riwayat pemblokiran.
     * @param previousScreen Nama layar sebelumnya (e.g., "mainMenu").
     * @param previousStage Stage dari layar sebelumnya untuk navigasi kembali.
     */
    public void setRiwayatList(RiwayatBlokirList riwayatList, String previousScreen, Stage previousStage) {
        // Line 151: Inisialisasi objek riwayatList dengan data yang diberikan
        this.riwayatList = riwayatList;
        // Line 152: Simpan nama layar sebelumnya
        this.previousScreen = previousScreen;
        // Line 153: Simpan objek Stage dari layar sebelumnya
        this.previousStage = previousStage;
        // Line 154: Tampilkan data ke tabel
        riwayatTable.setItems(riwayatList.getData());
        // Line 155: Update chart sesuai data dan tanggal
        updateChart();
    }

    /**
     * Line 160: Setup isi pilihan pada ComboBox periode dan event-nya.
     * Pilihan yang tersedia adalah "Harian (Per Jam)", "Mingguan", dan "Bulanan".
     */
    private void setupPeriodComboBox() {
        // Line 163: Mengisi ComboBox dengan pilihan periode
        periodeComboBox.setItems(FXCollections.observableArrayList("Harian (Per Jam)", "Mingguan", "Bulanan"));
        // Line 164: Mengatur pilihan default ke "Harian (Per Jam)"
        periodeComboBox.getSelectionModel().select("Harian (Per Jam)");
        // Line 165: Menambahkan listener untuk memperbarui chart saat pilihan periode berubah
        periodeComboBox.setOnAction(e -> updateChart());
    }

    /**
     * Line 170: Setup kolom-kolom pada tabel dengan property dari RiwayatBlokir.
     * Memetakan kolom tabel ke properti yang sesuai di kelas RiwayatBlokir.
     */
    private void setupTableColumns() {
        // Line 173: Mengatur nilai sel untuk kolom Nomor dari properti "nomor" di RiwayatBlokir
        colNomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        // Line 175: Mengatur nilai sel untuk kolom Tanggal dari properti "tanggalMulai" di RiwayatBlokir
        colTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggalMulai"));
        // Line 177: Mengatur nilai sel untuk kolom Durasi dari properti "durasi" di RiwayatBlokir
        colDurasi.setCellValueFactory(new PropertyValueFactory<>("durasi"));
        // Line 179: Mengatur nilai sel untuk kolom Status dari properti "status" di RiwayatBlokir
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Line 181: Mengatur nilai sel untuk kolom Aktivitas dari properti "aktivitas" di RiwayatBlokir
        colAktivitas.setCellValueFactory(new PropertyValueFactory<>("aktivitas"));
    }

    /**
     * Line 186: Setup aksi setiap tombol di layar.
     * Menghubungkan setiap tombol dengan metode handler yang sesuai.
     */
    private void setupButtonActions() {
        // Line 189: Mengatur aksi untuk tombol Edit
        btnEdit.setOnAction(this::handleEdit);
        // Line 191: Mengatur aksi untuk tombol Delete
        btnDelete.setOnAction(this::handleDelete);
        // Line 193: Mengatur aksi untuk tombol Kembali
        btnKembali.setOnAction(this::handleKembali);
        // Line 195: Mengatur aksi untuk tombol Statistik Apps
        btnStatistikApps.setOnAction(this::handleStatistikApps);
        // Line 197: Mengatur aksi untuk tombol Hari Sebelumnya
        btnPreviousDay.setOnAction(e -> navigateDay(-1));
        // Line 199: Mengatur aksi untuk tombol Hari Berikutnya
        btnNextDay.setOnAction(e -> navigateDay(1));
        // Line 201: Mengatur aksi untuk tombol Minggu Sebelumnya
        btnPreviousWeek.setOnAction(e -> navigateWeek(-1));
        // Line 203: Mengatur aksi untuk tombol Minggu Berikutnya
        btnNextWeek.setOnAction(e -> navigateWeek(1));
        // Line 205: Mengatur aksi untuk tombol Tahun Sebelumnya
        btnPreviousYear.setOnAction(e -> navigateYear(-1));
        // Line 207: Mengatur aksi untuk tombol Tahun Berikutnya
        btnNextYear.setOnAction(e -> navigateYear(1));
    }

    /**
     * Line 212: Setup listener ketika baris tabel dipilih agar field input terisi otomatis.
     * Saat pengguna memilih baris di tabel, nilai 'nomor' dan 'aktivitas' akan mengisi field input yang relevan.
     */
    private void setupTableSelectionListener() {
        // Line 215: Menambahkan listener ke properti item yang dipilih di tabel
        riwayatTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    // Line 217: Memeriksa apakah ada baris baru yang dipilih
                    if (newSelection != null) {
                        // Line 219: Mengisi nomorField dengan nomor riwayat yang dipilih
                        nomorField.setText(String.valueOf(newSelection.getNomor()));
                        // Line 221: Mengisi aktivitasBaruField dengan aktivitas dari riwayat yang dipilih
                        aktivitasBaruField.setText(newSelection.getAktivitas());
                    }
                });
    }

    // Line 227: Menavigasi tampilan harian: menambahkan atau mengurangi hari dari tanggal yang sedang ditampilkan.
    // @param direction Arah navigasi: -1 untuk hari sebelumnya, 1 untuk hari berikutnya.
    private void navigateDay(int direction) {
        // Line 229: Memperbarui currentDisplayDate dengan menambah atau mengurangi hari
        currentDisplayDate = currentDisplayDate.plusDays(direction);
        // Line 231: Memperbarui chart setelah navigasi
        updateChart();
    }

    // Line 234: Menavigasi tampilan mingguan: menambahkan atau mengurangi minggu dari tanggal yang sedang ditampilkan.
    // @param direction Arah navigasi: -1 untuk minggu sebelumnya, 1 untuk minggu berikutnya.
    private void navigateWeek(int direction) {
        // Line 236: Memperbarui currentDisplayDate dengan menambah atau mengurangi minggu
        currentDisplayDate = currentDisplayDate.plusWeeks(direction);
        // Line 238: Memperbarui chart setelah navigasi
        updateChart();
    }

    // Line 241: Menavigasi tampilan tahunan/bulanan: menambahkan atau mengurangi tahun dari tanggal yang sedang ditampilkan.
    // @param direction Arah navigasi: -1 untuk tahun sebelumnya, 1 untuk tahun berikutnya.
    private void navigateYear(int direction) {
        // Line 243: Memperbarui currentDisplayDate dengan menambah atau mengurangi tahun
        currentDisplayDate = currentDisplayDate.plusYears(direction);
        // Line 245: Memperbarui chart setelah navigasi
        updateChart();
    }

    /**
     * Line 250: Menentukan jenis chart yang akan ditampilkan berdasarkan periode yang dipilih di ComboBox.
     * Mengatur visibilitas navigasi dan memperbarui data chart.
     */
    private void updateChart() {
        // Line 253: Memeriksa apakah riwayatList sudah diinisialisasi
        if (riwayatList == null)
            return;

        // Line 257: Menghapus semua data dari chart sebelum menambahkan yang baru
        durasiChart.getData().clear();
        // Line 258: Mendapatkan periode yang dipilih dari ComboBox
        String selectedPeriod = periodeComboBox.getSelectionModel().getSelectedItem();
        // Line 259: Membuat seri data baru untuk chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        // Line 261: Mengatur nama seri data
        series.setName("Total Durasi (menit)");

        // Line 264: Mengatur visibilitas kotak navigasi harian
        dayNavigationBox.setVisible(selectedPeriod.equals("Harian (Per Jam)"));
        // Line 265: Mengatur apakah kotak navigasi harian dikelola oleh tata letak
        dayNavigationBox.setManaged(selectedPeriod.equals("Harian (Per Jam)"));
        // Line 266: Mengatur visibilitas kotak navigasi mingguan
        weekNavigationBox.setVisible(selectedPeriod.equals("Mingguan"));
        // Line 267: Mengatur apakah kotak navigasi mingguan dikelola oleh tata letak
        weekNavigationBox.setManaged(selectedPeriod.equals("Mingguan"));
        // Line 268: Mengatur visibilitas kotak navigasi tahunan/bulanan
        yearNavigationBox.setVisible(selectedPeriod.equals("Bulanan"));
        // Line 269: Mengatur apakah kotak navigasi tahunan/bulanan dikelola oleh tata letak
        yearNavigationBox.setManaged(selectedPeriod.equals("Bulanan"));

        // Line 272: Memperbarui data chart sesuai dengan periode yang dipilih
        switch (selectedPeriod) {
            case "Harian (Per Jam)":
                // Line 274: Memanggil metode untuk memperbarui chart harian
                updateHourlyChart(series);
                break;
            case "Mingguan":
                // Line 277: Memanggil metode untuk memperbarui chart mingguan
                updateWeeklyChart(series);
                break;
            case "Bulanan":
                // Line 280: Memanggil metode untuk memperbarui chart bulanan
                updateMonthlyChart(series);
                break;
        }

        // Line 284: Menambahkan seri data ke chart
        durasiChart.getData().add(series);
    }

    // Line 288: Mengisi chart dengan data per jam (harian) untuk tanggal yang sedang ditampilkan.
    // @param series Seri data XYChart tempat data akan ditambahkan.
    private void updateHourlyChart(XYChart.Series<String, Number> series) {
        // Line 290: Mengatur label sumbu X untuk chart harian
        dayAxis.setLabel("Jam");
        // Line 292: Mengatur teks label hari dengan tanggal saat ini yang diformat
        dayLabel.setText(currentDisplayDate.format(fullDateFormatter));

        // Line 294: Mengelompokkan data riwayat pemblokiran berdasarkan jam dalam sehari
        Map<Integer, Integer> hourlyData = riwayatList.getData().stream()
                // Line 296: Memfilter riwayat yang terjadi pada currentDisplayDate
                .filter(r -> LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")).equals(currentDisplayDate))
                // Line 299: Mengumpulkan data dengan mengelompokkan berdasarkan jam dan menjumlahkan durasi
                .collect(Collectors.groupingBy(
                        r -> LocalTime.parse(r.getTanggalMulai().split(" ")[1],
                                DateTimeFormatter.ofPattern("HH:mm")).getHour(),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));

        // Line 305: Mengisi seri data untuk setiap jam dalam sehari (0-23)
        for (int hour = 0; hour < 24; hour++) {
            // Line 307: Membuat label jam (e.g., "08:00")
            String hourLabel = LocalTime.of(hour, 0).format(simpleHourFormatter);
            // Line 309: Menambahkan data ke seri chart (jam dan total durasi untuk jam tersebut)
            series.getData().add(new XYChart.Data<>(hourLabel, hourlyData.getOrDefault(hour, 0)));
        }
    }

    // Line 314: Mengisi chart dengan data per hari dalam satu minggu yang mencakup currentDisplayDate.
    // @param series Seri data XYChart tempat data akan ditambahkan.
    private void updateWeeklyChart(XYChart.Series<String, Number> series) {
        // Line 316: Mengatur label sumbu X untuk chart mingguan
        dayAxis.setLabel("Hari dalam Seminggu");
        // Line 318: Mendapatkan tanggal hari Senin dari minggu yang mengandung currentDisplayDate
        LocalDate monday = currentDisplayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // Line 320: Mendapatkan tanggal hari Minggu dari minggu yang mengandung currentDisplayDate
        LocalDate sunday = currentDisplayDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        // Line 322: Mengatur label rentang minggu (e.g., "01/07 - 07/07")
        weekRangeLabel.setText(String.format("%s - %s", monday.format(dayFormatter), sunday.format(dayFormatter)));

        // Line 324: Mengelompokkan data riwayat pemblokiran berdasarkan hari dalam seminggu
        Map<DayOfWeek, Integer> weeklyData = riwayatList.getData().stream()
                // Line 326: Memfilter riwayat yang terjadi dalam rentang minggu yang ditentukan
                .filter(r -> {
                    LocalDate date = LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return !date.isBefore(monday) && !date.isAfter(sunday);
                })
                // Line 333: Mengumpulkan data dengan mengelompokkan berdasarkan DayOfWeek dan menjumlahkan durasi
                .collect(Collectors.groupingBy(
                        r -> LocalDate.parse(r.getTanggalMulai().split(" ")[0],
                                DateTimeFormatter.ofPattern("dd/MM/yyyy")).getDayOfWeek(),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));

        // Line 340: Mengisi seri data untuk setiap hari dalam seminggu (dari Senin sampai Minggu)
        for (DayOfWeek day : DayOfWeek.values()) {
            // Line 342: Mendapatkan nama hari dalam bahasa Indonesia
            String dayName = day.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            // Line 344: Menambahkan data ke seri chart (nama hari dan total durasi untuk hari tersebut)
            series.getData().add(new XYChart.Data<>(dayName, weeklyData.getOrDefault(day, 0)));
        }
    }

    // Line 349: Mengisi chart dengan data bulanan sepanjang tahun yang mencakup currentDisplayDate.
    // @param series Seri data XYChart tempat data akan ditambahkan.
    private void updateMonthlyChart(XYChart.Series<String, Number> series) {
        // Line 351: Mengatur label sumbu X untuk chart bulanan
        dayAxis.setLabel("Bulan");
        // Line 353: Mendapatkan tahun dari currentDisplayDate
        int year = currentDisplayDate.getYear();
        // Line 355: Mengatur label tahun
        yearLabel.setText(String.valueOf(year));

        // Line 357: Mengelompokkan data riwayat pemblokiran berdasarkan bulan dalam setahun
        Map<Month, Integer> monthlyData = riwayatList.getData().stream()
                // Line 359: Memfilter riwayat yang terjadi pada tahun yang sedang ditampilkan
                .filter(r -> Integer.parseInt(r.getTanggalMulai().split("/")[2].split(" ")[0]) == year)
                // Line 361: Mengumpulkan data dengan mengelompokkan berdasarkan Bulan dan menjumlahkan durasi
                .collect(Collectors.groupingBy(
                        r -> Month.of(Integer.parseInt(r.getTanggalMulai().split("/")[1])),
                        Collectors.summingInt(RiwayatBlokir::getDurasi)));

        // Line 367: Mengisi seri data untuk setiap bulan dalam setahun
        for (Month month : Month.values()) {
            // Line 369: Mendapatkan nama bulan dalam bahasa Indonesia
            String monthName = month.getDisplayName(TextStyle.FULL, new Locale("id", "ID"));
            // Line 371: Menambahkan data ke seri chart (nama bulan dan total durasi untuk bulan tersebut)
            series.getData().add(new XYChart.Data<>(monthName, monthlyData.getOrDefault(month, 0)));
        }
    }

    /**
     * Line 376: Event handler saat tombol edit ditekan.
     * Mengambil nomor dan aktivitas baru dari field input, kemudian memperbarui riwayat.
     * @param event Objek ActionEvent yang dihasilkan oleh aksi tombol.
     */
    @FXML
    private void handleEdit(ActionEvent event) {
        try {
            // Line 381: Mengambil nomor riwayat dari field input dan mengkonversinya menjadi integer
            int nomor = Integer.parseInt(nomorField.getText().trim());
            // Line 383: Mengambil aktivitas baru dari field input
            String aktivitasBaru = aktivitasBaruField.getText().trim();

            // Line 386: Memeriksa apakah aktivitas baru kosong
            if (aktivitasBaru.isEmpty()) {
                // Line 388: Menampilkan alert error jika aktivitas baru kosong
                showAlert("Error", "Aktivitas baru tidak boleh kosong!");
                return;
            }

            // Line 392: Memanggil metode untuk mengedit aktivitas pada riwayatList
            riwayatList.editAktivitas(nomor, aktivitasBaru);
            // Line 394: Menampilkan alert sukses
            showAlert("Sukses", "Aktivitas berhasil diubah!");
            // Line 395: Memperbarui tampilan tabel
            riwayatTable.refresh();
            // Line 396: Mengosongkan field input
            clearFields();
        } catch (NumberFormatException e) {
            // Line 398: Menangani kesalahan jika input nomor bukan angka
            showAlert("Error", "Nomor harus berupa angka!");
        } catch (IndexOutOfBoundsException e) {
            // Line 400: Menangani kesalahan jika nomor riwayat tidak valid (tidak ditemukan)
            showAlert("Error", "Nomor riwayat tidak valid!");
        }
    }

    /**
     * Line 406: Event handler saat tombol hapus ditekan.
     * Mengambil nomor dari field input, meminta konfirmasi, kemudian menghapus riwayat.
     * @param event Objek ActionEvent yang dihasilkan oleh aksi tombol.
     */
    @FXML
    private void handleDelete(ActionEvent event) {
        try {
            // Line 411: Mengambil nomor riwayat dari field input dan mengkonversinya menjadi integer
            int nomor = Integer.parseInt(nomorField.getText().trim());

            // Line 414: Membuat dialog konfirmasi hapus
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            // Line 416: Mengatur judul dialog
            confirmation.setTitle("Konfirmasi Hapus");
            // Line 418: Mengatur header teks dialog
            confirmation.setHeaderText("Anda akan menghapus riwayat nomor " + nomor);
            // Line 420: Mengatur isi pesan dialog
            confirmation.setContentText("Apakah Anda yakin?");

            // Line 422: Menampilkan dialog konfirmasi dan menunggu respons pengguna
            Optional<ButtonType> result = confirmation.showAndWait();
            // Line 424: Memeriksa apakah pengguna mengklik OK
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Line 426: Menghapus riwayat dari riwayatList
                riwayatList.remove(nomor);
                // Line 428: Menampilkan alert sukses
                showAlert("Sukses", "Riwayat berhasil dihapus!");
                // Line 429: Memperbarui chart setelah penghapusan
                updateChart();
                // Line 430: Mengosongkan field input
                clearFields();
            }
        } catch (NumberFormatException e) {
            // Line 433: Menangani kesalahan jika input nomor bukan angka
            showAlert("Error", "Nomor harus berupa angka!");
        } catch (IndexOutOfBoundsException e) {
            // Line 435: Menangani kesalahan jika nomor riwayat tidak valid (tidak ditemukan)
            showAlert("Error", "Nomor riwayat tidak valid!");
        }
    }

    /**
     * Line 441: Event handler saat tombol kembali ditekan.
     * Menavigasi kembali ke layar sebelumnya (main menu atau layar asal).
     * @param event Objek ActionEvent yang dihasilkan oleh aksi tombol.
     */
    @FXML
    private void handleKembali(ActionEvent event) {
        // Line 444: Mendapatkan Stage saat ini
        Stage currentStage = (Stage) btnKembali.getScene().getWindow();

        // Line 446: Memeriksa apakah layar sebelumnya adalah main menu
        if ("mainMenu".equals(previousScreen)) {
            try {
                // Line 448: Memuat FXMLMainMenu.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLMainMenu.fxml"));
                // Line 450: Memuat root Parent dari FXML
                Parent root = loader.load();
                // Line 451: Membuat Stage baru untuk main menu
                Stage stage = new Stage();
                // Line 453: Mengatur scene untuk Stage baru
                stage.setScene(new Scene(root));
                // Line 454: Mengatur judul Stage
                stage.setTitle("Purify - Main Menu");
                // Line 455: Menampilkan Stage main menu
                stage.show();
                // Line 456: Menutup Stage saat ini (statistik)
                currentStage.close();
            } catch (IOException e) {
                // Line 458: Menangani kesalahan jika gagal memuat main menu
                showAlert("Error", "Gagal kembali ke main menu");
            }
        } else if (previousStage != null) {
            // Line 461: Jika previousStage ada, tampilkan kembali previousStage
            previousStage.show();
            // Line 463: Menutup Stage saat ini (statistik)
            currentStage.close();
        }
    }

    /**
     * Line 469: Event handler saat tombol "Statistik Aplikasi" ditekan.
     * Membuka layar statistik aplikasi baru dan menyembunyikan layar saat ini.
     * @param event Objek ActionEvent yang dihasilkan oleh aksi tombol.
     */
    @FXML
    private void handleStatistikApps(ActionEvent event) {
        try {
            // Line 473: Memuat FXMLStatistikApps.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLStatistikApps.fxml"));
            // Line 475: Memuat root Parent dari FXML
            Parent root = loader.load();

            // Line 477: Mendapatkan controller dari FXMLStatistikApps
            FXMLStatistikAppsController controller = loader.getController();
            // Line 478: Mengatur Stage saat ini sebagai previousStage di controller Statistik Apps
            controller.setPreviousStage((Stage) btnStatistikApps.getScene().getWindow());

            // Line 480: Membuat Stage baru untuk Statistik Aplikasi
            Stage stage = new Stage();
            // Line 482: Mengatur scene untuk Stage baru
            stage.setScene(new Scene(root));
            // Line 483: Mengatur judul Stage
            stage.setTitle("Statistik Aplikasi");
            // Line 484: Menampilkan Stage Statistik Aplikasi
            stage.show();

            // Line 486: Menyembunyikan Stage saat ini (statistik riwayat)
            ((Stage) btnStatistikApps.getScene().getWindow()).hide();
        } catch (IOException e) {
            // Line 488: Menangani kesalahan jika gagal membuka statistik aplikasi
            showAlert("Error", "Gagal membuka statistik aplikasi");
            // Line 490: Mencetak stack trace untuk debugging
            e.printStackTrace();
        }
    }

    /**
     * Line 495: Menghapus isi field input nomor dan aktivitas, serta mereset seleksi tabel.
     */
    private void clearFields() {
        // Line 497: Mengosongkan field nomor
        nomorField.clear();
        // Line 499: Mengosongkan field aktivitas baru
        aktivitasBaruField.clear();
        // Line 501: Menghapus seleksi dari tabel
        riwayatTable.getSelectionModel().clearSelection();
    }

    /**
     * Line 505: Menampilkan alert dialog dengan judul dan pesan tertentu.
     * @param title Judul alert.
     * @param message Pesan yang akan ditampilkan di alert.
     */
    private void showAlert(String title, String message) {
        // Line 508: Membuat objek Alert baru dengan tipe INFORMASI
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        // Line 510: Mengatur judul alert
        alert.setTitle(title);
        // Line 512: Menghapus header teks (biasanya tidak diperlukan untuk alert informasi sederhana)
        alert.setHeaderText(null);
        // Line 514: Mengatur isi pesan alert
        alert.setContentText(message);
        // Line 516: Menampilkan alert dan menunggu hingga ditutup
        alert.showAndWait();
    }
}