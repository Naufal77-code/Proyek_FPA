package purify;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Kelas ini merepresentasikan data psikolog untuk ditampilkan di antarmuka JavaFX,
// termasuk dukungan binding properti agar data otomatis tersinkron dengan UI.
public class Psikolog {

    // ===== ATTRIBUT UTAMA =====

    // Menyimpan nama psikolog dalam bentuk string biasa (non-bindable)
    private String nama;

    // Menyimpan spesialisasi psikolog (misal: "Psikolog Klinis", "Psikolog Anak")
    private String spesialisasi;

    // Menyimpan status ketersediaan psikolog (misal: "Tersedia", "Sibuk",
    // "Offline")
    private String status;

    // ===== PROPERTI UNTUK UI (BINDING DENGAN TABLEVIEW) =====

    // Properti JavaFX yang digunakan untuk binding ke elemen GUI (misal:
    // TableColumn)
    // transient: agar properti ini tidak ikut disimpan saat serialisasi (misalnya
    // ke file XML)
    private transient StringProperty namaProperty;
    private transient StringProperty spesialisasiProperty;
    private transient StringProperty statusProperty;

    // ===== KONSTRUKTOR =====

    // Konstruktor default diperlukan oleh XStream untuk deserialisasi objek dari
    // XML
    public Psikolog() {
    }

    // Konstruktor untuk inisialisasi cepat data psikolog
    public Psikolog(String nama, String spesiallisasi, String status) {
        this.nama = nama;
        this.spesialisasi = spesiallisasi; // ⚠️ Typo: 'spesiallisasi' sebaiknya diganti jadi 'spesialisasi'
        this.status = status;
    }

    // ===== GETTER & SETTER =====

    // Mengembalikan nama psikolog
    public String getNama() {
        return nama;
    }

    // Mengatur nama psikolog dan update ke properti UI jika sudah dibuat
    public void setNama(String nama) {
        this.nama = nama;
        if (namaProperty != null)
            namaProperty.set(nama); // Jika properti sudah ada, sinkronkan nilainya
    }

    // Mengembalikan spesialisasi psikolog
    public String getSpesialisasi() {
        return spesialisasi;
    }

    // Mengatur spesialisasi dan update ke properti UI jika sudah dibuat
    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
        if (spesialisasiProperty != null)
            spesialisasiProperty.set(spesialisasi);
    }

    // Mengembalikan status psikolog
    public String getStatus() {
        return status;
    }

    // Mengatur status dan update ke properti UI jika sudah dibuat
    public void setStatus(String status) {
        this.status = status;
        if (statusProperty != null)
            statusProperty.set(status);
    }

    // ===== PROPERTI BINDING UNTUK TABLEVIEW =====

    // Properti nama untuk TableView. Akan dibuat saat dibutuhkan (lazy
    // initialization)
    public StringProperty namaProperty() {
        if (namaProperty == null)
            namaProperty = new SimpleStringProperty(this, "nama", nama);
        return namaProperty;
    }

    // Properti spesialisasi untuk TableView
    public StringProperty spesialisasiProperty() {
        if (spesialisasiProperty == null)
            spesialisasiProperty = new SimpleStringProperty(this, "spesialisasi", spesialisasi);
        return spesialisasiProperty;
    }

    // Properti status untuk TableView
    public StringProperty statusProperty() {
        if (statusProperty == null)
            statusProperty = new SimpleStringProperty(this, "status", status);
        return statusProperty;
    }

    // ===== METODE OTOMATIS SETELAH DESERIALISASI =====

    /**
     * Dipanggil secara otomatis oleh XStream setelah objek selesai dimuat dari XML.
     * Digunakan untuk menginisialisasi ulang properti transient agar bisa digunakan
     * lagi di UI.
     */
    private Object readResolve() {
        namaProperty(); // Inisialisasi ulang namaProperty jika perlu
        spesialisasiProperty(); // Inisialisasi ulang spesialisasiProperty jika perlu
        statusProperty(); // Inisialisasi ulang statusProperty jika perlu
        return this;
    }

    // ===== OVERRIDE METODE toString =====

    // Berguna untuk debug/log: mencetak isi objek ke konsol/log
    @Override
    public String toString() {
        return "Psikolog{" +
                "nama='" + nama + '\'' +
                ", spesialisasi='" + spesialisasi + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
