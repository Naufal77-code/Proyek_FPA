package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Komentar {
    private String isiKomentar;
    private String penulis;
    private LocalDateTime waktuDibuat;

    public Komentar(String isiKomentar, String penulis) {
        this.isiKomentar = isiKomentar;
        this.penulis = penulis;
        this.waktuDibuat = LocalDateTime.now();
    }

    // --- Getters ---
    public String getIsiKomentar() { return isiKomentar; }
    public String getPenulis() { return penulis; }
    
    public String getWaktuDibuatFormatted() {
        if (waktuDibuat != null) {
            return waktuDibuat.format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm"));
        }
        return "N/A";
    }
}