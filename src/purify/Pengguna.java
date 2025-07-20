package purify;

public class Pengguna {
    // --- PERUBAHAN 4: Field "username" dikembalikan menjadi "nama" ---
    private String nama;
    private String password;

    public Pengguna() {}

    public Pengguna(String nama, String password) {
        this.nama = nama;
        this.password = password;
    }

    // --- PERUBAHAN 5: Getter dan Setter disesuaikan kembali ---
    public String getNama() { return nama; }
    public String getPassword() { return password; }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}