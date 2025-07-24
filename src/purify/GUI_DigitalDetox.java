package purify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Kelas utama untuk menjalankan aplikasi JavaFX "Purify - Digital Detox".
 * Menentukan tampilan awal yang ditampilkan berdasarkan status sesi pengguna.
 */
public class GUI_DigitalDetox extends Application {

    /**
     * Method yang dijalankan pertama kali saat aplikasi JavaFX dimulai.
     * Menentukan apakah akan membuka halaman login atau langsung ke main menu,
     * tergantung apakah ada sesi pengguna yang valid.
     *
     * @param stage Primary stage untuk aplikasi ini.
     * @throws Exception jika terjadi error dalam pemuatan FXML.
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root;
        String title;

        // Mengecek apakah terdapat sesi pengguna yang masih valid
        Pengguna validUser = SessionManager.getValidSession();

        if (validUser != null) {
            // Jika sesi valid, langsung buka main menu
            root = FXMLLoader.load(getClass().getResource("/purify/FXMLMainMenu.fxml"));
            title = "Purify - Digital Detox";
        } else {
            // Jika tidak, tampilkan form login terlebih dahulu
            root = FXMLLoader.load(getClass().getResource("/purify/FXMLLogin.fxml"));
            title = "Purify - Login";
        }

        // Membuat scene dan menampilkannya di stage
        Scene scene = new Scene(root);

        stage.setTitle(title); // Set judul window
        stage.setScene(scene); // Set scene ke stage
        stage.setResizable(false); // Menonaktifkan resize window
        stage.show(); // Menampilkan window
    }

    /**
     * Method utama yang digunakan untuk meluncurkan aplikasi JavaFX.
     *
     * @param args Argumen baris perintah (tidak digunakan di sini).
     */
    public static void main(String[] args) {
        launch(args);

        /*
         * Nama Anggota Kelompok
         * Naufal Ahmad Fauzi (24523168)
         * Muhammad Farhan Yusuf Azizi (24523129)
         * Candra Hanafi (24523084)
         * Muhammad Lutfi (24523234)
         * Mohammad Nabil (24523277)
         */

        /*
         * Link Poster
         * https://imgur.com/a/eNFGKcD
         */

        /*
         * Link Demonstrasi Aplikasi
         * https://youtu.be/_O_ZlIxrkdw?si=QZ-fpQsxRBIQ0g8R
         */
    }
}
