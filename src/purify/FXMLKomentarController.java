package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FXMLKomentarController {

    @FXML private Label headerLabel;
    @FXML private TextArea komentarArea;
    @FXML private Button btnBatal;
    @FXML private Button btnKirim;

    private Post targetPost;
    private FXMLKomunitasController komunitasController;
    private String komentar;

    public void setPost(Post post, FXMLKomunitasController komunitasController) {
        this.targetPost = post;
        this.komunitasController = komunitasController;
        this.headerLabel.setText("Beri komentar pada post oleh " + post.getPenulis());
    }

    public String getKomentar() {
        return komentar;
    }

    @FXML
    private void handleKirim(ActionEvent event) {
        String isiKomentar = komentarArea.getText().trim();
        if (!isiKomentar.isEmpty()) {
            this.komentar = isiKomentar;
            String penulisKomentar = "PenggunaDetox"; // Hardcoded
            Komentar komentarBaru = new Komentar(this.komentar, penulisKomentar);
            
            targetPost.tambahKomentar(komentarBaru);
            KomunitasDataList.getInstance().saveToXML();
            komunitasController.refreshPosts();
        }
        closeWindow();
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        this.komentar = null;
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnBatal.getScene().getWindow();
        stage.close();
    }
}