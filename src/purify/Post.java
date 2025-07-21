package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private int idPost;
    private String isiPost;
    private String penulis;
    private LocalDateTime waktuDibuat;
    private List<Komentar> daftarKomentar;
    private int jumlahLike;

    public Post() {
        this.daftarKomentar = new ArrayList<>();
        this.jumlahLike = 0;
    }

    public Post(int idPost, String isiPost, String penulis) {
        this.idPost = idPost;
        this.isiPost = isiPost;
        this.penulis = penulis;
        this.waktuDibuat = LocalDateTime.now();
        this.daftarKomentar = new ArrayList<>();
        this.jumlahLike = 0;
    }

    // --- Getters ---
    public int getIdPost() { return idPost; }
    public String getIsiPost() { return isiPost; }
    public String getPenulis() { return penulis; }
    public List<Komentar> getDaftarKomentar() { return daftarKomentar; }
    public int getJumlahLike() { return jumlahLike; }

    public void toggleLike() {
        if (this.jumlahLike == 0) {
            this.jumlahLike = 1;
        } else {
            this.jumlahLike = 0;
        }
    }
    
    public String getWaktuDibuatFormatted() {
        if (waktuDibuat != null) {
            return waktuDibuat.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }
        return "N/A";
    }

    // --- Setters ---
    public void setIdPost(int idPost) { this.idPost = idPost; }
    public void setIsiPost(String isiPost) { this.isiPost = isiPost; }
    public void setPenulis(String penulis) { this.penulis = penulis; }

    // --- Metode lain ---
    public void tambahKomentar(Komentar komentar) {
        this.daftarKomentar.add(komentar);
    }

    // --- [TAMBAHAN] Metode untuk menghapus komentar ---
    public void hapusKomentar(Komentar komentar) {
        this.daftarKomentar.remove(komentar);
    }
}