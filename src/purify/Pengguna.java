package purify;

public class Pengguna {
    private String nama;
    private String password;

    // Default constructor untuk XStream
    public Pengguna() {}

    public Pengguna(String nama, String password) {
        this.nama = nama;
        // Di aplikasi nyata, sandi harus di-hash. Untuk kesederhanaan, kita simpan sebagai plain text.
        this.password = password;
    }

    // Getters
    public String getNama() { return nama; }
    public String getPassword() { return password; }
}