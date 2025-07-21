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

    @FXML private ImageView avatarImageView;
    @FXML private TextArea isiPostArea;
    @FXML private Button btnKirim;
    
    private FXMLKomunitasController komunitasController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Image avatar = new Image(getClass().getResourceAsStream("/icons/avatar_placeholder.png"));
            avatarImageView.setImage(avatar);
        } catch (Exception e) {
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

        // --- [MODIFIKASI] Mengambil nama penulis dari user yang sedang login ---
        Pengguna currentUser = ManajemenPengguna.getInstance().getCurrentUser();
        String penulis;

        if (currentUser != null) {
            penulis = currentUser.getNama();
        } else {
            // Fallback jika tidak ada user yang login (seharusnya tidak terjadi)
            penulis = "Anonim"; 
        }
        // --- Akhir Modifikasi ---
        
        KomunitasDataList.getInstance().tambahPost(isi, penulis);
        
        if (komunitasController != null) {
            komunitasController.refreshPosts();
        }
        
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