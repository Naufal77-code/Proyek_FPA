package purify;

public class Pengguna {
    // PERUBAHAN 4: Field "nama" diubah menjadi "username"
    private String username;
    private String password;

    public Pengguna() {}

    public Pengguna(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // PERUBAHAN 5: Getter dan Setter disesuaikan
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}