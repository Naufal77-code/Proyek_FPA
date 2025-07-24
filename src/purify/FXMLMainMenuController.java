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

    // Tombol navigasi utama di menu utama
    @FXML
    private Button blokirHP, blokirAplikasi, lihatStatistik, konsultasiPsikolog, komunitas, btnMulaiTantangan;

    // Label untuk menampilkan tantangan harian
    @FXML
    private Label tantanganLabel;

    // Menu item untuk fitur tambahan
    @FXML
    private MenuItem editProfilMenuItem, bantuanMenuItem, keluarMenuItem;

    // Menyimpan tipe tantangan (0 = HP, 1 = Sosmed, 2 = Streaming)
    private int challengeType;

    // Teks yang akan ditampilkan secara acak sebagai tantangan harian
    private final String[] challengeTexts = {
            "Blokir HP selama 30 menit untuk hari ini!",
            "Blokir aplikasi sosial media selama 30 menit!",
            "Blokir aplikasi streaming selama 30 menit!"
    };

    // Singleton list yang menyimpan riwayat pemblokiran
    private final RiwayatBlokirList riwayatList = RiwayatBlokirList.getInstance();

    // Inisialisasi awal saat controller dimuat
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        generateRandomChallenge(); // Menampilkan tantangan secara acak di label
    }

    // Menangani klik menu Edit Profil
    @FXML
    private void handleEditProfil(ActionEvent event) {
        openModalWindow("FXMLEditProfilPilihan.fxml", "Pilih Opsi Edit");
    }

    // Menangani klik menu Bantuan
    @FXML
    private void handleBantuan(ActionEvent event) {
        openModalWindow("FXMLBantuan.fxml", "Bantuan");
    }

    // Menangani klik menu Keluar
    @FXML
    private void handleKeluar(ActionEvent event) {
        try {
            // Menghapus sesi login otomatis saat logout
            SessionManager.clearSession();

            // Menutup jendela utama
            Stage mainStage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            mainStage.close();

            // Membuka jendela login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLLogin.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Purify - Login");
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false); // Agar jendela tidak bisa diubah ukurannya
            loginStage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal untuk keluar.");
        }
    }

    // Mengecek apakah pengguna sudah melakukan detoks hari ini
    private boolean hasDetoxedToday() {
        RiwayatBlokirList riwayatHP = RiwayatBlokirList.getInstance();
        RiwayatBlokirAppsList riwayatAplikasi = RiwayatBlokirAppsList.getInstance();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Periksa riwayat pemblokiran HP
        for (RiwayatBlokir riwayat : riwayatHP.getData()) {
            try {
                LocalDate tanggalRiwayat = LocalDate.parse(riwayat.getTanggalMulai().split(" ")[0], formatter);
                if (tanggalRiwayat.equals(today) && "BERHASIL".equals(riwayat.getStatus()))
                    return true;
            } catch (Exception e) {
                /* Abaikan kesalahan format tanggal */ }
        }

        // Periksa riwayat pemblokiran aplikasi
        for (RiwayatBlokirApps riwayat : riwayatAplikasi.getData()) {
            try {
                LocalDate tanggalRiwayat = LocalDate.parse(riwayat.getTanggalMulai().split(" ")[0], formatter);
                if (tanggalRiwayat.equals(today) && "BERHASIL".equals(riwayat.getStatus()))
                    return true;
            } catch (Exception e) {
                /* Abaikan kesalahan format tanggal */ }
        }
        return false;
    }

    // Menghasilkan tantangan harian secara acak
    private void generateRandomChallenge() {
        challengeType = new Random().nextInt(challengeTexts.length);
        tantanganLabel.setText(challengeTexts[challengeType]);
        btnMulaiTantangan.setText("Mulai Tantangan (" + getChallengeDuration() + " Menit)");
    }

    // Mengembalikan durasi default tantangan
    private int getChallengeDuration() {
        return 30;
    }

    // Menangani tombol "Mulai Tantangan"
    @FXML
    private void handleMulaiTantangan(ActionEvent event) {
        switch (challengeType) {
            case 0:
                openBlokirHPWithPreset(event);
                break;
            case 1:
                openBlokirAplikasiWithPreset(event, "sosmed");
                break;
            case 2:
                openBlokirAplikasiWithPreset(event, "streaming");
                break;
        }
    }

    // Menangani tombol "Blokir HP"
    @FXML
    private void handleBlokirHP(ActionEvent event) {
        openNewWindow(event, "/purify/FXMLBlokirHP.fxml", "Blokir HP");
    }

    // Menangani tombol "Blokir Aplikasi"
    @FXML
    private void HandleBlokirAplikasi(ActionEvent event) {
        openNewWindow(event, "/purify/FXMLBlokirApps.fxml", "Blokir Aplikasi");
    }

    // Menangani tombol "Lihat Statistik"
    @FXML
    private void handleLihatStatistik(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLStatistik.fxml"));
            Parent root = loader.load();
            FXMLStatistikController controller = loader.getController();
            controller.setRiwayatList(riwayatList, "mainMenu", currentStage);
            Stage stage = new Stage();
            stage.setTitle("Lihat Statistik");
            stage.setScene(new Scene(root));
            stage.show();
            currentStage.hide(); // Menyembunyikan jendela utama sementara
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka halaman Statistik: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Menangani tombol "Komunitas"
    @FXML
    private void handleKomunitas(ActionEvent event) {
        if (hasDetoxedToday()) {
            openNewWindow(event, "/purify/FXMLKomunitas.fxml", "Komunitas");
        } else {
            openModalWindow("FXMLVerifikasiDetox.fxml", "Verifikasi Detoks");
        }
    }

    // Menangani tombol "Konsultasi Psikolog"
    @FXML
    private void handleKonsultasiPsikolog(ActionEvent event) {
        openNewWindow(event, "/purify/FXMLKonsultasiPsikolog.fxml", "Konsultasi Psikolog");
    }

    // Membuka jendela baru (non-modal)
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

    // Membuka jendela baru (modal)
    private void openModalWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL); // Jendela modal
            stage.initOwner(btnMulaiTantangan.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal membuka jendela: " + title);
            e.printStackTrace();
        }
    }

    // Membuka halaman blokir HP dengan preset durasi dari tantangan
    private void openBlokirHPWithPreset(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirHP.fxml"));
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

    // Membuka halaman blokir aplikasi dengan preset sesuai kategori
    // (sosmed/streaming)
    private void openBlokirAplikasiWithPreset(ActionEvent event, String presetType) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/purify/FXMLBlokirApps.fxml"));
            Parent root = loader.load();
            List<String> appsToBlock = presetType.equals("sosmed")
                    ? Arrays.asList("Instagram", "TikTok", "Facebook", "Twitter/X", "WhatsApp", "Telegram", "Snapchat",
                            "Discord", "Twitch")
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

    // Menutup jendela saat ini
    private void closeCurrentWindow(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
    }

    // Menampilkan alert/error dialog
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
