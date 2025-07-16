package purify;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RiwayatBlokir {
    private int nomor;
    private String tanggalMulai;
    private int durasi;
    private String status;
    private String aktivitas;

    public RiwayatBlokir() {
    }

    public RiwayatBlokir(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        this.nomor = nomor;
        this.tanggalMulai = tanggalMulai;
        this.durasi = durasi;
        this.status = status;
        this.aktivitas = aktivitas;
    }

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

    public IntegerProperty nomorProperty() {
        return new SimpleIntegerProperty(nomor);
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