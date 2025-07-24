package purify;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model data untuk janji temu (appointment) antara pengguna dan psikolog.
 * Class ini menggunakan JavaFX Property agar dapat di-bind ke elemen UI secara
 * reaktif.
 */
public class Appointment {

    // ===== Field data biasa (untuk disimpan & dibaca dari file XML) =====

    // Nama psikolog yang dituju
    private String psikologNama;

    // Tanggal janji temu
    private String tanggal;

    // Waktu janji temu
    private String waktu;

    // Lokasi pertemuan atau konsultasi
    private String lokasi;

    // Status janji temu (misalnya: "Dijadwalkan", "Selesai", "Dibatalkan")
    private String status;

    // ===== Property JavaFX (transient agar tidak diserialisasi ke XML) =====

    // Property digunakan untuk bind ke UI (TableView, Label, dll)
    // `transient` digunakan agar properti ini tidak ikut disimpan saat serialisasi
    // XML
    private transient StringProperty psikologNamaProperty;
    private transient StringProperty tanggalProperty;
    private transient StringProperty waktuProperty;
    private transient StringProperty lokasiProperty;
    private transient StringProperty statusProperty;

    // ===== Konstruktor =====

    /**
     * Konstruktor default (dibutuhkan oleh XStream saat deserialisasi dari XML)
     */
    public Appointment() {
    }

    /**
     * Konstruktor lengkap untuk membuat appointment baru
     */
    public Appointment(String psikologNama, String tanggal, String waktu, String lokasi, String status) {
        this.psikologNama = psikologNama;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.lokasi = lokasi;
        this.status = status;
    }

    // ===== Getter & Setter untuk field biasa =====

    public String getPsikologNama() {
        return psikologNama;
    }

    public void setPsikologNama(String psikologNama) {
        this.psikologNama = psikologNama;
        // Jika properti telah diinisialisasi, sinkronkan nilai barunya
        if (psikologNamaProperty != null)
            psikologNamaProperty.set(psikologNama);
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
        if (tanggalProperty != null)
            tanggalProperty.set(tanggal);
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
        if (waktuProperty != null)
            waktuProperty.set(waktu);
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
        if (lokasiProperty != null)
            lokasiProperty.set(lokasi);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if (statusProperty != null)
            statusProperty.set(status);
    }

    // ===== Property-method JavaFX =====
    // Method ini akan membuat property jika belum dibuat, lalu mengembalikannya.
    // Properti ini berguna untuk menghubungkan data ke UI yang dapat bereaksi
    // terhadap perubahan nilai.

    public StringProperty psikologNamaProperty() {
        if (psikologNamaProperty == null)
            psikologNamaProperty = new SimpleStringProperty(this, "psikologNama", psikologNama);
        return psikologNamaProperty;
    }

    public StringProperty tanggalProperty() {
        if (tanggalProperty == null)
            tanggalProperty = new SimpleStringProperty(this, "tanggal", tanggal);
        return tanggalProperty;
    }

    public StringProperty waktuProperty() {
        if (waktuProperty == null)
            waktuProperty = new SimpleStringProperty(this, "waktu", waktu);
        return waktuProperty;
    }

    public StringProperty lokasiProperty() {
        if (lokasiProperty == null)
            lokasiProperty = new SimpleStringProperty(this, "lokasi", lokasi);
        return lokasiProperty;
    }

    public StringProperty statusProperty() {
        if (statusProperty == null)
            statusProperty = new SimpleStringProperty(this, "status", status);
        return statusProperty;
    }

    // ===== Metode khusus saat object di-deserialize dari XML =====

    /**
     * Metode ini akan dipanggil otomatis oleh XStream setelah objek di-deserialize
     * dari XML.
     * Tujuannya adalah mengembalikan properti JavaFX agar dapat digunakan kembali
     * oleh UI.
     */
    private Object readResolve() {
        psikologNamaProperty();
        tanggalProperty();
        waktuProperty();
        lokasiProperty();
        statusProperty();
        return this;
    }
}
