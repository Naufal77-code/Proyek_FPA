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

public class FXMLKonsultasiPsikologController implements Initializable {

    // Tabel yang menampilkan daftar psikolog
    @FXML
    private TableView<Psikolog> psikologTable;

    // Kolom tabel yang menampilkan nama psikolog
    @FXML
    private TableColumn<Psikolog, String> colNama;

    // Kolom tabel yang menampilkan spesialisasi psikolog
    @FXML
    private TableColumn<Psikolog, String> colSpesialisasi;

    // Kolom tabel yang menampilkan status (misal: Online, Offline)
    @FXML
    private TableColumn<Psikolog, String> colStatus;

    // Tombol untuk memulai chat dengan psikolog yang dipilih
    @FXML
    private Button btnMulaiChat;

    // Tombol untuk menjadwalkan konsultasi dengan psikolog
    @FXML
    private Button btnJadwalkan;

    // Tombol untuk melihat riwayat konsultasi pengguna
    @FXML
    private Button btnLihatRiwayatKonsultasi;

    // Tombol untuk kembali ke main menu
    @FXML
    private Button btnKembali;

    // Objek yang menyimpan daftar psikolog dan datanya
    private PsikologList psikologList;

    /**
     * Method yang otomatis dipanggil saat scene dimuat.
     * Bertugas menginisialisasi tabel dan mengatur listener tombol.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Menghubungkan kolom tabel ke properti dari objek Psikolog
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colSpesialisasi.setCellValueFactory(new PropertyValueFactory<>("spesialisasi"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Inisialisasi dan muat data default psikolog
        psikologList = new PsikologList();
        psikologList.initializeDefaultPsikolog();

        // Tampilkan data ke tabel
        psikologTable.setItems(psikologList.getData());

        // Atur aksi tombol-tombol
        btnMulaiChat.setOnAction(this::handleMulaiChat);
        btnJadwalkan.setOnAction(this::handleJadwalkanKonsultasi);
        btnLihatRiwayatKonsultasi.setOnAction(this::handleLihatRiwayatKonsultasi);
        btnKembali.setOnAction(this::handleKembali);

        // Nonaktifkan tombol chat & jadwal saat belum ada psikolog yang dipilih
        btnMulaiChat.setDisable(true);
        btnJadwalkan.setDisable(true);

        // Aktifkan tombol chat & jadwal hanya jika baris pada tabel dipilih
        psikologTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean selected = newSelection != null;
            btnMulaiChat.setDisable(!selected);
            btnJadwalkan.setDisable(!selected);
        });
    }

    /**
     * Handler ketika pengguna ingin memulai chat dengan psikolog.
     * Akan membuka scene chat dan mengoper objek psikolog yang dipilih.
     */
    @FXML
    private void handleMulaiChat(ActionEvent event) {
        Psikolog selectedPsikolog = psikologTable.getSelectionModel().getSelectedItem();

        if (selectedPsikolog == null) {
            showAlert("Peringatan", "Pilih psikolog terlebih dahulu untuk memulai chat.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLChatWindow.fxml"));
            Parent root = loader.load();

            // Oper data ke controller chat
            FXMLChatWindowController chatController = loader.getController();
            chatController.setSelectedPsikolog(selectedPsikolog);
            chatController.setMainController(this);

            // Ganti scene ke jendela chat
            Stage currentStage = (Stage) btnMulaiChat.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Chat dengan " + selectedPsikolog.getNama());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka jendela chat: " + e.getMessage());
        }
    }

    /**
     * Handler untuk menjadwalkan konsultasi dengan psikolog.
     * Membuka jendela penjadwalan dan oper data psikolog ke controller terkait.
     */
    @FXML
    private void handleJadwalkanKonsultasi(ActionEvent event) {
        Psikolog selectedPsikolog = psikologTable.getSelectionModel().getSelectedItem();

        if (selectedPsikolog == null) {
            showAlert("Peringatan", "Pilih psikolog terlebih dahulu untuk menjadwalkan konsultasi.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLJadwalKonsultasi.fxml"));
            Parent root = loader.load();

            FXMLJadwalKonsultasiController jadwalController = loader.getController();
            jadwalController.setSelectedPsikolog(selectedPsikolog);
            jadwalController.setMainController(this);

            Stage currentStage = (Stage) btnJadwalkan.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Jadwalkan Konsultasi dengan " + selectedPsikolog.getNama());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka jendela jadwal konsultasi: " + e.getMessage());
        }
    }

    /**
     * Handler untuk membuka halaman riwayat konsultasi pengguna.
     */
    @FXML
    private void handleLihatRiwayatKonsultasi(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLRiwayatKonsultasi.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) btnLihatRiwayatKonsultasi.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Digital Detox - Riwayat Konsultasi");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman riwayat konsultasi: " + e.getMessage());
        }
    }

    /**
     * Handler untuk kembali ke halaman main menu dari halaman konsultasi psikolog.
     */
    @FXML
    private void handleKembali(ActionEvent event) {
        Stage currentStage = (Stage) btnKembali.getScene().getWindow();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLMainMenu.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Purify - Digital Detox");
            stage.show();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Error", "Gagal kembali ke main menu: " + e.getMessage());
        }
    }

    /**
     * Menampilkan popup alert sederhana dengan judul dan isi pesan.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Method untuk menyegarkan ulang isi tabel psikolog.
     * Biasanya dipanggil setelah update data (misalnya: setelah menjadwalkan
     * konsultasi).
     */
    public void refreshPsikologTable() {
        psikologList.saveToXML(); // Simpan data terbaru ke file
        psikologTable.setItems(psikologList.getData()); // Tampilkan ulang data
        psikologTable.refresh(); // Paksa refresh tampilan tabel
    }
}
