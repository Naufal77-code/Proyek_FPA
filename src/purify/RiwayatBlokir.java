package purify;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RiwayatBlokir {

    // 1. Tambahkan field untuk menyimpan data murni (primitive)
    private int nomor;
    private String tanggalMulai;
    private int durasi;
    private String status;
    private String aktivitas;

    // 2. Jadikan semua properti JavaFX sebagai "transient"
    // Ini memberitahu XStream untuk MENGABAIKAN field ini saat menyimpan/memuat
    private transient IntegerProperty nomorProperty;
    private transient StringProperty tanggalMulaiProperty;
    private transient IntegerProperty durasiProperty;
    private transient StringProperty statusProperty;
    private transient StringProperty aktivitasProperty;

    public RiwayatBlokir() {
        // Konstruktor kosong dibutuhkan oleh beberapa framework
    }

    public RiwayatBlokir(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        this.nomor = nomor;
        this.tanggalMulai = tanggalMulai;
        this.durasi = durasi;
        this.status = status;
        this.aktivitas = aktivitas;
    }

    // --- GETTER & SETTER untuk DATA MURNI ---

    public int getNomor() { return nomor; }
    public void setNomor(int nomor) {
        this.nomor = nomor;
        if (nomorProperty != null) nomorProperty.set(nomor);
    }

    public String getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(String tanggalMulai) { this.tanggalMulai = tanggalMulai; }

    public int getDurasi() { return durasi; }
    public void setDurasi(int durasi) { this.durasi = durasi; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAktivitas() { return aktivitas; }
    public void setAktivitas(String aktivitas) {
        this.aktivitas = aktivitas;
        if (aktivitasProperty != null) aktivitasProperty.set(aktivitas);
    }

    // --- PROPERTY GETTERS (untuk TableView) ---
    // Inisialisasi properti saat pertama kali dibutuhkan (lazy initialization)

    public IntegerProperty nomorProperty() {
        if (nomorProperty == null) nomorProperty = new SimpleIntegerProperty(this, "nomor", nomor);
        return nomorProperty;
    }

    public StringProperty tanggalMulaiProperty() {
        if (tanggalMulaiProperty == null) tanggalMulaiProperty = new SimpleStringProperty(this, "tanggalMulai", tanggalMulai);
        return tanggalMulaiProperty;
    }

    public IntegerProperty durasiProperty() {
        if (durasiProperty == null) durasiProperty = new SimpleIntegerProperty(this, "durasi", durasi);
        return durasiProperty;
    }

    public StringProperty statusProperty() {
        if (statusProperty == null) statusProperty = new SimpleStringProperty(this, "status", status);
        return statusProperty;
    }

    public StringProperty aktivitasProperty() {
        if (aktivitasProperty == null) aktivitasProperty = new SimpleStringProperty(this, "aktivitas", aktivitas);
        return aktivitasProperty;
    }

    /**
     * 3. Metode ini dipanggil secara otomatis oleh XStream setelah objek dibuat dari XML.
     * Fungsinya untuk menginisialisasi ulang semua field 'transient'.
     */
    private Object readResolve() {
        nomorProperty();
        tanggalMulaiProperty();
        durasiProperty();
        statusProperty();
        aktivitasProperty();
        return this;
    }
}