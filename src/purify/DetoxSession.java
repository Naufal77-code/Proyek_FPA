package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DetoxSession {
    private static DetoxSession instance;
    private long durasiInSeconds;
    private String aktivitas;
    private String kodeDarurat;
    private LocalDateTime waktuMulai;
    private boolean isActive;

    private DetoxSession() {
        this.isActive = false;
        this.durasiInSeconds = 0;
        this.aktivitas = "";
        this.kodeDarurat = "";
        this.waktuMulai = null;
    }

    public static DetoxSession getInstance() {
        if (instance == null) {
            instance = new DetoxSession();
        }
        return instance;
    }

    public void startDetox(long durasiDetik, String aktivitas, String kodeDarurat) {
        if (durasiDetik <= 0) {
            throw new IllegalArgumentException("Durasi harus lebih dari 0 detik");
        }
        if (kodeDarurat == null || kodeDarurat.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode darurat tidak boleh kosong");
        }
        this.durasiInSeconds = durasiDetik;
        this.aktivitas = (aktivitas == null || aktivitas.trim().isEmpty()) ? "Aktivitas tidak ada" : aktivitas.trim();
        this.kodeDarurat = kodeDarurat.trim();
        this.waktuMulai = LocalDateTime.now();
        this.isActive = true;
    }

    public void endDetox() {
        this.isActive = false;
    }

    public long getDurasiInSeconds() {
        return durasiInSeconds;
    }

    public int getDurasiInMinutes() {
        return (int) Math.round(durasiInSeconds / 60.0);
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public String getKodeDarurat() {
        return kodeDarurat;
    }

    public String getFormattedWaktuMulai() {
        if (waktuMulai != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return waktuMulai.format(formatter);
        }
        return "";
    }

    public long getRemainingTimeInSeconds() {
        if (!isActive || waktuMulai == null) {
            return 0;
        }
        LocalDateTime endTime = waktuMulai.plusSeconds(durasiInSeconds);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) {
            return 0;
        }
        return java.time.Duration.between(now, endTime).getSeconds();
    }
}