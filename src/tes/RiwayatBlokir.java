package tes;

import javafx.beans.property.SimpleStringProperty;

public class RiwayatBlokir {
    private final SimpleStringProperty nomor;
    private final SimpleStringProperty riwayat;

    public RiwayatBlokir(String nomor, String riwayat) {
        this.nomor = new SimpleStringProperty(nomor);
        this.riwayat = new SimpleStringProperty(riwayat);
    }

    public String getNomor() {
        return nomor.get();
    }

    public String getRiwayat() {
        return riwayat.get();
    }

}
