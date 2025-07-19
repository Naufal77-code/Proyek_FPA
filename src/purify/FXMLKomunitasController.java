package purify;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
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
import java.util.ResourceBundle;

public class FXMLKomunitasController implements Initializable {

    @FXML private ListView<Post> postListView;
    @FXML private Button btnBuatPost;
    @FXML private Button btnKembali;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadPosts();
        
        // Kustomisasi tampilan setiap item di ListView menjadi seperti Timeline Facebook
        postListView.setCellFactory(param -> new ListCell<Post>() {
            // Siapkan elemen-elemen UI sekali saja untuk efisiensi
            private final VBox card = new VBox(10);
            private final ImageView avatar = new ImageView();
            private final Label penulisLabel = new Label();
            private final Label waktuLabel = new Label();
            private final Label isiLabel = new Label();
            private final Label likesLabel = new Label();
            private final Button likeButton = new Button("Like");
            private final Button commentButton = new Button("Comment");
            private final VBox commentSection = new VBox(5);

            {
                // -- Atur style dan struktur elemen-elemen --
                card.getStyleClass().add("post-card");
                
                // 1. Header (Avatar + Nama + Waktu)
                VBox penulisInfo = new VBox(penulisLabel, waktuLabel);
                avatar.setFitHeight(40); // Atur tinggi gambar menjadi 40 piksel
                avatar.setFitWidth(40);  // Atur lebar gambar menjadi 40 piksel
                HBox header = new HBox(10, avatar, penulisInfo);
                header.setAlignment(Pos.CENTER_LEFT);
                avatar.getStyleClass().add("avatar-image");
                penulisLabel.getStyleClass().add("post-author");
                waktuLabel.getStyleClass().add("post-timestamp");

                // 2. Konten
                isiLabel.setWrapText(true);
                isiLabel.getStyleClass().add("post-content");

                // 3. Info Engagement (Likes)
                HBox engagementInfo = new HBox(likesLabel);
                likesLabel.getStyleClass().add("post-engagement");

                // 4. Tombol Aksi
                setupActionButton(likeButton, "/icons/like.png");
                setupActionButton(commentButton, "/icons/comment.png");
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox actionBar = new HBox(10, likeButton, commentButton, spacer);
                actionBar.getStyleClass().add("post-actions");

                // 5. Rakit semua ke dalam kartu
                card.getChildren().addAll(
                    header, 
                    isiLabel, 
                    engagementInfo, 
                    new Separator(), 
                    actionBar, 
                    new Separator(), 
                    commentSection
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
                    // Jika ikon tidak ditemukan, tampilkan teks saja
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
                    // -- Isi data ke elemen-elemen --
                    // Avatar (gunakan placeholder)
                    try {
                        avatar.setImage(new Image(getClass().getResourceAsStream("/icons/avatar_placeholder.png")));
                    } catch (Exception e) {
                        System.err.println("Placeholder avatar not found.");
                    }

                    // Info penulis dan waktu
                    penulisLabel.setText(item.getPenulis());
                    waktuLabel.setText(item.getWaktuDibuatFormatted());
                    
                    // Konten
                    isiLabel.setText(item.getIsiPost());
                    
                    // Likes
                    likesLabel.setText(item.getJumlahLike() + " Likes");

                    // Tampilkan Komentar
                    commentSection.getChildren().clear(); // Kosongkan dulu
                    if (!item.getDaftarKomentar().isEmpty()) {
                        for (Komentar komentar : item.getDaftarKomentar()) {
                            Label commentLabel = new Label(komentar.getPenulis() + ": " + komentar.getIsiKomentar());
                            commentLabel.setWrapText(true);
                            commentLabel.getStyleClass().add("comment-label");
                            commentSection.getChildren().add(commentLabel);
                        }
                    }

                    // -- Atur Aksi Tombol --
                    likeButton.setOnAction(e -> {
                        item.tambahLike();
                        likesLabel.setText(item.getJumlahLike() + " Likes");
                        KomunitasDataList.getInstance().saveToXML(); // Simpan perubahan
                    });

                    commentButton.setOnAction(e -> {
                        // Logika untuk menambahkan komentar bisa dipanggil di sini
                        System.out.println("Tombol comment untuk post #" + item.getIdPost() + " ditekan.");
                        // Buka dialog untuk input komentar
                    });

                    setGraphic(card);
                }
            }
        });
    }

    // ... (sisa kode seperti loadPosts(), handleBuatPost(), handleKembali() tetap sama)
    // ...
    public void loadPosts() {
        KomunitasDataList data = KomunitasDataList.getInstance();
        postListView.setItems(data.getPostList());
    }

    @FXML
private void handleBuatPost() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLBuatPost.fxml"));
        Parent root = loader.load();
        
        FXMLBuatPostController controller = loader.getController();
        controller.setKomunitasController(this);

        // --- Perubahan utama ada di sini ---
        Stage popupStage = new Stage();
        popupStage.setTitle("Buat Postingan Baru");
        
        // 1. Menjadikannya MODAL: Memblokir window di belakangnya
        popupStage.initModality(Modality.APPLICATION_MODAL);
        
        // 2. Menentukan "pemilik" window ini
        popupStage.initOwner(btnBuatPost.getScene().getWindow());

        Scene scene = new Scene(root);
        // Jika Anda ingin menerapkan tema yang sama
        scene.getStylesheets().add(getClass().getResource("purify-theme.css").toExternalForm());
        
        popupStage.setScene(scene);
        
        // 3. Menggunakan showAndWait() agar kode berhenti sejenak sampai popup ditutup
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
    
    public void refreshPosts() {
        postListView.refresh();
    }
}