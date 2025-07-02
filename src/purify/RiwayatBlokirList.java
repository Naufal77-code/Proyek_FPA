package purify;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RiwayatBlokirList {
    private final ObservableList<RiwayatBlokir> dataList;

    public RiwayatBlokirList() {
        dataList = FXCollections.observableArrayList();
    }

    public ObservableList<RiwayatBlokir> getData() {
        return this.dataList;
    }

    public void setData(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        if (tanggalMulai == null) tanggalMulai = "";
        if (status == null) status = "";
        if (aktivitas == null || aktivitas.trim().isEmpty()) {
            aktivitas = "Aktivitas tidak ada";
        }
        
        RiwayatBlokir newRiwayat = new RiwayatBlokir(nomor, tanggalMulai, durasi, status, aktivitas);
        dataList.add(newRiwayat);
    }

    public void removeOldest() {
        if (!dataList.isEmpty()) {
            dataList.remove(0);
            updateNomor();
        }
    }

    public void editAktivitas(int nomor, String aktivitasBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokir riwayat = dataList.get(nomor - 1);
            if (aktivitasBaru == null || aktivitasBaru.trim().isEmpty()) {
                riwayat.setAktivitas("Aktivitas tidak ada");
            } else {
                riwayat.setAktivitas(aktivitasBaru.trim());
            }
        }
    }

    public void clear() {
        dataList.clear();
    }

    public boolean isEmpty() {
        return dataList.isEmpty();
    }

    public int size() {
        return dataList.size();
    }

    public RiwayatBlokir get(int index) {
        if (index >= 0 && index < dataList.size()) {
            return dataList.get(index);
        }
        return null;
    }

    private void updateNomor() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setNomor(i + 1);
        }
    }

    public void remove(int nomor) {
        if (nomor > 0 && nomor <= dataList.size()) {
            dataList.remove(nomor - 1);
            updateNomor();
        }
    }

    @Override
    public String toString() {
        return "RiwayatBlokirList{" +
                "size=" + dataList.size() +
                ", data=" + dataList +
                '}';
    }
}