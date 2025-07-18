package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RiwayatBlokirAppsList {
    private static final Logger logger = Logger.getLogger(RiwayatBlokirAppsList.class.getName());
    private static final String XML_FILE = "riwayat_blokir_apps.xml";

    private final ObservableList<RiwayatBlokirApps> dataList;

    public RiwayatBlokirAppsList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML();
    }

    public ObservableList<RiwayatBlokirApps> getData() {
        return this.dataList;
    }

    public void setData(int nomor, String tanggalMulai, int durasi, String status, String aktivitas,
            String appsBlokir) {
        RiwayatBlokirApps newRiwayat = new RiwayatBlokirApps(
                nomor,
                (tanggalMulai == null) ? "" : tanggalMulai,
                durasi,
                (status == null) ? "" : status,
                (aktivitas == null || aktivitas.trim().isEmpty()) ? "Aktivitas tidak ada" : aktivitas.trim(),
                (appsBlokir == null || appsBlokir.trim().isEmpty()) ? "Tidak ada aplikasi" : appsBlokir.trim());

        dataList.add(newRiwayat);
        saveToXML();
    }

    public void remove(int nomor) {
        if (nomor > 0 && nomor <= dataList.size()) {
            dataList.remove(nomor - 1);
            updateNomor();
            saveToXML();
        }
    }

    public void editAktivitas(int nomor, String aktivitasBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokirApps riwayat = dataList.get(nomor - 1);
            riwayat.setAktivitas((aktivitasBaru == null || aktivitasBaru.trim().isEmpty())
                    ? "Aktivitas tidak ada"
                    : aktivitasBaru.trim());
            saveToXML();
        }
    }

    public void editAppsBlokir(int nomor, String appsBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokirApps riwayat = dataList.get(nomor - 1);
            riwayat.setAppsBlokir((appsBaru == null || appsBaru.trim().isEmpty())
                    ? "Tidak ada aplikasi"
                    : appsBaru.trim());
            saveToXML();
        }
    }

    public void clear() {
        dataList.clear();
        saveToXML();
    }

    private void updateNomor() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setNomor(i + 1);
        }
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);

        xstream.allowTypesByWildcard(new String[] {
                "Purify.**",
                "java.util.*"
        });

        xstream.alias("RiwayatBlokirApps", RiwayatBlokirApps.class);
        xstream.alias("list", java.util.List.class);

        return xstream;
    }

    public void saveToXML() {
        updateNomor();
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            ArrayList<RiwayatBlokirApps> plainList = new ArrayList<>(this.dataList);
            xstream.toXML(plainList, fos);
            logger.log(Level.INFO, "Data riwayat apps berhasil disimpan ke {0}", XML_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan data riwayat apps ke XML", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromXML() { // <- Ubah jadi PUBLIC
        File file = new File(XML_FILE);
        if (!file.exists()) {
            logger.log(Level.INFO, "File {0} tidak ditemukan.", XML_FILE);
            return;
        }

        XStream xstream = createXStream();

        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<RiwayatBlokirApps> loadedList = (ArrayList<RiwayatBlokirApps>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList);
                logger.log(Level.INFO, "Data riwayat apps berhasil dimuat dari {0}", XML_FILE);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saat parsing XML. File lama akan di-backup.", e);

            File backupFile = new File(XML_FILE + ".corrupted_" + System.currentTimeMillis());
            if (file.renameTo(backupFile)) {
                logger.log(Level.INFO, "File lama di-backup sebagai {0}", backupFile.getName());
            } else {
                logger.log(Level.SEVERE, "Gagal melakukan backup file lama.");
            }
        }
    }
}
