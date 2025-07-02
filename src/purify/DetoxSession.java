package purify;

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
        this.durasi = 0;
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

    public void startDetox(int durasi, String aktivitas, String kodeDarurat) {
        // Validate inputs
        if (durasi <= 0) {
            throw new IllegalArgumentException("Durasi harus lebih dari 0");
        }
        if (kodeDarurat == null || kodeDarurat.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode darurat tidak boleh kosong");
        }
        
        this.durasi = durasi;
        this.aktivitas = (aktivitas == null || aktivitas.trim().isEmpty()) ? "Aktivitas tidak ada" : aktivitas.trim();
        this.kodeDarurat = kodeDarurat.trim();
        this.waktuMulai = LocalDateTime.now();
        this.isActive = true;
    }

    public void endDetox() {
        this.isActive = false;
    }

    public void reset() {
        this.durasi = 0;
        this.aktivitas = "";
        this.kodeDarurat = "";
        this.waktuMulai = null;
        this.isActive = false;
    }

    // Getters
    public int getDurasi() { 
        return durasi; 
    }
    
    public String getAktivitas() { 
        return aktivitas; 
    }
    
    public String getKodeDarurat() { 
        return kodeDarurat; 
    }
    
    public LocalDateTime getWaktuMulai() { 
        return waktuMulai; 
    }
    
    public boolean isActive() { 
        return isActive; 
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
        
        LocalDateTime endTime = waktuMulai.plusMinutes(durasi);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(endTime)) {
            return 0;
        }
        
        return java.time.Duration.between(now, endTime).getSeconds();
    }

    public boolean isExpired() {
        return isActive && getRemainingTimeInSeconds() <= 0;
    }

    @Override
    public String toString() {
        return "DetoxSession{" +
                "durasi=" + durasi +
                ", aktivitas='" + aktivitas + '\'' +
                ", kodeDarurat='" + kodeDarurat + '\'' +
                ", waktuMulai=" + waktuMulai +
                ", isActive=" + isActive +
                '}';
    }
}