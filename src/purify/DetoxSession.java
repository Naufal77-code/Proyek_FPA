package purify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration; // Import Duration untuk menghitung selisih waktu

/**
 * Kelas DetoxSession bertindak sebagai Singleton yang menyimpan
 * status sesi detox saat ini, seperti durasi, aktivitas, dan waktu mulai.
 */
public class DetoxSession {

    // === Atribut Singleton ===

    private static DetoxSession instance;
    /**
     * Satu-satunya instance dari DetoxSession untuk memastikan hanya ada
     * satu sesi detox aktif pada suatu waktu (pola Singleton).
     */

    // === Atribut Sesi ===

    private long durasiInSeconds; // Durasi sesi dalam detik
    private String aktivitas; // Deskripsi aktivitas selama sesi detox
    private String kodeDarurat; // Kode untuk membatalkan sesi detox
    private LocalDateTime waktuMulai; // Waktu saat sesi detox dimulai
    private boolean isActive; // Status apakah sesi sedang berjalan atau tidak

    // === Konstruktor Private ===

    private DetoxSession() {
        /**
         * Konstruktor privat agar hanya dapat diakses melalui getInstance(),
         * sesuai dengan pola Singleton.
         */
        this.isActive = false;
        this.durasiInSeconds = 0;
        this.aktivitas = "";
        this.kodeDarurat = "";
        this.waktuMulai = null;
    }

    // === Singleton Accessor ===

    public static DetoxSession getInstance() {
        /**
         * Mengembalikan instance tunggal dari DetoxSession.
         * Jika belum ada instance, maka akan dibuat baru.
         */
        if (instance == null) {
            instance = new DetoxSession();
        }
        return instance;
    }

    // === Method untuk Memulai Sesi Detox ===

    public void startDetox(long durasiDetik, String aktivitas, String kodeDarurat) {
        /**
         * Memulai sesi detox dengan parameter yang diberikan.
         *
         * @param durasiDetik Durasi dalam detik
         * @param aktivitas   Nama aktivitas detox
         * @param kodeDarurat Kode darurat untuk membatalkan sesi
         *
         *                    Validasi input:
         *                    - Durasi harus > 0
         *                    - Kode darurat tidak boleh kosong
         */
        if (durasiDetik <= 0) {
            throw new IllegalArgumentException("Durasi harus lebih dari 0 detik");
        }
        if (kodeDarurat == null || kodeDarurat.trim().isEmpty()) {
            throw new IllegalArgumentException("Kode darurat tidak boleh kosong");
        }

        this.durasiInSeconds = durasiDetik;
        this.aktivitas = (aktivitas == null || aktivitas.trim().isEmpty())
                ? "Aktivitas tidak ada"
                : aktivitas.trim();
        this.kodeDarurat = kodeDarurat.trim();
        this.waktuMulai = LocalDateTime.now();
        this.isActive = true;
    }

    public void endDetox() {
        /**
         * Mengakhiri sesi detox saat ini.
         */
        this.isActive = false;
    }

    public long getDurasiInSeconds() {
        /**
         * Mengembalikan total durasi sesi detox dalam satuan detik.
         */
        return durasiInSeconds;
    }

    public int getDurasiInMinutes() {
        /**
         * Mengembalikan durasi dalam satuan menit (pembulatan ke atas jika ada
         * desimal).
         */
        return (int) Math.round(durasiInSeconds / 60.0);
    }

    public long getActualElapsedSeconds() {
        /**
         * Mengembalikan waktu yang telah berjalan sejak sesi dimulai (dalam detik).
         * Jika sesi belum dimulai, kembalikan 0.
         */
        if (!isActive || waktuMulai == null) {
            return 0;
        }
        return Duration.between(waktuMulai, LocalDateTime.now()).getSeconds();
    }

    public String getAktivitas() {
        /**
         * Mengembalikan nama aktivitas detox.
         */
        return aktivitas;
    }

    public String getKodeDarurat() {
        /**
         * Mengembalikan kode darurat yang digunakan untuk membatalkan sesi detox.
         */
        return kodeDarurat;
    }

    public String getFormattedWaktuMulai() {
        /**
         * Mengembalikan waktu mulai sesi detox dalam format "dd/MM/yyyy HH:mm".
         * Jika sesi belum dimulai, mengembalikan string kosong.
         */
        if (waktuMulai != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return waktuMulai.format(formatter);
        }
        return "";
    }

    public long getRemainingTimeInSeconds() {
        /**
         * Menghitung sisa waktu yang tersisa dalam sesi detox.
         * Jika sesi sudah selesai, mengembalikan 0.
         */
        if (!isActive || waktuMulai == null) {
            return 0;
        }
        LocalDateTime endTime = waktuMulai.plusSeconds(durasiInSeconds);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(endTime)) {
            return 0;
        }
        return Duration.between(now, endTime).getSeconds();
    }
}
