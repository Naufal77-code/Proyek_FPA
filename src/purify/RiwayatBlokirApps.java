package purify;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RiwayatBlokirApps {
    private int nomor;
    private String tanggalMulai;
    private int durasi;
    private String status;
    private String aktivitas;
    private String appsBlokir;

    private transient IntegerProperty nomorProperty;
    private transient StringProperty tanggalMulaiProperty;
    private transient IntegerProperty durasiProperty;
    private transient StringProperty statusProperty;
    private transient StringProperty aktivitasProperty;
    private transient StringProperty appsBlokirProperty;

    public RiwayatBlokirApps() {
        // Konstruktor kosong dibutuhkan oleh beberapa framework
    }

    public RiwayatBlokirApps(int nomor, String tanggalMulai, int durasi, String status, String aktivitas, String appsBlokir) {
        this.nomor = nomor;
        this.tanggalMulai = tanggalMulai;
        this.durasi = durasi;
        this.status = status;
        this.aktivitas = aktivitas;
        this.appsBlokir = appsBlokir;
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

    public String getAppsBlokir() { return appsBlokir; }
    public void setAppsBlokir(String appsBlokir) {
        this.appsBlokir = appsBlokir;
        if (appsBlokirProperty != null) appsBlokirProperty.set(appsBlokir);
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

    public StringProperty appsBlokirProperty() {
        if (appsBlokirProperty == null) appsBlokirProperty = new SimpleStringProperty(this, "appsBlokir", appsBlokir);
        return appsBlokirProperty;
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
        appsBlokirProperty();
        return this;
    }
}

