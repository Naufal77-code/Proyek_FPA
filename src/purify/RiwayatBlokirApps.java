package purify;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Kelas model untuk merepresentasikan satu entri riwayat blokir aplikasi.
 * Kompatibel dengan JavaFX TableView melalui penggunaan properti (binding).
 */
public class RiwayatBlokirApps {

    // --- Atribut data murni (untuk penyimpanan XML) ---

    // Nomor urut riwayat
    private int nomor;

    // Tanggal mulai sesi blokir
    private String tanggalMulai;

    // Durasi blokir (dalam satuan menit atau detik)
    private int durasi;

    // Status blokir, misal: "Selesai", "Berjalan"
    private String status;

    // Aktivitas yang dilakukan selama sesi
    private String aktivitas;

    // Daftar aplikasi yang diblokir (format string)
    private String appsBlokir;

    // --- Property JavaFX untuk binding dengan TableView ---
    // `transient` digunakan agar properti ini tidak diserialisasi oleh XStream

    private transient IntegerProperty nomorProperty;
    private transient StringProperty tanggalMulaiProperty;
    private transient IntegerProperty durasiProperty;
    private transient StringProperty statusProperty;
    private transient StringProperty aktivitasProperty;
    private transient StringProperty appsBlokirProperty;

    /**
     * Konstruktor kosong diperlukan oleh XStream saat melakukan deserialisasi XML.
     */
    public RiwayatBlokirApps() {
        // Kosong
    }

    /**
     * Konstruktor utama yang digunakan untuk mengisi semua data.
     */
    public RiwayatBlokirApps(int nomor, String tanggalMulai, int durasi, String status, String aktivitas,
            String appsBlokir) {
        this.nomor = nomor;
        this.tanggalMulai = tanggalMulai;
        this.durasi = durasi;
        this.status = status;
        this.aktivitas = aktivitas;
        this.appsBlokir = appsBlokir;
    }

    // --- Getter dan Setter untuk data murni (digunakan saat load/save ke XML) ---

    public int getNomor() {
        return nomor;
    }

    public void setNomor(int nomor) {
        this.nomor = nomor;
        if (nomorProperty != null)
            nomorProperty.set(nomor); // sinkronkan property jika sudah diinisialisasi
    }

    public String getTanggalMulai() {
        return tanggalMulai;
    }

    public void setTanggalMulai(String tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public int getDurasi() {
        return durasi;
    }

    public void setDurasi(int durasi) {
        this.durasi = durasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAktivitas() {
        return aktivitas;
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas = aktivitas;
        if (aktivitasProperty != null)
            aktivitasProperty.set(aktivitas); // sinkronkan ke property jika sudah digunakan
    }

    public String getAppsBlokir() {
        return appsBlokir;
    }

    public void setAppsBlokir(String appsBlokir) {
        this.appsBlokir = appsBlokir;
        if (appsBlokirProperty != null)
            appsBlokirProperty.set(appsBlokir);
    }

    // --- Getter untuk property binding JavaFX ---

    public IntegerProperty nomorProperty() {
        if (nomorProperty == null)
            nomorProperty = new SimpleIntegerProperty(this, "nomor", nomor);
        return nomorProperty;
    }

    public StringProperty tanggalMulaiProperty() {
        if (tanggalMulaiProperty == null)
            tanggalMulaiProperty = new SimpleStringProperty(this, "tanggalMulai", tanggalMulai);
        return tanggalMulaiProperty;
    }

    public IntegerProperty durasiProperty() {
        if (durasiProperty == null)
            durasiProperty = new SimpleIntegerProperty(this, "durasi", durasi);
        return durasiProperty;
    }

    public StringProperty statusProperty() {
        if (statusProperty == null)
            statusProperty = new SimpleStringProperty(this, "status", status);
        return statusProperty;
    }

    public StringProperty aktivitasProperty() {
        if (aktivitasProperty == null)
            aktivitasProperty = new SimpleStringProperty(this, "aktivitas", aktivitas);
        return aktivitasProperty;
    }

    public StringProperty appsBlokirProperty() {
        if (appsBlokirProperty == null)
            appsBlokirProperty = new SimpleStringProperty(this, "appsBlokir", appsBlokir);
        return appsBlokirProperty;
    }

    /**
     * Metode khusus yang dipanggil otomatis oleh XStream setelah deserialisasi.
     * Digunakan untuk menginisialisasi ulang property JavaFX yang bersifat
     * transient.
     */
    private Object readResolve() {
        nomorProperty();
        tanggalMulaiProperty();
        durasiProperty();
        statusProperty();
        aktivitasProperty();
        appsBlokirProperty();
        return this;
    }
}
