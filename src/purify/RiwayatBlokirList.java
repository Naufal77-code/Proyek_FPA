package tes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RiwayatBlokirList {
    private ObservableList<RiwayatBlokir> dataList;

    public RiwayatBlokirList() {
        dataList = FXCollections.observableArrayList();
    }

    public ObservableList<RiwayatBlokir> getData() {
        return this.dataList;
    }

    public void setData(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        dataList.add(new RiwayatBlokir(nomor, tanggalMulai, durasi, status, aktivitas));
    }

    public void removeOldest() {
        if (!dataList.isEmpty()) {
            dataList.remove(0);
            for (int i = 0; i < dataList.size(); i++) {
                dataList.get(i).setNomor(i + 1);
            }
        }
    }

    public void editAktivitas(int nomor, String aktivitasBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokir riwayat = dataList.get(nomor - 1);
            if (aktivitasBaru.trim().isEmpty()) {
                riwayat.setAktivitas("Aktivitas tidak ada");
            } else {
                riwayat.setAktivitas(aktivitasBaru);
            }
        }
    }
}
