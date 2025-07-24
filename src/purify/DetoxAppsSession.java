package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration; // Import Duration
import java.util.ArrayList;
import java.util.List;

public class DetoxAppsSession {
    // Singleton instance untuk memastikan hanya ada satu sesi detox aktif
    private static DetoxAppsSession instance;

    // Durasi sesi detox dalam satuan detik
    private int durasi;

    // Aktivitas yang dilakukan pengguna selama sesi detox
    private String aktivitas;

    // Kode darurat yang bisa digunakan untuk membatalkan sesi detox
    private String kodeDarurat;

    // Waktu mulai sesi detox
    private LocalDateTime waktuMulai;

    // Menandai apakah sesi detox sedang berlangsung
    private boolean isActive;

    // Daftar nama aplikasi yang dipilih untuk diblokir
    private List<String> selectedApps;

    // Konstruktor private untuk menerapkan pola Singleton
    private DetoxAppsSession() {
        this.isActive = false;
        this.durasi = 0;
        this.aktivitas = "";
        this.kodeDarurat = "";
        this.waktuMulai = null;
        this.selectedApps = new ArrayList<>();
    }

    // Mengembalikan satu-satunya instance dari DetoxAppsSession
    public static DetoxAppsSession getInstance() {
        if (instance == null) {
            instance = new DetoxAppsSession();
        }
        return instance;
    }

    /**
     * Memulai sesi detox baru dengan parameter yang diberikan.
     * 
     * @param durasiDetik  Durasi sesi dalam detik.
     * @param aktivitas    Deskripsi aktivitas selama detox.
     * @param kodeDarurat  Kode darurat untuk membatalkan detox.
     * @param selectedApps Daftar aplikasi yang akan diblokir.
     */
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

    // Mengembalikan durasi dalam satuan menit (dibulatkan ke atas)
    public int getDurasiInMinutes() {
        return (int) Math.ceil(durasi / 60.0);
    }

    // Menandai sesi detox sebagai telah berakhir (nonaktif)
    public void endDetox() {
        this.isActive = false;
    }

    // Mengatur ulang semua data sesi detox ke kondisi awal
    public void reset() {
        this.durasi = 0;
        this.aktivitas = "";
        this.kodeDarurat = "";
        this.waktuMulai = null;
        this.isActive = false;
        this.selectedApps.clear();
    }

    // Menghitung lama waktu yang sudah berjalan sejak sesi dimulai (dalam detik)
    public long getActualElapsedSeconds() {
        if (!isActive || waktuMulai == null) {
            return 0;
        }
        return Duration.between(waktuMulai, LocalDateTime.now()).getSeconds();
    }

    // Getter untuk durasi dalam detik
    public int getDurasi() {
        return durasi;
    }

    // Getter untuk aktivitas pengguna
    public String getAktivitas() {
        return aktivitas;
    }

    // Getter untuk kode darurat
    public String getKodeDarurat() {
        return kodeDarurat;
    }

    // Getter untuk waktu mulai sesi detox
    public LocalDateTime getWaktuMulai() {
        return waktuMulai;
    }

    // Mengecek apakah sesi detox masih aktif
    public boolean isActive() {
        return isActive;
    }

    // Mengembalikan salinan daftar aplikasi yang diblokir
    public List<String> getSelectedApps() {
        return new ArrayList<>(selectedApps);
    }

    // Mengembalikan daftar aplikasi sebagai satu string, dipisahkan koma
    public String getSelectedAppsString() {
        if (selectedApps.isEmpty()) {
            return "Tidak ada aplikasi";
        }
        return String.join(", ", selectedApps);
    }

    // Mengembalikan waktu mulai dalam format string "dd/MM/yyyy HH:mm"
    public String getFormattedWaktuMulai() {
        if (waktuMulai != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return waktuMulai.format(formatter);
        }
        return "";
    }

    // Menghitung sisa waktu sesi detox dalam detik
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