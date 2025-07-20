package purify;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Appointment {
    private String psikologNama;
    private String tanggal;
    private String waktu;
    private String lokasi;
    private String status;  

    private transient StringProperty psikologNamaProperty;
    private transient StringProperty tanggalProperty;
    private transient StringProperty waktuProperty;
    private transient StringProperty lokasiProperty;
    private transient StringProperty statusProperty;

    public Appointment() {
    }

    public Appointment(String psikologNama, String tanggal, String waktu, String lokasi, String status) {
        this.psikologNama = psikologNama;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.lokasi = lokasi;
        this.status = status;
    }

    public String getPsikologNama() { return psikologNama; }
    public void setPsikologNama(String psikologNama) {
        this.psikologNama = psikologNama;
        if (psikologNamaProperty != null) psikologNamaProperty.set(psikologNama);
    }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
        if (tanggalProperty != null) tanggalProperty.set(tanggal);
    }

    public String getWaktu() { return waktu; }
    public void setWaktu(String waktu) {
        this.waktu = waktu;
        if (waktuProperty != null) waktuProperty.set(waktu);
    }

    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
        if (lokasiProperty != null) lokasiProperty.set(lokasi);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = status;
        if (statusProperty != null) statusProperty.set(status);
    }

    public StringProperty psikologNamaProperty() {
        if (psikologNamaProperty == null) psikologNamaProperty = new SimpleStringProperty(this, "psikologNama", psikologNama);
        return psikologNamaProperty;
    }

    public StringProperty tanggalProperty() {
        if (tanggalProperty == null) tanggalProperty = new SimpleStringProperty(this, "tanggal", tanggal);
        return tanggalProperty;
    }

    public StringProperty waktuProperty() {
        if (waktuProperty == null) waktuProperty = new SimpleStringProperty(this, "waktu", waktu);
        return waktuProperty;
    }

    public StringProperty lokasiProperty() {
        if (lokasiProperty == null) lokasiProperty = new SimpleStringProperty(this, "lokasi", lokasi);
        return lokasiProperty;
    }

    public StringProperty statusProperty() {
        if (statusProperty == null) statusProperty = new SimpleStringProperty(this, "status", status);
        return statusProperty;
    }

    private Object readResolve() {
        psikologNamaProperty();
        tanggalProperty();
        waktuProperty();
        lokasiProperty();
        statusProperty();
        return this;
    }
}

