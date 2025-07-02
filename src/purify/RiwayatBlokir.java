package tes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class RiwayatBlokir {
<<<<<<< HEAD:src/purify/RiwayatBlokir.java
    private final SimpleStringProperty nomor;
    private final SimpleStringProperty riwayat;
    
    public RiwayatBlokir(String nomor, String riwayat) {
        this.nomor = new SimpleStringProperty(nomor);
        this.riwayat = new SimpleStringProperty(riwayat);
=======
    private SimpleIntegerProperty nomor;
    private SimpleStringProperty tanggalMulai;
    private SimpleIntegerProperty durasi;
    private SimpleStringProperty status;
    private SimpleStringProperty aktivitas;

    public RiwayatBlokir() {
        this(0, "", 0, "", "");
>>>>>>> 58019ee (baru):src/tes/RiwayatBlokir.java
    }

    public RiwayatBlokir(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        this.nomor = new SimpleIntegerProperty(nomor);
        this.tanggalMulai = new SimpleStringProperty(tanggalMulai);
        this.durasi = new SimpleIntegerProperty(durasi);
        this.status = new SimpleStringProperty(status);
        this.aktivitas = new SimpleStringProperty(aktivitas);
    }

    public int getNomor() {
        return nomor.get();
    }

    public void setNomor(int nomor) {
        this.nomor.set(nomor);
    }

    public String getTanggalMulai() {
        return tanggalMulai.get();
    }

    public void setTanggalMulai(String tanggalMulai) {
        this.tanggalMulai.set(tanggalMulai);
    }

    public int getDurasi() {
        return durasi.get();
    }

    public void setDurasi(int durasi) {
        this.durasi.set(durasi);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public String getAktivitas() {
        return aktivitas.get();
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas.set(aktivitas);
    }
}
