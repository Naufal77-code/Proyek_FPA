package purify;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLBuatPostController implements Initializable {

    // 1. Deklarasikan ImageView dari FXML
    @FXML
    private ImageView avatarImageView;
    
    @FXML
    private TextArea isiPostArea;
    
    @FXML
    private Button btnKirim;
    
    private FXMLKomunitasController komunitasController;

    // 2. Implementasikan metode initialize
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Di sinilah kita memuat gambar
        try {
            Image avatar = new Image(getClass().getResourceAsStream("/icons/avatar_placeholder.png"));
            avatarImageView.setImage(avatar);
        } catch (Exception e) {
            // Jika ada error, ini akan memberitahu Anda di konsol
            System.err.println("Gagal memuat gambar avatar di popup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setKomunitasController(FXMLKomunitasController controller) {
        this.komunitasController = controller;
    }

    @FXML
    private void handleKirim() {
        String isi = isiPostArea.getText().trim();
        if (isi.isEmpty()) {
            showAlert("Error", "Postingan tidak boleh kosong.");
            return;
        }

        // Untuk sementara, kita hardcode nama penulis.
        // Nantinya bisa diambil dari sesi login pengguna.
        String penulis = "PenggunaDetox"; 
        
        KomunitasDataList.getInstance().tambahPost(isi, penulis);
        
        // Refresh timeline di halaman utama komunitas
        if (komunitasController != null) {
            komunitasController.refreshPosts();
        }
        
        // Tutup window
        Stage stage = (Stage) btnKirim.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleBatal() {
        Stage stage = (Stage) btnKirim.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}