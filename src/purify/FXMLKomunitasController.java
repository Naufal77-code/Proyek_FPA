package purify;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLKomunitasController implements Initializable {

    // Komponen UI untuk menampilkan daftar postingan
    @FXML
    private ListView<Post> postListView;

    // Tombol untuk membuat postingan baru
    @FXML
    private Button btnBuatPost;

    // Tombol untuk kembali ke menu utama
    @FXML
    private Button btnKembali;

    /**
     * Method ini dipanggil secara otomatis saat FXML dimuat.
     * Digunakan untuk menginisialisasi data dan UI.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPostsForCurrentUser(); // Memuat postingan berdasarkan user yang login
        postListView.setCellFactory(param -> new PostListCell(this)); // Menentukan bagaimana tiap item ditampilkan
    }

    /**
     * Memuat postingan hanya milik user yang sedang login.
     * Jika tidak ada user yang login, maka tombol dinonaktifkan dan list
     * dikosongkan.
     */
    public void loadPostsForCurrentUser() {
        Pengguna currentUser = ManajemenPengguna.getInstance().getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getNama();
            KomunitasDataList data = KomunitasDataList.getInstance();
            postListView.setItems(data.getPostsForUser(username)); // Hanya post milik user ini
        } else {
            postListView.setItems(FXCollections.observableArrayList()); // Kosongkan daftar post
            btnBuatPost.setDisable(true); // Nonaktifkan tombol jika belum login
        }
    }

    /**
     * Handler untuk tombol "Buat Post".
     * Membuka jendela popup untuk membuat postingan baru.
     */
    @FXML
    private void handleBuatPost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBuatPost.fxml"));
            Parent root = loader.load();

            // Ambil controller popup dan berikan referensi controller utama
            FXMLBuatPostController controller = loader.getController();
            controller.setKomunitasController(this);

            // Buat dan tampilkan jendela popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Buat Postingan Baru");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(btnBuatPost.getScene().getWindow());

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("purify-theme.css").toExternalForm());
            popupStage.setScene(scene);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler untuk tombol kembali.
     * Mengarahkan user ke Main Menu.
     */
    @FXML
    private void handleKembali() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLMainMenu.fxml"));
            Stage currentStage = (Stage) btnKembali.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Purify - Digital Detox");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Menyegarkan ulang daftar postingan dari pengguna saat ini.
     * Dipanggil setelah membuat post atau menghapus komentar.
     */
    public void refreshPosts() {
        loadPostsForCurrentUser();
        postListView.refresh();
    }

    /**
     * Kelas dalam (inner class) untuk mendesain tampilan tiap sel ListView.
     * Setiap Post ditampilkan dalam bentuk "card" dengan tombol Like dan Komentar.
     */
    private class PostListCell extends ListCell<Post> {
        // Komponen visual dalam satu card post
        private final VBox card = new VBox(10); // Card utama
        private final ImageView avatar = new ImageView(); // Gambar profil
        private final Label penulisLabel = new Label(); // Nama penulis
        private final Label waktuLabel = new Label(); // Tanggal/waktu post
        private final Label isiLabel = new Label(); // Isi teks post
        private final Label likesLabel = new Label(); // Jumlah like
        private final Button likeButton = new Button("Like"); // Tombol like
        private final Button commentButton = new Button("Comment"); // Tombol komentar
        private final VBox commentSection = new VBox(5); // Komentar yang muncul
        private final FXMLKomunitasController komunitasController; // Referensi ke controller utama

        /**
         * Konstruktor: Menyiapkan UI untuk setiap post.
         */
        public PostListCell(FXMLKomunitasController komunitasController) {
            this.komunitasController = komunitasController;

            card.getStyleClass().add("post-card");

            VBox penulisInfo = new VBox(penulisLabel, waktuLabel);
            avatar.setFitHeight(40);
            avatar.setFitWidth(40);

            HBox header = new HBox(10, avatar, penulisInfo);
            header.setAlignment(Pos.CENTER_LEFT);

            // Styling label
            avatar.getStyleClass().add("avatar-image");
            penulisLabel.getStyleClass().add("post-author");
            waktuLabel.getStyleClass().add("post-timestamp");
            isiLabel.setWrapText(true);
            isiLabel.getStyleClass().add("post-content");

            // Info like
            HBox engagementInfo = new HBox(likesLabel);
            likesLabel.getStyleClass().add("post-engagement");

            // Tombol Like dan Komentar
            setupActionButton(likeButton, "/icons/like.png");
            setupActionButton(commentButton, "/icons/comment.png");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox actionBar = new HBox(10, likeButton, commentButton, spacer);
            actionBar.getStyleClass().add("post-actions");

            card.getChildren().addAll(
                    header, isiLabel, engagementInfo, new Separator(),
                    actionBar, new Separator(), commentSection);
        }

        /**
         * Menambahkan ikon ke tombol.
         */
        private void setupActionButton(Button button, String iconPath) {
            try {
                Image icon = new Image(getClass().getResourceAsStream(iconPath));
                ImageView iconView = new ImageView(icon);
                iconView.setFitHeight(16);
                iconView.setFitWidth(16);
                button.setGraphic(iconView);
            } catch (Exception e) {
                System.err.println("Icon not found: " + iconPath);
            }
            button.getStyleClass().add("action-button");
        }

        /**
         * Method utama untuk meng-update tampilan cell.
         * Menyesuaikan card berdasarkan data post.
         */
        @Override
        protected void updateItem(Post item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                try {
                    avatar.setImage(new Image(getClass().getResourceAsStream("/icons/avatar_placeholder.png")));
                } catch (Exception e) {
                    System.err.println("Placeholder avatar not found.");
                }

                penulisLabel.setText(item.getPenulis());
                waktuLabel.setText(item.getWaktuDibuatFormatted());
                isiLabel.setText(item.getIsiPost());
                likesLabel.setText(item.getJumlahLike() + " Likes");

                // Render komentar
                commentSection.getChildren().clear();
                if (!item.getDaftarKomentar().isEmpty()) {
                    for (Komentar komentar : item.getDaftarKomentar()) {
                        HBox commentBox = new HBox(5);
                        commentBox.setAlignment(Pos.CENTER_LEFT);

                        Label commentLabel = new Label(komentar.getPenulis() + ": " + komentar.getIsiKomentar());
                        commentLabel.setWrapText(true);
                        commentLabel.getStyleClass().add("comment-label");
                        HBox.setHgrow(commentLabel, Priority.ALWAYS);

                        // Tombol hapus komentar
                        Button deleteCommentButton = new Button("X");
                        deleteCommentButton.getStyleClass().add("delete-comment-button");
                        deleteCommentButton.setOnAction(e -> {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Konfirmasi Hapus");
                            alert.setHeaderText("Anda akan menghapus komentar ini.");
                            alert.setContentText("Apakah Anda yakin?");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                item.hapusKomentar(komentar);
                                KomunitasDataList.getInstance().saveToXML();
                                komunitasController.refreshPosts();
                            }
                        });

                        commentBox.getChildren().addAll(commentLabel, deleteCommentButton);
                        commentSection.getChildren().add(commentBox);
                    }
                }

                // Tombol like
                likeButton.setOnAction(e -> {
                    item.toggleLike();
                    likesLabel.setText(item.getJumlahLike() + " Likes");
                    KomunitasDataList.getInstance().saveToXML();
                });

                // Tombol komentar
                commentButton.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLKomentar.fxml"));
                        Parent root = loader.load();
                        FXMLKomentarController controller = loader.getController();
                        controller.setPost(item, this.komunitasController);

                        Stage popupStage = new Stage();
                        popupStage.setTitle("Beri Komentar");
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.initOwner(btnBuatPost.getScene().getWindow());

                        Scene scene = new Scene(root);
                        popupStage.setScene(scene);
                        popupStage.showAndWait();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                });

                setGraphic(card);
            }
        }
    }
}
