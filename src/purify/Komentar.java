package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Komentar {
    // Menyimpan isi dari komentar yang ditulis oleh pengguna
    private String isiKomentar;

    // Nama penulis dari komentar
    private String penulis;

    // Waktu kapan komentar dibuat, menggunakan LocalDateTime untuk akurasi waktu
    private LocalDateTime waktuDibuat;

    /**
     * Konstruktor untuk membuat objek Komentar baru.
     * 
     * @param isiKomentar Isi dari komentar yang ditulis.
     * @param penulis     Nama penulis komentar.
     *                    Secara otomatis menetapkan waktu saat ini sebagai waktu
     *                    pembuatan komentar.
     */
    public Komentar(String isiKomentar, String penulis) {
        this.isiKomentar = isiKomentar;
        this.penulis = penulis;
        this.waktuDibuat = LocalDateTime.now(); // Waktu komentar dibuat di-set saat objek dibuat
    }

    // --- Getters ---

    /**
     * Mengembalikan isi komentar.
     */
    public String getIsiKomentar() {
        return isiKomentar;
    }

    /**
     * Mengembalikan nama penulis komentar.
     */
    public String getPenulis() {
        return penulis;
    }

    /**
     * Mengembalikan waktu pembuatan komentar dalam format "dd/MM/yy HH:mm".
     * Jika waktu tidak tersedia (null), mengembalikan "N/A".
     */
    public String getWaktuDibuatFormatted() {
        if (waktuDibuat != null) {
            return waktuDibuat.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
        }
        return "N/A";
    }
}
