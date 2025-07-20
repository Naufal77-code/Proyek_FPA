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

    @FXML
    private Label psikologNamaLabel;
    @FXML
    private TextArea chatArea;
    @FXML
    private TextField chatInputField;
    @FXML
    private Button btnKirim;
    @FXML
    private Button btnJadwalkanTatapMukaDariChat;
    @FXML
    private Button btnSelesaiChat;

    private Psikolog selectedPsikolog;
    private FXMLKonsultasiPsikologController mainController;
    private ChatHistoryList chatHistoryList; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatHistoryList = new ChatHistoryList(); 

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

    public void setSelectedPsikolog(Psikolog psikolog) {
        this.selectedPsikolog = psikolog;
        if (psikologNamaLabel != null) {
            psikologNamaLabel.setText("Chat dengan " + psikolog.getNama() + " (" + psikolog.getSpesialisasi() + ")");
            chatArea.appendText("Anda memulai chat dengan " + psikolog.getNama() + " pada " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".\n");
        }
    }

    public void setMainController(FXMLKonsultasiPsikologController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleKirimPesan(ActionEvent event) {
        String pesan = chatInputField.getText().trim();
        if (!pesan.isEmpty()) {
            chatArea.appendText("Anda: " + pesan + "\n");
            chatInputField.clear();
            
            String [] balasanPsikolog = {
            "Halo . Terima kasih sudah berbagi. Ada yang Bisa Saya Bantu lebih lanjut?",
            "Baik, saya mengerti. Mari kita bahas lebih dalam.",
            "Saya mendengarkan. Apa yang ingin Anda sampaikan?",
            "Oke. Ceritakan lebih banyak tentang hal itu.",
            "Terima kasih atas pesannya. Apa yang sedang Anda rasakan saat ini?"
            };

            Random random = new Random();
            String balasanAcak = balasanPsikolog[random.nextInt(balasanPsikolog.length)];
            chatArea.appendText(selectedPsikolog.getNama()+ ": " + balasanAcak + "\n");
        }
    }

    @FXML
    private void handleJadwalkanTatapMuka(ActionEvent event) {
        if (selectedPsikolog == null) {
            showAlert("Error", "Tidak ada psikolog yang dipilih untuk penjadwalan.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Purify/FXMLJadwalKonsultasi.fxml"));
            Parent root = loader.load();

            FXMLJadwalKonsultasiController jadwalController = loader.getController();
            jadwalController.setSelectedPsikolog(selectedPsikolog);
            jadwalController.setMainController(mainController); 

            Stage currentStage = (Stage) btnJadwalkanTatapMukaDariChat.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Jadwalkan Konsultasi dengan " + selectedPsikolog.getNama());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka jendela jadwal konsultasi: " + e.getMessage());
        }
    }

    @FXML
    private void handleSelesaiChat(ActionEvent event) {
        String ringkasanChat = chatArea.getText(); 
        if (selectedPsikolog != null && !ringkasanChat.isEmpty()) {
            String waktuMulai = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            ChatRecord newRecord = new ChatRecord(selectedPsikolog.getNama(), waktuMulai, ringkasanChat);
            chatHistoryList.addChatRecord(newRecord);
            showAlert("Sukses", "Riwayat chat dengan " + selectedPsikolog.getNama() + " berhasil disimpan.");
        } else {
            showAlert("Info", "Tidak ada chat untuk disimpan.");
        }

        try {
            if (mainController != null) {
                mainController.refreshPsikologTable();
            }
            Stage currentStage = (Stage) btnSelesaiChat.getScene().getWindow();
            currentStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/Purify/FXMLKonsultasiPsikolog.fxml"))));
            currentStage.setTitle("Konsultasi dengan Psikolog");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}