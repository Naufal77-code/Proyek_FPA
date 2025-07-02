package purify;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;

public class RiwayatBlokir {
    private final IntegerProperty nomor;
    private final StringProperty tanggalMulai;
    private final IntegerProperty durasi;
    private final StringProperty status;
    private final StringProperty aktivitas;

    public RiwayatBlokir() {
        this(0, "", 0, "", "");
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

    public IntegerProperty nomorProperty() {
        return nomor;
    }


    public String getTanggalMulai() {
        return tanggalMulai.get();
    }

    public void setTanggalMulai(String tanggalMulai) {
        this.tanggalMulai.set(tanggalMulai);
    }

    public StringProperty tanggalMulaiProperty() {
        return tanggalMulai;
    }


    public int getDurasi() {
        return durasi.get();
    }

    public void setDurasi(int durasi) {
        this.durasi.set(durasi);
    }

    public IntegerProperty durasiProperty() {
        return durasi;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getAktivitas() {
        return aktivitas.get();
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas.set(aktivitas);
    }

    public StringProperty aktivitasProperty() {
        return aktivitas;
    }

    @Override
    public String toString() {
        return "RiwayatBlokir{" +
                "nomor=" + getNomor() +
                ", tanggalMulai='" + getTanggalMulai() + '\'' +
                ", durasi=" + getDurasi() +
                ", status='" + getStatus() + '\'' +
                ", aktivitas='" + getAktivitas() + '\'' +
                '}';
    }
}