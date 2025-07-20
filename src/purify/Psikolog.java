package purify;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Psikolog {

    private String nama;
    private String spesialisasi;
    private String status;

    private transient StringProperty namaProperty;
    private transient StringProperty spesialisasiProperty;
    private transient StringProperty statusProperty;

    public Psikolog() {

    }

    public Psikolog(String nama, String spesiallisasi, String status) {
        this.nama = nama;
        this.spesialisasi = spesialisasi;
        this.status = status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
        if (namaProperty != null)
            namaProperty.set(nama);
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
        if (spesialisasiProperty != null)
            spesialisasiProperty.set(spesialisasi);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if (statusProperty != null)
            statusProperty.set(status);
    }

    // --- PROPERTY GETTERS (untuk TableView di JavaFX) ---
    // Inisialisasi properti saat pertama kali dibutuhkan (lazy initialization)
    public StringProperty namaProperty() {
        if (namaProperty == null)
            namaProperty = new SimpleStringProperty(this, "nama", nama);
        return namaProperty;
    }

    public StringProperty spesialisasiProperty() {
        if (spesialisasiProperty == null)
            spesialisasiProperty = new SimpleStringProperty(this, "spesialisasi", spesialisasi);
        return spesialisasiProperty;
    }

    public StringProperty statusProperty() {
        if (statusProperty == null)
            statusProperty = new SimpleStringProperty(this, "status", status);
        return statusProperty;
    }

    /**
     * Metode ini dipanggil secara otomatis oleh XStream setelah objek dibuat dari
     * XML.
     * Fungsinya untuk menginisialisasi ulang semua field 'transient'.
     */
    private Object readResolve() {
        namaProperty();
        spesialisasiProperty();
        statusProperty();
        return this;
    }

    @Override
    public String toString() {
        return "Psikolog{" +
                "nama='" + nama + '\'' +
                ", spesialisasi='" + spesialisasi + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
