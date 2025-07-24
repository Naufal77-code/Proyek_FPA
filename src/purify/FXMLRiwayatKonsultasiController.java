package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLRiwayatKonsultasiController implements Initializable {

    // Tabel yang menampilkan riwayat chat konsultasi pengguna dengan psikolog
    @FXML
    private TableView<ChatRecord> chatHistoryTable;

    // Kolom yang menampilkan nama psikolog dalam tabel riwayat chat
    @FXML
    private TableColumn<ChatRecord, String> colChatPsikologNama;

    // Kolom yang menampilkan waktu mulai chat dalam tabel riwayat chat
    @FXML
    private TableColumn<ChatRecord, String> colChatWaktuMulai;

    // Kolom yang menampilkan ringkasan chat antara pengguna dan psikolog
    @FXML
    private TableColumn<ChatRecord, String> colChatRingkasan;

    // Tabel yang menampilkan jadwal konsultasi psikolog pengguna
    @FXML
    private TableView<Appointment> appointmentTable;

    // Kolom yang menampilkan nama psikolog dalam tabel jadwal konsultasi
    @FXML
    private TableColumn<Appointment, String> colJadwalPsikologNama;

    // Kolom yang menampilkan tanggal konsultasi
    @FXML
    private TableColumn<Appointment, String> colJadwalTanggal;

    // Kolom yang menampilkan waktu konsultasi
    @FXML
    private TableColumn<Appointment, String> colJadwalWaktu;

    // Kolom yang menampilkan lokasi konsultasi
    @FXML
    private TableColumn<Appointment, String> colJadwalLokasi;

    // Kolom yang menampilkan status konsultasi (misalnya: selesai, dijadwalkan,
    // dibatalkan)
    @FXML
    private TableColumn<Appointment, String> colJadwalStatus;

    // Tombol untuk kembali ke layar sebelumnya (halaman konsultasi utama)
    @FXML
    private Button btnKembali;

    // Objek untuk memuat dan mengelola data riwayat chat konsultasi
    private ChatHistoryList chatHistoryList;

    // Objek untuk memuat dan mengelola data jadwal konsultasi
    private AppointmentList appointmentList;

    /**
     * Method ini dipanggil otomatis saat controller diinisialisasi.
     * Bertugas menyiapkan data dan menghubungkan kolom tabel dengan properti
     * masing-masing.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatHistoryList = new ChatHistoryList();
        appointmentList = new AppointmentList();

        // Menghubungkan kolom riwayat chat dengan atribut di ChatRecord
        colChatPsikologNama.setCellValueFactory(new PropertyValueFactory<>("psikologNama"));
        colChatWaktuMulai.setCellValueFactory(new PropertyValueFactory<>("waktuMulaiChat"));
        colChatRingkasan.setCellValueFactory(new PropertyValueFactory<>("ringkasanChat"));
        chatHistoryTable.setItems(chatHistoryList.getData());

        // Menghubungkan kolom jadwal konsultasi dengan atribut di Appointment
        colJadwalPsikologNama.setCellValueFactory(new PropertyValueFactory<>("psikologNama"));
        colJadwalTanggal.setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        colJadwalWaktu.setCellValueFactory(new PropertyValueFactory<>("waktu"));
        colJadwalLokasi.setCellValueFactory(new PropertyValueFactory<>("lokasi"));
        colJadwalStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        appointmentTable.setItems(appointmentList.getData());

        // Menetapkan aksi untuk tombol kembali
        btnKembali.setOnAction(this::handleKembali);
    }

    /**
     * Method untuk menangani aksi tombol kembali.
     * Akan membuka kembali tampilan FXMLKonsultasiPsikolog.fxml.
     */
    @FXML
    private void handleKembali(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLKonsultasiPsikolog.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) btnKembali.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Konsultasi dengan Psikolog");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }

    /**
     * Method untuk menampilkan alert sederhana.
     * Berguna untuk menampilkan pesan kesalahan atau informasi ke pengguna.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}