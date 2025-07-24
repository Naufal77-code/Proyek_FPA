package purify;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Kelas model sederhana untuk menyimpan riwayat sesi blokir (tanpa informasi
 * aplikasi).
 * Cocok digunakan pada tampilan ringkasan atau statistik umum.
 */
public class RiwayatBlokir {

    // Nomor urut entri
    private int nomor;

    // Tanggal sesi blokir dimulai
    private String tanggalMulai;

    // Durasi sesi dalam satuan waktu tertentu (misal menit)
    private int durasi;

    // Status sesi (misal: "Berjalan", "Selesai", dll.)
    private String status;

    // Aktivitas yang dilakukan saat sesi
    private String aktivitas;

    /**
     * Konstruktor kosong diperlukan untuk serialisasi atau penggunaan di JavaFX.
     */
    public RiwayatBlokir() {
        // Kosong
    }

    /**
     * Konstruktor penuh untuk mengisi semua atribut riwayat blokir.
     */
    public RiwayatBlokir(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        this.nomor = nomor;
        this.tanggalMulai = tanggalMulai;
        this.durasi = durasi;
        this.status = status;
        this.aktivitas = aktivitas;
    }

    // --- Getter dan Setter data murni (digunakan untuk penyimpanan atau manipulasi
    // data) ---

    public int getNomor() {
        return nomor;
    }

    public void setNomor(int nomor) {
        this.nomor = nomor;
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
    }

    // --- Property getter untuk TableView JavaFX (namun tidak pakai caching /
    // properti real) ---

    public IntegerProperty nomorProperty() {
        return new SimpleIntegerProperty(nomor); // tidak disimpan, jadi setiap panggilan buat objek baru
    }

    public StringProperty tanggalMulaiProperty() {
        return new SimpleStringProperty(tanggalMulai);
    }

    public IntegerProperty durasiProperty() {
        return new SimpleIntegerProperty(durasi);
    }

    public StringProperty statusProperty() {
        return new SimpleStringProperty(status);
    }

    public StringProperty aktivitasProperty() {
        return new SimpleStringProperty(aktivitas);
    }
}
