package purify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.logging.Logger;

public class RiwayatBlokirList {
    private static final Logger logger = Logger.getLogger(RiwayatBlokirList.class.getName());
    private static final String XML_FILE = "riwayat_blokir.xml";

    private final ObservableList<RiwayatBlokir> dataList;

    public RiwayatBlokirList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML();
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
        saveToXML();
    }

    public void removeOldest() {
        if (!dataList.isEmpty()) {
            dataList.remove(0);
            updateNomor();
            saveToXML();
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
            saveToXML();
        }
    }

    public void clear() {
        dataList.clear();
        saveToXML();
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
            saveToXML();
        }
    }

    @Override
    public String toString() {
        return "RiwayatBlokirList{" +
                "size=" + dataList.size() +
                ", data=" + dataList +
                '}';
    }

    public void saveToXML(){
    XStream xstream = new XStream(new DomDriver());
    xstream.alias("RiwayatBlokir", RiwayatBlokir.class);
    xstream.alias("RiwayatBlokirList", RiwayatBlokirList.class);

    try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
        xstream.toXML(this,fos);
        logger.log(Level.INFO, "Data riwayat berhasil disimpam ke "+XML_FILE);
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Gagal menyimpan data riwayat ke XML",e);
    }
}

 @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists()) {
            logger.log(Level.INFO, "File " + XML_FILE + " tidak ditemukan, menggunakan data baru");
            return;
        }

        XStream xstream = createSecureXStream();
        xstream.alias("RiwayatBlokir", RiwayatBlokir.class);
        xstream.alias("RiwayatBlokirList", RiwayatBlokirList.class);
        
        try (FileInputStream fis = new FileInputStream(file)) {
            RiwayatBlokirList loadedList = (RiwayatBlokirList) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList.getData());
                logger.log(Level.INFO, "Data riwayat berhasil dimuat dari " + XML_FILE);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal memuat data riwayat dari XML", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saat parsing XML", e);
            // Buat backup file yang rusak
            file.renameTo(new File(XML_FILE + ".corrupted_" + System.currentTimeMillis()));
        }
    }


    private static XStream createSecureXStream() {
        XStream xstream = new XStream(new DomDriver());
        
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(RiwayatBlokir.class);
        xstream.allowTypesByWildcard(new String[] {
            "purify.**"
        });
        
        return xstream;
    }
}