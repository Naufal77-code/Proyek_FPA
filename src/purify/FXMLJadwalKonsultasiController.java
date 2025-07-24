package purify;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller untuk form penjadwalan konsultasi dengan psikolog.
 * Mengatur input pengguna untuk memilih tanggal, waktu, dan lokasi konsultasi.
 */
public class FXMLJadwalKonsultasiController implements Initializable {

    // Label untuk menampilkan nama dan spesialisasi psikolog yang dipilih
    @FXML
    private Label psikologNamaLabel;

    // Komponen untuk memilih tanggal konsultasi
    @FXML
    private DatePicker datePicker;

    // Inputan waktu konsultasi
    @FXML
    private TextField timeField;

    // Inputan lokasi konsultasi
    @FXML
    private TextField locationField;

    // Tombol untuk menyimpan jadwal konsultasi
    @FXML
    private Button btnSimpanJadwal;

    // Tombol untuk membatalkan proses penjadwalan
    @FXML
    private Button btnBatal;

    // Objek psikolog yang sedang dipilih untuk dijadwalkan konsultasinya
    private Psikolog selectedPsikolog;

    // Referensi ke controller utama (FXMLKonsultasiPsikologController) untuk update
    // data setelah perubahan
    private FXMLKonsultasiPsikologController mainController;

    // Objek untuk mengelola daftar jadwal konsultasi
    private AppointmentList appointmentList;

    /**
     * Method yang dipanggil otomatis saat controller diinisialisasi.
     * Digunakan untuk set event handler tombol dan inisialisasi daftar jadwal.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inisialisasi objek daftar janji temu
        appointmentList = new AppointmentList();

        // Set event handler untuk tombol simpan jika tidak null
        if (btnSimpanJadwal != null) {
            btnSimpanJadwal.setOnAction(this::handleSimpanJadwal);
        }

        // Set event handler untuk tombol batal jika tidak null
        if (btnBatal != null) {
            btnBatal.setOnAction(this::handleBatal);
        }
    }

    /**
     * Setter untuk mengatur psikolog yang dipilih.
     * Juga memperbarui label di UI dengan informasi psikolog tersebut.
     */
    public void setSelectedPsikolog(Psikolog psikolog) {
        this.selectedPsikolog = psikolog;

        // Jika label tersedia, tampilkan nama dan spesialisasi psikolog
        if (psikologNamaLabel != null) {
            psikologNamaLabel.setText(
                    "Jadwalkan Konsultasi dengan " + psikolog.getNama() + " (" + psikolog.getSpesialisasi() + ")");
        }
    }

    /**
     * Setter untuk memberikan referensi ke controller utama agar bisa menyegarkan
     * data setelah penjadwalan.
     */
    public void setMainController(FXMLKonsultasiPsikologController controller) {
        this.mainController = controller;
    }

    /**
     * Handler saat tombol "Simpan Jadwal" diklik.
     * Melakukan validasi input, menyimpan jadwal, dan kembali ke layar sebelumnya.
     */
    @FXML
    private void handleSimpanJadwal(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeField.getText().trim();
        String selectedLocation = locationField.getText().trim();

        // Validasi apakah psikolog dipilih
        if (selectedPsikolog == null) {
            showAlert("Error", "Psikolog belum dipilih.");
            return;
        }

        // Validasi input jadwal
        if (selectedDate == null || selectedTime.isEmpty() || selectedLocation.isEmpty()) {
            showAlert("Peringatan", "Mohon lengkapi semua detail jadwal.");
            return;
        }

        // Format tanggal ke dalam bentuk yang rapi
        String tanggalJadwal = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Buat dan simpan janji temu baru
        Appointment newAppointment = new Appointment(
                selectedPsikolog.getNama(),
                tanggalJadwal,
                selectedTime,
                selectedLocation,
                "Terjadwal");
        appointmentList.addAppointment(newAppointment);

        showAlert("Sukses", "Jadwal berhasil disimpan!");

        try {
            // Refresh data di controller utama jika tersedia
            if (mainController != null) {
                mainController.refreshPsikologTable();
            }

            // Kembali ke scene utama konsultasi
            Stage currentStage = (Stage) btnSimpanJadwal.getScene().getWindow();
            currentStage.setScene(
                    new Scene(FXMLLoader.load(getClass().getResource("/purify/FXMLKonsultasiPsikolog.fxml"))));
            currentStage.setTitle("Konsultasi dengan Psikolog");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }

    /**
     * Handler saat tombol "Batal" diklik.
     * Kembali ke scene sebelumnya tanpa menyimpan data.
     */
    @FXML
    private void handleBatal(ActionEvent event) {
        try {
            // Refresh data jika controller utama tersedia
            if (mainController != null) {
                mainController.refreshPsikologTable();
            }

            // Kembali ke halaman konsultasi utama
            Stage currentStage = (Stage) btnBatal.getScene().getWindow();
            currentStage.setScene(
                    new Scene(FXMLLoader.load(getClass().getResource("/purify/FXMLKonsultasiPsikolog.fxml"))));
            currentStage.setTitle("Konsultasi dengan Psikolog");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }

    /**
     * Menampilkan popup alert ke layar dengan pesan tertentu.
     * 
     * @param title   Judul dari alert
     * @param message Isi pesan
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
