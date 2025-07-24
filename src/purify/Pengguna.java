package purify;

/**
 * Kelas ini merepresentasikan entitas pengguna (user) dalam sistem,
 * yang memiliki nama (sebagai identitas) dan password untuk otentikasi.
 */
public class Pengguna {

    // ===== ATRIBUT =====

    // Nama pengguna
    private String nama;

    // Password pengguna (
    private String password;

    // ===== KONSTRUKTOR =====

    /**
     * Konstruktor default tanpa parameter.
     * Digunakan ketika ingin membuat objek kosong
     */
    public Pengguna() {
    }

    /**
     * Konstruktor dengan parameter nama dan password.
     * Digunakan untuk membuat objek pengguna secara langsung.
     * 
     * @param nama     Nama pengguna
     * @param password Password pengguna
     */
    public Pengguna(String nama, String password) {
        this.nama = nama;
        this.password = password;
    }

    // ===== GETTER =====

    /**
     * Mengembalikan nama pengguna.
     * 
     * @return Nama pengguna
     */
    public String getNama() {
        return nama;
    }

    /**
     * Mengembalikan password pengguna.
     * 
     * @return Password pengguna
     */
    public String getPassword() {
        return password;
    }

    // ===== SETTER =====

    /**
     * Mengatur nama pengguna.
     * 
     * @param nama Nama baru
     */
    public void setNama(String nama) {
        this.nama = nama;
    }

    /**
     * Mengatur password pengguna.
     * 
     * @param password Password baru
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
