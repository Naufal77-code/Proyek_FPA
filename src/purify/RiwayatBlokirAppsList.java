package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RiwayatBlokirAppsList {
    private static final Logger logger = Logger.getLogger(RiwayatBlokirAppsList.class.getName());
    private static final String XML_FILE = "riwayat_blokir_apps.xml";
    private static final String TEMP_FILE = "riwayat_blokir_apps.tmp";

    private static RiwayatBlokirAppsList instance;
    private ObservableList<RiwayatBlokirApps> dataList = FXCollections.observableArrayList();

    public RiwayatBlokirAppsList() {}

    public static RiwayatBlokirAppsList getInstance() {
        if (instance == null) {
            instance = new RiwayatBlokirAppsList();
        }
        return instance;
    }

    public ObservableList<RiwayatBlokirApps> getData() {
        return dataList;
    }

    public void addData(RiwayatBlokirApps riwayat) {
        dataList.add(riwayat);
    }

    public synchronized void setData(int nomor, String tanggalMulai, int durasi, String status, String aktivitas, String appsBlokir) {
        RiwayatBlokirApps newRiwayat = new RiwayatBlokirApps(
                nomor,
                (tanggalMulai == null) ? "" : tanggalMulai,
                durasi,
                (status == null) ? "" : status,
                (aktivitas == null || aktivitas.trim().isEmpty()) ? "Aktivitas tidak ada" : aktivitas.trim(),
                (appsBlokir == null || appsBlokir.trim().isEmpty()) ? "Tidak ada aplikasi" : appsBlokir.trim()
        );

        dataList.add(newRiwayat);
        saveToXML();
    }

    public synchronized void remove(int nomor) {
        if (nomor > 0 && nomor <= dataList.size()) {
            dataList.remove(nomor - 1);
            updateNomor();
            saveToXML();
        }
    }

    public synchronized void editAktivitas(int nomor, String aktivitasBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokirApps riwayat = dataList.get(nomor - 1);
            riwayat.setAktivitas((aktivitasBaru == null || aktivitasBaru.trim().isEmpty())
                    ? "Aktivitas tidak ada"
                    : aktivitasBaru.trim());
            saveToXML();
        }
    }

    public synchronized void editAppsBlokir(int nomor, String appsBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokirApps riwayat = dataList.get(nomor - 1);
            riwayat.setAppsBlokir((appsBaru == null || appsBaru.trim().isEmpty())
                    ? "Tidak ada aplikasi"
                    : appsBaru.trim());
            saveToXML();
        }
    }

    public synchronized void clear() {
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
        xstream.allowTypesByWildcard(new String[]{"purify.**", "java.util.*"});
        xstream.alias("RiwayatBlokirApps", RiwayatBlokirApps.class);
        xstream.alias("list", java.util.List.class);
        return xstream;
    }

    public synchronized boolean saveToXML() {
        updateNomor();
        XStream xstream = createXStream();
        File tempFile = new File(TEMP_FILE);
        File realFile = new File(XML_FILE);

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            ArrayList<RiwayatBlokirApps> plainList = new ArrayList<>(this.dataList);
            xstream.toXML(plainList, fos);

            Files.move(tempFile.toPath(), realFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);

            logger.log(Level.INFO, "Data riwayat apps berhasil disimpan ke {0}", XML_FILE);
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan data riwayat apps ke XML", e);
            if (tempFile.exists()) {
                tempFile.delete();
            }
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized boolean loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists()) {
            logger.log(Level.INFO, "File {0} tidak ditemukan.", XML_FILE);
            return false;
        }

        XStream xstream = createXStream();

        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<RiwayatBlokirApps> loadedList = (ArrayList<RiwayatBlokirApps>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList);
                logger.log(Level.INFO, "Data riwayat apps berhasil dimuat dari {0}", XML_FILE);
                return true;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saat parsing XML.", e);

            try {
                if (file.delete()) {
                    logger.log(Level.INFO, "File korup dihapus dan akan dibuat baru");
                    dataList.clear();
                    saveToXML();
                }
            } catch (SecurityException ex) {
                logger.log(Level.SEVERE, "Tidak bisa menghapus file korup", ex);
            }
        }
        return false;
    }

    public synchronized boolean reloadData() {
        boolean loaded = loadFromXML();
        updateNomor();
        return loaded;
    }
}
