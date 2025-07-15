package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RiwayatBlokirList {
    private static final Logger logger = Logger.getLogger(RiwayatBlokirList.class.getName());
    private static final String XML_FILE = "riwayat_blokir.xml";

    private final ObservableList<RiwayatBlokir> dataList;

    public RiwayatBlokirList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML();
    }

    // ===================================================================
    // METODE INTI (Tidak ada perubahan)
    // ===================================================================

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

    public void remove(int nomor) {
        // Pastikan nomor valid (lebih besar dari 0 dan tidak melebihi ukuran list)
        if (nomor > 0 && nomor <= dataList.size()) {
            // Hapus item dari list. Ingat, list index dimulai dari 0, jadi gunakan nomor - 1.
            dataList.remove(nomor - 1);
            
            // Perbarui nomor urut untuk semua item yang tersisa
            updateNomor();
            
            // Simpan perubahan ke file XML
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
    
    private void updateNomor() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setNomor(i + 1);
        }
    }

   private XStream createXStream() {
    XStream.setupDefaultSecurity(null);
    XStream xstream = new XStream(new StaxDriver());

    // Konfigurasi keamanan
    xstream.addPermission(NoTypePermission.NONE);
    xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
    xstream.allowTypeHierarchy(String.class);

    // Izinkan kelas dari paket proyek Anda
    xstream.allowTypesByWildcard(new String[] { "purify.**" });
    
    // Izinkan kelas koleksi yang digunakan
    xstream.allowTypeHierarchy(java.util.List.class);
    xstream.allowTypeHierarchy(ArrayList.class);

    // Konfigurasi alias
    xstream.alias("RiwayatBlokir", RiwayatBlokir.class);
    xstream.alias("list", java.util.List.class); 

    return xstream;
}

    /**
     * Menyimpan state dari list ke file XML menggunakan konfigurasi XStream yang aman.
     */
    public void saveToXML() {
    XStream xstream = createXStream();
    try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
        // Konversi ObservableList ke ArrayList biasa sebelum menyimpan
        ArrayList<RiwayatBlokir> plainList = new ArrayList<>(this.dataList);
        xstream.toXML(plainList, fos); // Simpan plainList, bukan "this"
        logger.log(Level.INFO, "Data riwayat berhasil disimpan ke " + XML_FILE);
    } catch (IOException e) {
        logger.log(Level.SEVERE, "Gagal menyimpan data riwayat ke XML", e);
    }
}

    /**
     * Memuat state dari list dari file XML menggunakan konfigurasi XStream yang aman.
     */
    @SuppressWarnings("unchecked")
private void loadFromXML() {
    File file = new File(XML_FILE);
    if (!file.exists()) {
        logger.log(Level.INFO, "File " + XML_FILE + " tidak ditemukan.");
        return;
    }

    XStream xstream = createXStream();
    
    try (FileInputStream fis = new FileInputStream(file)) {
        // Baca data sebagai ArrayList
        ArrayList<RiwayatBlokir> loadedList = (ArrayList<RiwayatBlokir>) xstream.fromXML(fis);
        if (loadedList != null) {
            // Set data ke ObservableList
            dataList.setAll(loadedList);
            logger.log(Level.INFO, "Data riwayat berhasil dimuat dari " + XML_FILE);
        }
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Error saat parsing XML. File lama akan di-backup.", e);
        file.renameTo(new File(XML_FILE + ".corrupted_" + System.currentTimeMillis()));
    }
}
}