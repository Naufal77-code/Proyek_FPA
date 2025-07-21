package purify;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI_DigitalDetox extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root;
        String title;

        Pengguna validUser = SessionManager.getValidSession();

        if (validUser != null) {
            root = FXMLLoader.load(getClass().getResource("/purify/FXMLMainMenu.fxml"));
            title = "Purify - Digital Detox";
        } else {
            root = FXMLLoader.load(getClass().getResource("/purify/FXMLLogin.fxml"));
            title = "Purify - Login";
        }

        Scene scene = new Scene(root);

        stage.setTitle(title);
        stage.setScene(scene);

        stage.setResizable(false);

        stage.show();
    }

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