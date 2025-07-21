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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class FXMLKomunitasController implements Initializable {

    @FXML private ListView<Post> postListView;
    @FXML private Button btnBuatPost;
    @FXML private Button btnKembali;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // [MODIFIKASI] Memuat unggahan berdasarkan user yang login
        loadPostsForCurrentUser();
        postListView.setCellFactory(param -> new PostListCell(this));
    }

    // [MODIFIKASI] Metode ini sekarang memfilter unggahan
    public void loadPostsForCurrentUser() {
        Pengguna currentUser = ManajemenPengguna.getInstance().getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getNama();
            KomunitasDataList data = KomunitasDataList.getInstance();
            // Memanggil metode baru untuk mendapatkan unggahan yang sudah difilter
            postListView.setItems(data.getPostsForUser(username));
        } else {
            // Jika tidak ada yang login, tampilkan timeline kosong
            postListView.setItems(FXCollections.observableArrayList());
            btnBuatPost.setDisable(true); // Nonaktifkan tombol buat post
        }
    }

    @FXML
    private void handleBuatPost() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBuatPost.fxml"));
            Parent root = loader.load();
            
            FXMLBuatPostController controller = loader.getController();
            controller.setKomunitasController(this);

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
    
    // [MODIFIKASI] Metode refresh sekarang memanggil metode yang sudah difilter
    public void refreshPosts() {
        loadPostsForCurrentUser();
        postListView.refresh();
    }

    // Kelas internal PostListCell tetap sama seperti sebelumnya
    private class PostListCell extends ListCell<Post> {
        // ... (Tidak ada perubahan di dalam kelas ini)
        private final VBox card = new VBox(10);
        private final ImageView avatar = new ImageView();
        private final Label penulisLabel = new Label();
        private final Label waktuLabel = new Label();
        private final Label isiLabel = new Label();
        private final Label likesLabel = new Label();
        private final Button likeButton = new Button("Like");
        private final Button commentButton = new Button("Comment");
        private final VBox commentSection = new VBox(5);
        private final FXMLKomunitasController komunitasController;

        public PostListCell(FXMLKomunitasController komunitasController) {
            this.komunitasController = komunitasController;
            card.getStyleClass().add("post-card");
            VBox penulisInfo = new VBox(penulisLabel, waktuLabel);
            avatar.setFitHeight(40);
            avatar.setFitWidth(40);
            HBox header = new HBox(10, avatar, penulisInfo);
            header.setAlignment(Pos.CENTER_LEFT);
            avatar.getStyleClass().add("avatar-image");
            penulisLabel.getStyleClass().add("post-author");
            waktuLabel.getStyleClass().add("post-timestamp");
            isiLabel.setWrapText(true);
            isiLabel.getStyleClass().add("post-content");
            HBox engagementInfo = new HBox(likesLabel);
            likesLabel.getStyleClass().add("post-engagement");
            setupActionButton(likeButton, "/icons/like.png");
            setupActionButton(commentButton, "/icons/comment.png");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox actionBar = new HBox(10, likeButton, commentButton, spacer);
            actionBar.getStyleClass().add("post-actions");
            card.getChildren().addAll(
                header, isiLabel, engagementInfo, new Separator(), 
                actionBar, new Separator(), commentSection
            );
        }

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
                commentSection.getChildren().clear();
                if (!item.getDaftarKomentar().isEmpty()) {
                    for (Komentar komentar : item.getDaftarKomentar()) {
                        HBox commentBox = new HBox(5);
                        commentBox.setAlignment(Pos.CENTER_LEFT);
                        Label commentLabel = new Label(komentar.getPenulis() + ": " + komentar.getIsiKomentar());
                        commentLabel.setWrapText(true);
                        commentLabel.getStyleClass().add("comment-label");
                        HBox.setHgrow(commentLabel, Priority.ALWAYS);
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
                likeButton.setOnAction(e -> {
                    item.toggleLike();
                    likesLabel.setText(item.getJumlahLike() + " Likes");
                    KomunitasDataList.getInstance().saveToXML();
                });
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