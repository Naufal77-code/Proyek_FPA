package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas ini merepresentasikan sebuah postingan (Post) dalam sistem, misalnya
 * untuk forum, komunitas, atau fitur sosial.
 * Setiap Post memiliki ID, isi, penulis, waktu dibuat, komentar, dan jumlah
 * like.
 */
public class Post {

    // ===== ATRIBUT =====

    // ID unik untuk setiap Post
    private int idPost;

    // Isi konten dari Post (misalnya teks curhatan atau informasi)
    private String isiPost;

    // Nama atau identitas penulis post
    private String penulis;

    // Timestamp saat post dibuat (secara otomatis diset saat konstruktor dipanggil)
    private LocalDateTime waktuDibuat;

    // Daftar komentar yang ditambahkan ke Post ini
    private List<Komentar> daftarKomentar;

    // Jumlah "like" yang diterima oleh post ini. Bisa 0 atau 1 (toggle).
    private int jumlahLike;

    // ===== KONSTRUKTOR =====

    // Konstruktor default: digunakan jika kita ingin membuat objek kosong dulu
    // (misalnya saat deserialisasi)
    public Post() {
        this.daftarKomentar = new ArrayList<>();
        this.jumlahLike = 0;
    }

    // Konstruktor dengan data awal: ID, isi, dan penulis. Waktu dibuat diset
    // otomatis saat ini.
    public Post(int idPost, String isiPost, String penulis) {
        this.idPost = idPost;
        this.isiPost = isiPost;
        this.penulis = penulis;
        this.waktuDibuat = LocalDateTime.now(); // Otomatis menyimpan waktu post dibuat
        this.daftarKomentar = new ArrayList<>(); // Inisialisasi list kosong untuk komentar
        this.jumlahLike = 0; // Default: belum ada like
    }

    // ===== GETTER =====

    // Mengembalikan ID post
    public int getIdPost() {
        return idPost;
    }

    // Mengembalikan isi konten post
    public String getIsiPost() {
        return isiPost;
    }

    // Mengembalikan nama penulis
    public String getPenulis() {
        return penulis;
    }

    // Mengembalikan daftar komentar pada post ini
    public List<Komentar> getDaftarKomentar() {
        return daftarKomentar;
    }

    // Mengembalikan jumlah like saat ini
    public int getJumlahLike() {
        return jumlahLike;
    }

    /**
     * Metode untuk mendapatkan waktu dibuat dalam format yang ramah dibaca.
     */
    public String getWaktuDibuatFormatted() {
        if (waktuDibuat != null) {
            return waktuDibuat.format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
        }
        return "N/A";
    }

    // ===== SETTER =====

    // Mengatur ulang ID post
    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    // Mengatur ulang isi konten post
    public void setIsiPost(String isiPost) {
        this.isiPost = isiPost;
    }

    // Mengatur ulang nama penulis
    public void setPenulis(String penulis) {
        this.penulis = penulis;
    }

    // ===== METODE LAIN =====

    /**
     * Metode untuk menambah komentar ke daftar komentar.
     * Komentar ditambahkan ke akhir list.
     */
    public void tambahKomentar(Komentar komentar) {
        this.daftarKomentar.add(komentar);
    }

    /**
     * Metode untuk menghapus komentar tertentu dari daftar.
     * Komentar dicocokkan berdasarkan objek yang sama.
     */
    public void hapusKomentar(Komentar komentar) {
        this.daftarKomentar.remove(komentar);
    }

    /**
     * Metode untuk toggle (berpindah) status like:
     * - Jika jumlah like = 0 → akan berubah menjadi 1 (like)
     * - Jika jumlah like = 1 → akan kembali menjadi 0 (unlike)
     */
    public void toggleLike() {
        if (this.jumlahLike == 0) {
            this.jumlahLike = 1;
        } else {
            this.jumlahLike = 0;
        }
    }
}
