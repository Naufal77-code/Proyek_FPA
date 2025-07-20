package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RiwayatBlokirAppsList {
    private static final Logger logger = Logger.getLogger(RiwayatBlokirAppsList.class.getName());

    private static final String APP_FOLDER = System.getProperty("user.home") + File.separator + "Purify";
    private static final String XML_FILE = APP_FOLDER + File.separator + "riwayat_blokir_apps.xml";
    private static final String TEMP_FILE = APP_FOLDER + File.separator + "riwayat_blokir_apps.tmp";

    private static RiwayatBlokirAppsList instance;
    private ObservableList<RiwayatBlokirApps> dataList = FXCollections.observableArrayList();

    public static RiwayatBlokirAppsList getInstance() {
        if (instance == null) {
            instance = new RiwayatBlokirAppsList();
        }
        return instance;
    }

    public ObservableList<RiwayatBlokirApps> getData() {
        return dataList;
    }

    public synchronized void addData(String tanggalMulai, int durasi, String status, String aktivitas, String appsBlokir) {
        RiwayatBlokirApps newRiwayat = new RiwayatBlokirApps(
            dataList.size() + 1,
            tanggalMulai == null ? "" : tanggalMulai,
            durasi,
            status == null ? "" : status,
            aktivitas == null || aktivitas.trim().isEmpty() ? "Aktivitas tidak ada" : aktivitas.trim(),
            appsBlokir == null || appsBlokir.trim().isEmpty() ? "Tidak ada aplikasi" : appsBlokir.trim()
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
        xstream.allowTypesByWildcard(new String[] { "purify.**", "java.util.*" });
        xstream.alias("RiwayatBlokirApps", RiwayatBlokirApps.class);
        xstream.alias("list", java.util.List.class);
        return xstream;
    }

    public synchronized boolean saveToXML() {
        updateNomor();
        XStream xstream = createXStream();

        try {
            Files.createDirectories(Paths.get(APP_FOLDER));  // pastikan folder ada

            File tempFile = new File(TEMP_FILE);
            File realFile = new File(XML_FILE);

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                ArrayList<RiwayatBlokirApps> plainList = new ArrayList<>(this.dataList);
                xstream.toXML(plainList, fos);
                fos.flush();
            }

            Files.move(tempFile.toPath(), realFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);

            logger.log(Level.INFO, "Data riwayat apps berhasil disimpan ke {0}", realFile.getAbsolutePath());
            return true;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan data riwayat apps ke XML", e);
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized boolean loadFromXML() {
        File file = new File(XML_FILE);

        if (!file.exists()) {
            logger.log(Level.INFO, "File XML tidak ditemukan di {0}", XML_FILE);
            return false;
        }

        XStream xstream = createXStream();

        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<RiwayatBlokirApps> loadedList = (ArrayList<RiwayatBlokirApps>) xstream.fromXML(fis);

            if (loadedList != null) {
                dataList.setAll(loadedList);
                logger.log(Level.INFO, "Data riwayat apps berhasil dimuat dari {0}", XML_FILE);
                return true;
            } else {
                logger.log(Level.WARNING, "File XML kosong atau tidak valid");
                return false;
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Gagal memuat data dari XML. File TIDAK dihapus otomatis.", e);
            // Jangan hapus file! Lebih baik user perbaiki manual.
            return false;
        }
    }

    public synchronized boolean reloadData() {
        boolean loaded = loadFromXML();
        updateNomor();
        return loaded;
    }
}
