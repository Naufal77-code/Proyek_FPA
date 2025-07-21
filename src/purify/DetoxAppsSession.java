package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration; // Import Duration
import java.util.ArrayList;
import java.util.List;

public class DetoxAppsSession {
    private static DetoxAppsSession instance;
    private int durasi;
    private String aktivitas;
    private String kodeDarurat;
    private LocalDateTime waktuMulai;
    private boolean isActive;
    private List<String> selectedApps;

    private DetoxAppsSession() {
        this.isActive = false;
        this.durasi = 0;
        this.aktivitas = "";
        this.kodeDarurat = "";
        this.waktuMulai = null;
        this.selectedApps = new ArrayList<>();
    }

    public static DetoxAppsSession getInstance() {
        if (instance == null) {
            instance = new DetoxAppsSession();
        }
        return instance;
    }

    public void startDetox(int durasiDetik, String aktivitas, String kodeDarurat, List<String> selectedApps) {
        if (durasiDetik <= 0) {
            throw new IllegalArgumentException("Durasi harus lebih dari 0 detik");
        }
        if (kodeDarurat == null || kodeDarurat.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode darurat tidak boleh kosong");
        }
        if (selectedApps == null || selectedApps.isEmpty()) {
            throw new IllegalArgumentException("Pilih minimal satu aplikasi untuk diblokir");
        }
        this.durasi = durasiDetik;
        this.aktivitas = (aktivitas == null || aktivitas.trim().isEmpty()) 
            ? "Aktivitas tidak ada" 
            : aktivitas.trim();
        this.kodeDarurat = kodeDarurat.trim();
        this.waktuMulai = LocalDateTime.now();
        this.isActive = true;
        this.selectedApps = new ArrayList<>(selectedApps);
    }

    public int getDurasiInMinutes() {
        return (int) Math.ceil(durasi / 60.0);
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
        this.selectedApps.clear();
    }

    // --- [TAMBAHAN] Metode untuk menghitung durasi berjalan ---
    public long getActualElapsedSeconds() {
        if (!isActive || waktuMulai == null) {
            return 0;
        }
        return Duration.between(waktuMulai, LocalDateTime.now()).getSeconds();
    }

    // Getters
    public int getDurasi() { return durasi; }
    public String getAktivitas() { return aktivitas; }
    public String getKodeDarurat() { return kodeDarurat; }
    public LocalDateTime getWaktuMulai() { return waktuMulai; }
    public boolean isActive() { return isActive; }

    public List<String> getSelectedApps() {
        return new ArrayList<>(selectedApps);
    }

    public String getSelectedAppsString() {
        if (selectedApps.isEmpty()) {
            return "Tidak ada aplikasi";
        }
        return String.join(", ", selectedApps);
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
        LocalDateTime endTime = waktuMulai.plusSeconds(durasi);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(endTime)) {
            return 0;
        }
        return Duration.between(now, endTime).getSeconds();
    }
}