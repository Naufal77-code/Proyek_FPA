package purify;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Random;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class FXMLChatWindowController implements Initializable {

    // Label untuk menampilkan nama dan spesialisasi psikolog
    @FXML
    private Label psikologNamaLabel;

    // Area teks untuk menampilkan percakapan antara pengguna dan psikolog
    @FXML
    private TextArea chatArea;

    // Field input tempat pengguna mengetik pesan yang ingin dikirim
    @FXML
    private TextField chatInputField;

    // Tombol untuk mengirim pesan ke psikolog
    @FXML
    private Button btnKirim;

    // Tombol untuk membuka jendela penjadwalan konsultasi tatap muka
    @FXML
    private Button btnJadwalkanTatapMukaDariChat;

    // Tombol untuk menyelesaikan sesi chat dan menyimpan riwayatnya
    @FXML
    private Button btnSelesaiChat;

    // Psikolog yang sedang dipilih untuk chat
    private Psikolog selectedPsikolog;

    // Referensi ke controller utama (FXMLKonsultasiPsikologController)
    private FXMLKonsultasiPsikologController mainController;

    // Objek untuk menyimpan dan mengelola riwayat chat
    private ChatHistoryList chatHistoryList;

    /**
     * Inisialisasi controller saat FXML dimuat.
     * Mengatur event handler tombol dan membuat objek riwayat chat.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatHistoryList = new ChatHistoryList();

        // Menetapkan aksi untuk masing-masing tombol jika tidak null
        if (btnKirim != null) {
            btnKirim.setOnAction(this::handleKirimPesan);
        }
        if (btnJadwalkanTatapMukaDariChat != null) {
            btnJadwalkanTatapMukaDariChat.setOnAction(this::handleJadwalkanTatapMuka);
        }
        if (btnSelesaiChat != null) {
            btnSelesaiChat.setOnAction(this::handleSelesaiChat);
        }
    }

    /**
     * Setter untuk menentukan psikolog yang sedang diajak chat.
     * Juga memperbarui label dan area chat dengan waktu mulai.
     */
    public void setSelectedPsikolog(Psikolog psikolog) {
        this.selectedPsikolog = psikolog;
        if (psikologNamaLabel != null) {
            psikologNamaLabel.setText("Chat dengan " + psikolog.getNama() + " (" + psikolog.getSpesialisasi() + ")");
            chatArea.appendText("Anda memulai chat dengan " + psikolog.getNama() + " pada " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".\n");
        }
    }

    /**
     * Setter untuk memberikan referensi controller utama agar bisa kembali setelah
     * selesai chat.
     */
    public void setMainController(FXMLKonsultasiPsikologController controller) {
        this.mainController = controller;
    }

    /**
     * Mengirim pesan dari pengguna ke area chat dan memberikan balasan otomatis
     * dari psikolog.
     */
    @FXML
    private void handleKirimPesan(ActionEvent event) {
        String pesan = chatInputField.getText().trim();
        if (!pesan.isEmpty()) {
            // Tampilkan pesan pengguna
            chatArea.appendText("Anda: " + pesan + "\n");
            chatInputField.clear();

            // Simulasikan balasan dari psikolog secara acak
            String[] balasanPsikolog = {
                    "Halo. Terima kasih sudah berbagi. Ada yang bisa saya bantu lebih lanjut?",
                    "Baik, saya mengerti. Mari kita bahas lebih dalam.",
                    "Saya mendengarkan. Apa yang ingin Anda sampaikan?",
                    "Oke. Ceritakan lebih banyak tentang hal itu.",
                    "Terima kasih atas pesannya. Apa yang sedang Anda rasakan saat ini?"
            };

            Random random = new Random();
            String balasanAcak = balasanPsikolog[random.nextInt(balasanPsikolog.length)];

            // Tampilkan balasan
            chatArea.appendText(selectedPsikolog.getNama() + ": " + balasanAcak + "\n");
        }
    }

    /**
     * Membuka jendela untuk menjadwalkan konsultasi tatap muka dengan psikolog.
     */
    @FXML
    private void handleJadwalkanTatapMuka(ActionEvent event) {
        if (selectedPsikolog == null) {
            showAlert("Error", "Tidak ada psikolog yang dipilih untuk penjadwalan.");
            return;
        }
        try {
            // Muat FXML jadwal konsultasi
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Purify/FXMLJadwalKonsultasi.fxml"));
            Parent root = loader.load();

            // Berikan data psikolog ke controller jadwal
            FXMLJadwalKonsultasiController jadwalController = loader.getController();
            jadwalController.setSelectedPsikolog(selectedPsikolog);
            jadwalController.setMainController(mainController);

            // Ganti scene ke halaman penjadwalan
            Stage currentStage = (Stage) btnJadwalkanTatapMukaDariChat.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Jadwalkan Konsultasi dengan " + selectedPsikolog.getNama());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka jendela jadwal konsultasi: " + e.getMessage());
        }
    }

    /**
     * Menyimpan riwayat chat ke `ChatHistoryList` dan kembali ke halaman konsultasi
     * utama.
     */
    @FXML
    private void handleSelesaiChat(ActionEvent event) {
        String ringkasanChat = chatArea.getText();
        if (selectedPsikolog != null && !ringkasanChat.isEmpty()) {
            // Simpan chat ke dalam daftar riwayat
            String waktuMulai = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            ChatRecord newRecord = new ChatRecord(selectedPsikolog.getNama(), waktuMulai, ringkasanChat);
            chatHistoryList.addChatRecord(newRecord);
            showAlert("Sukses", "Riwayat chat dengan " + selectedPsikolog.getNama() + " berhasil disimpan.");
        } else {
            showAlert("Info", "Tidak ada chat untuk disimpan.");
        }

        try {
            // Kembali ke halaman utama konsultasi
            if (mainController != null) {
                mainController.refreshPsikologTable();
            }
            Stage currentStage = (Stage) btnSelesaiChat.getScene().getWindow();
            currentStage.setScene(
                    new Scene(FXMLLoader.load(getClass().getResource("/Purify/FXMLKonsultasiPsikolog.fxml"))));
            currentStage.setTitle("Konsultasi dengan Psikolog");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }

    /**
     * Utility method untuk menampilkan dialog informasi ke pengguna.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
