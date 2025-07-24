package purify;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controller untuk jendela popup "Beri Komentar".
 * Mengatur interaksi pengguna saat memberikan komentar pada sebuah post.
 */
public class FXMLKomentarController {

    // Label yang menampilkan informasi post yang sedang dikomentari
    @FXML
    private Label headerLabel;

    // Area teks tempat pengguna menulis komentar
    @FXML
    private TextArea komentarArea;

    // Tombol untuk membatalkan penulisan komentar
    @FXML
    private Button btnBatal;

    // Tombol untuk mengirimkan komentar
    @FXML
    private Button btnKirim;

    // Post yang menjadi target komentar
    private Post targetPost;

    // Referensi ke controller utama komunitas, untuk menyegarkan tampilan setelah
    // komentar dikirim
    private FXMLKomunitasController komunitasController;

    // Variabel penyimpan isi komentar yang telah dikirim (jika ada)
    private String komentar;

    /**
     * Digunakan untuk mengatur post target dan controller utama komunitas.
     * Juga memperbarui label header agar menunjukkan siapa penulis post-nya.
     *
     * @param post                Post yang akan dikomentari
     * @param komunitasController Referensi ke controller utama
     */
    public void setPost(Post post, FXMLKomunitasController komunitasController) {
        this.targetPost = post;
        this.komunitasController = komunitasController;
        this.headerLabel.setText("Beri komentar pada post oleh " + post.getPenulis());
    }

    /**
     * Getter untuk mengambil komentar yang telah ditulis oleh user.
     * Nilainya null jika dibatalkan.
     *
     * @return Isi komentar
     */
    public String getKomentar() {
        return komentar;
    }

    /**
     * Handler untuk tombol "Kirim".
     * Jika area komentar tidak kosong, komentar akan ditambahkan ke post, data
     * disimpan, dan tampilan diperbarui.
     */
    @FXML
    private void handleKirim(ActionEvent event) {
        String isiKomentar = komentarArea.getText().trim();

        // Pastikan komentar tidak kosong
        if (!isiKomentar.isEmpty()) {
            this.komentar = isiKomentar;

            // TODO: Ganti string hardcoded ini dengan nama pengguna sebenarnya yang sedang
            // login
            String penulisKomentar = "PenggunaDetox";

            // Buat objek komentar dan tambahkan ke post
            Komentar komentarBaru = new Komentar(this.komentar, penulisKomentar);
            targetPost.tambahKomentar(komentarBaru);

            // Simpan perubahan ke XML dan perbarui tampilan
            KomunitasDataList.getInstance().saveToXML();
            komunitasController.refreshPosts();
        }

        closeWindow();
    }

    /**
     * Handler untuk tombol "Batal".
     * Menutup popup dan mengabaikan input pengguna.
     */
    @FXML
    private void handleBatal(ActionEvent event) {
        this.komentar = null; // Tidak menyimpan komentar
        closeWindow();
    }

    /**
     * Menutup jendela popup komentar.
     * Digunakan oleh tombol "Kirim" dan "Batal".
     */
    private void closeWindow() {
        Stage stage = (Stage) btnBatal.getScene().getWindow();
        stage.close();
    }
}
