package purify;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLBlokirAppsController implements Initializable {

    @FXML
    private TextField durasiField;

    @FXML
    private ComboBox<String> satuanWaktuComboBox;

    @FXML
    private TextField kodeDaruratField;

    @FXML
    private TextField aktivitasField;

    @FXML
    private CheckBox cbInstagram;

    @FXML
    private CheckBox cbTikTok;

    @FXML
    private CheckBox cbFacebook;

    @FXML
    private CheckBox cbTwitter;

    @FXML
    private CheckBox cbWhatsApp;

    @FXML
    private CheckBox cbTelegram;

    @FXML
    private CheckBox cbYouTube;

    @FXML
    private CheckBox cbNetflix;

    @FXML
    private CheckBox cbSpotify;

    @FXML
    private CheckBox cbSnapchat;

    @FXML
    private CheckBox cbDiscord;

    @FXML
    private CheckBox cbTwitch;

    @FXML
    private Button btnBlokir;

    @FXML
    private Button btnMainMenu;

    @FXML
    private Button btnStatistikApp;

    private static final RiwayatBlokirAppsList riwayatList = RiwayatBlokirAppsList.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
         satuanWaktuComboBox.setItems(FXCollections.observableArrayList("Detik", "Menit", "Jam"));
        satuanWaktuComboBox.setValue("Menit");
    }

    @FXML
private void handleBlokir(ActionEvent event) {
    try {
        // Validasi durasi
        String durasiText = durasiField.getText().trim();
        if (durasiText.isEmpty()) {
            showAlert("Error", "Durasi tidak boleh kosong!");
            return;
        }
        
        int durasiValue = Integer.parseInt(durasiText);
        if (durasiValue <= 0) {
            showAlert("Error", "Durasi harus lebih dari 0!");
            return;
        }
        
        String satuan = satuanWaktuComboBox.getValue();
        int durasiMenit = convertToMinutes(durasiValue, satuan);

        // Validasi kode darurat (hanya cek tidak kosong)
        String kodeDarurat = kodeDaruratField.getText().trim();
        if (kodeDarurat.isEmpty()) {
            showAlert("Error", "Kode darurat tidak boleh kosong!");
            return;
        }

        // Validasi aplikasi
        List<String> selectedApps = getSelectedApps();
        if (selectedApps.isEmpty()) {
            showAlert("Error", "Pilih minimal satu aplikasi untuk diblokir!");
            return;
        }

        String aktivitas = aktivitasField.getText().trim();
        if (aktivitas.isEmpty()) {
            aktivitas = "Aktivitas tidak ada";
        }

        DetoxAppsSession.getInstance().startDetox(
            durasiMenit,
            aktivitas,
            kodeDarurat,
            selectedApps
        );
        openBlockingScreen();

    } catch (NumberFormatException e) {
        showAlert("Error", "Durasi harus berupa angka!");
    } catch (Exception e) {
        showAlert("Error", e.getMessage());
    }
}

    private int convertToMinutes(int value, String unit) {
        switch (unit) {
            case "Detik":
                return (int) Math.ceil(value / 60.0); // Konversi ke menit (dibulatkan ke atas)
            case "Jam":
                return value * 60;
            case "Menit":
            default:
                return value;
        }
    }

    private List<String> getSelectedApps() {
        List<String> selectedApps = new ArrayList<>();

        if (cbInstagram.isSelected())
            selectedApps.add("Instagram");
        if (cbTikTok.isSelected())
            selectedApps.add("TikTok");
        if (cbFacebook.isSelected())
            selectedApps.add("Facebook");
        if (cbTwitter.isSelected())
            selectedApps.add("Twitter/X");
        if (cbWhatsApp.isSelected())
            selectedApps.add("WhatsApp");
        if (cbTelegram.isSelected())
            selectedApps.add("Telegram");
        if (cbYouTube.isSelected())
            selectedApps.add("YouTube");
        if (cbNetflix.isSelected())
            selectedApps.add("Netflix");
        if (cbSpotify.isSelected())
            selectedApps.add("Spotify");
        if (cbSnapchat.isSelected())
            selectedApps.add("Snapchat");
        if (cbDiscord.isSelected())
            selectedApps.add("Discord");
        if (cbTwitch.isSelected())
            selectedApps.add("Twitch");

        return selectedApps;
    }

    @FXML
private void handleStatistikApp(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLStatistikApps.fxml"));
        Parent root = loader.load();

        FXMLStatistikAppsController controller = loader.getController();
        controller.setRiwayatList(RiwayatBlokirAppsList.getInstance());  // Pastikan instance terbaru
        
        Stage stage = new Stage();
        stage.setTitle("Statistik Detox Apps");
        stage.setScene(new Scene(root));
        stage.show();

    } catch (IOException e) {
        e.printStackTrace();
        showAlert("Error", "Gagal membuka halaman statistik!");
    }
}

    private void openBlockingScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Purify/FXMLBlokirAppsStatus.fxml"));
            Parent root = loader.load();

            FXMLBlokirAppsStatusController controller = loader.getController();
            controller.setMainController(this);

            Stage currentStage = (Stage) btnBlokir.getScene().getWindow();
            currentStage.setScene(new Scene(root));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman blokir!");
        }
    }

    public void addToRiwayat(String status) {
        DetoxAppsSession session = DetoxAppsSession.getInstance();
        riwayatList.addData(
                session.getFormattedWaktuMulai(),
                session.getDurasi(),
                status,
                session.getAktivitas(),
                session.getSelectedAppsString());

        durasiField.clear();
        kodeDaruratField.clear();
        aktivitasField.clear();
        clearCheckboxes();
    }

    private void clearCheckboxes() {
        cbInstagram.setSelected(false);
        cbTikTok.setSelected(false);
        cbFacebook.setSelected(false);
        cbTwitter.setSelected(false);
        cbWhatsApp.setSelected(false);
        cbTelegram.setSelected(false);
        cbYouTube.setSelected(false);
        cbNetflix.setSelected(false);
        cbSpotify.setSelected(false);
        cbSnapchat.setSelected(false);
        cbDiscord.setSelected(false);
        cbTwitch.setSelected(false);
    }

    @FXML
    private void handleMainMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLMainMenu.fxml"));
            Stage currentStage = (Stage) btnMainMenu.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Purify - Digital Detox");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal membuka halaman utama!");
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