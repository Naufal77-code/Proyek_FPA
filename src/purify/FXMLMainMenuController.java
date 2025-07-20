package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class FXMLMainMenuController implements Initializable {
    // Variabel FXML
    @FXML private Button blokirHP, blokirAplikasi, lihatStatistik, konsultasiPsikolog, komunitas, btnMulaiTantangan;
    @FXML private Label tantanganLabel;
    @FXML private MenuItem editProfilMenuItem, bantuanMenuItem, keluarMenuItem;

    private int challengeType;
    private final String[] challengeTexts = {
        "Blokir HP selama 30 menit untuk hari ini!",
        "Blokir aplikasi sosial media selama 30 menit!",
        "Blokir aplikasi streaming selama 30 menit!"
    };
    private final RiwayatBlokirList riwayatList = RiwayatBlokirList.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generateRandomChallenge();
    }

    // --- Logika Menu Pengaturan ---
    @FXML
    private void handleEditProfil(ActionEvent event) {
        openModalWindow("FXMLEditProfilPilihan.fxml", "Pilih Opsi Edit");
    }

    @FXML
    private void handleBantuan(ActionEvent event) {
        openModalWindow("FXMLBantuan.fxml", "Bantuan");
    }

    @FXML
    private void handleKeluar(ActionEvent event) {
        try {
            // Dapatkan stage dari MenuItem
            Stage mainStage = (Stage) ((MenuItem)event.getSource()).getParentPopup().getOwnerWindow();
            mainStage.close();

            // Buka kembali jendela login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLLogin.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Purify - Login");
            loginStage.setScene(new Scene(root));
            loginStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal untuk keluar.");
        }
    }

    // --- Logika Tombol dan Tantangan (Tidak Berubah) ---
    private boolean hasDetoxedToday() {
        RiwayatBlokirList riwayatHP = RiwayatBlokirList.getInstance();
        RiwayatBlokirAppsList riwayatAplikasi = RiwayatBlokirAppsList.getInstance();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (RiwayatBlokir riwayat : riwayatHP.getData()) {
            try {
                LocalDate tanggalRiwayat = LocalDate.parse(riwayat.getTanggalMulai().split(" ")[0], formatter);
                if (tanggalRiwayat.equals(today) && "BERHASIL".equals(riwayat.getStatus())) return true;
            } catch (Exception e) { /* Abaikan format salah */ }
        }
        for (RiwayatBlokirApps riwayat : riwayatAplikasi.getData()) {
            try {
                LocalDate tanggalRiwayat = LocalDate.parse(riwayat.getTanggalMulai().split(" ")[0], formatter);
                if (tanggalRiwayat.equals(today) && "BERHASIL".equals(riwayat.getStatus())) return true;
            } catch (Exception e) { /* Abaikan format salah */ }
        }
        return false;
    }

    private void generateRandomChallenge() {
        challengeType = new Random().nextInt(challengeTexts.length);
        tantanganLabel.setText(challengeTexts[challengeType]);
        btnMulaiTantangan.setText("Mulai Tantangan (" + getChallengeDuration() + " Menit)");
    }

    private int getChallengeDuration() { return 30; }

    @FXML
    private void handleMulaiTantangan(ActionEvent event) {
        switch (challengeType) {
            case 0: openBlokirHPWithPreset(event); break;
            case 1: openBlokirAplikasiWithPreset(event, "sosmed"); break;
            case 2: openBlokirAplikasiWithPreset(event, "streaming"); break;
        }
    }
    
    @FXML
    private void handleBlokirHP(ActionEvent event) {
        openNewWindow(event, "FXMLBlokirHP.fxml", "Blokir HP");
    }

    @FXML
    private void HandleBlokirAplikasi(ActionEvent event) {
        openNewWindow(event, "FXMLBlokirApps.fxml", "Blokir Aplikasi");
    }

    @FXML
    private void handleLihatStatistik(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLStatistik.fxml"));
            Parent root = loader.load();
            FXMLStatistikController controller = loader.getController();
            controller.setRiwayatList(riwayatList, "mainMenu", currentStage);
            Stage stage = new Stage();
            stage.setTitle("Lihat Statistik");
            stage.setScene(new Scene(root));
            stage.show();
            currentStage.hide();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Statistik: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleKomunitas(ActionEvent event) {
        if (hasDetoxedToday()) {
            openNewWindow(event, "FXMLKomunitas.fxml", "Komunitas");
        } else {
            openModalWindow("FXMLVerifikasiDetox.fxml", "Verifikasi Detoks");
        }
    }

    @FXML
    private void handleKonsultasiPsikolog(ActionEvent event) {
        openNewWindow(event, "FXMLKonsultasiPsikolog.fxml", "Konsultasi Psikolog");
    }

    // --- Metode Bantuan (Helper Methods) ---
    private void openNewWindow(ActionEvent event, String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            closeCurrentWindow(event);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman: " + title);
            e.printStackTrace();
        }
    }

    private void openModalWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnMulaiTantangan.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka jendela: " + title);
            e.printStackTrace();
        }
    }

    private void openBlokirHPWithPreset(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBlokirHP.fxml"));
            Parent root = loader.load();
            FXMLBlokirHPController controller = loader.getController();
            controller.setPreset(String.valueOf(getChallengeDuration()), "Menit");
            Stage stage = new Stage();
            stage.setTitle("Blokir HP (Tantangan)");
            stage.setScene(new Scene(root));
            stage.show();
            closeCurrentWindow(event);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Blokir HP.");
            e.printStackTrace();
        }
    }

    private void openBlokirAplikasiWithPreset(ActionEvent event, String presetType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBlokirApps.fxml"));
            Parent root = loader.load();
            List<String> appsToBlock = presetType.equals("sosmed")
                ? Arrays.asList("Instagram", "TikTok", "Facebook", "Twitter/X", "WhatsApp", "Telegram", "Snapchat", "Discord", "Twitch")
                : Arrays.asList("YouTube", "Netflix");
            FXMLBlokirAppsController controller = loader.getController();
            controller.setPreset(String.valueOf(getChallengeDuration()), "Menit", appsToBlock);
            Stage stage = new Stage();
            stage.setTitle("Blokir Aplikasi (Tantangan)");
            stage.setScene(new Scene(root));
            stage.show();
            closeCurrentWindow(event);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Blokir Aplikasi.");
            e.printStackTrace();
        }
    }
    
    private void closeCurrentWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}