package tes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DetoxSession {
    private static DetoxSession instance;
    private int durasi;
    private String aktivitas;
    private String kodeDarurat;
    private LocalDateTime waktuMulai;
    private boolean isActive;

    private DetoxSession() {
        this.isActive = false;
    }

    public static DetoxSession getInstance() {
        if (instance == null) {
            instance = new DetoxSession();
        }
        return instance;
    }

    public void startDetox(int durasi, String aktivitas, String kodeDarurat) {
        this.durasi = durasi;
        this.aktivitas = aktivitas;
        this.kodeDarurat = kodeDarurat;
        this.waktuMulai = LocalDateTime.now();
        this.isActive = true;
    }

    public void endDetox() {
        this.isActive = false;
    }

    public int getDurasi() { return durasi; }
    public String getAktivitas() { return aktivitas; }
    public String getKodeDarurat() { return kodeDarurat; }
    public LocalDateTime getWaktuMulai() { return waktuMulai; }
    public boolean isActive() { return isActive; }

    public String getFormattedWaktuMulai() {
        if (waktuMulai != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return waktuMulai.format(formatter);
        }
        return "";
    }
}