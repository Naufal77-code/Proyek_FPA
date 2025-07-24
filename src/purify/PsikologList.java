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

/**
 * Kelas PsikologList digunakan untuk menyimpan dan mengelola data psikolog
 * dengan dukungan penyimpanan ke XML menggunakan XStream.
 */
public class PsikologList {

    // Logger untuk mencatat event atau error ke console/file log
    private static final Logger logger = Logger.getLogger(PsikologList.class.getName());

    // Nama file XML untuk menyimpan data
    private static final String XML_FILE = "data_psikolog.xml";

    // List observable agar bisa di-bind langsung ke komponen JavaFX seperti
    // TableView
    private final ObservableList<Psikolog> dataList;

    /**
     * Konstruktor utama yang membuat list kosong dan mencoba load data dari file
     * XML.
     */
    public PsikologList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML(); // Muat data dari file saat inisialisasi
    }

    /**
     * Mengembalikan list observable dari objek Psikolog.
     */
    public ObservableList<Psikolog> getData() {
        return this.dataList;
    }

    /**
     * Menambahkan objek Psikolog ke dalam list, lalu menyimpannya ke XML.
     * 
     * @param psikolog Objek Psikolog yang ingin ditambahkan.
     */
    public void addPsikolog(Psikolog psikolog) {
        if (psikolog == null) {
            throw new IllegalArgumentException("Objek Psikolog tidak boleh null");
        }
        dataList.add(psikolog);
        saveToXML();
    }

    /**
     * Mengisi list dengan data default hanya jika list kosong.
     * Berguna saat pertama kali aplikasi dijalankan.
     */
    public void initializeDefaultPsikolog() {
        if (dataList.isEmpty()) {
            addPsikolog(new Psikolog("Dr. Ana Wijaya", "Psikolog Klinis", "Tersedia"));
            addPsikolog(new Psikolog("Budi Santoso, M.Psi", "Psikolog Pendidikan", "Sibuk"));
            addPsikolog(new Psikolog("Citra Dewi, S.Psi", "Psikolog Remaja", "Tersedia"));
            addPsikolog(new Psikolog("Doni Pratama, M.Psi", "Psikolog Keluarga", "Offline"));
            addPsikolog(new Psikolog("Ella Putri, S.Psi", "Konselor Karir", "Tersedia"));
            saveToXML();
        }
    }

    /**
     * Mengedit data psikolog berdasarkan nama lama yang dicocokkan
     * (case-insensitive).
     * Jika ditemukan, datanya akan diperbarui dan disimpan kembali ke XML.
     */
    public void editPsikolog(String namaLama, String namaBaru, String spesialisasiBaru, String statusBaru) {
        for (Psikolog p : dataList) {
            if (p.getNama().equalsIgnoreCase(namaLama)) {
                p.setNama(namaBaru);
                p.setSpesialisasi(spesialisasiBaru);
                p.setStatus(statusBaru);
                saveToXML();
                return;
            }
        }
        // Jika tidak ditemukan, tampilkan peringatan di log
        logger.log(Level.WARNING, "Psikolog dengan nama " + namaLama + " tidak ditemukan.");
    }

    /**
     * Menghapus psikolog berdasarkan nama (case-insensitive), lalu menyimpan
     * kembali ke XML.
     */
    public void removePsikolog(String nama) {
        dataList.removeIf(p -> p.getNama().equalsIgnoreCase(nama));
        saveToXML();
    }

    /**
     * Membuat dan mengonfigurasi instance XStream dengan pengaturan keamanan
     * modern.
     * Hanya tipe-tipe tertentu yang diizinkan untuk deserialisasi.
     */
    private XStream createXStream() {
        // Matikan semua izin default
        XStream.setupDefaultSecurity(null);
        XStream xstream = new XStream(new StaxDriver());

        // Izinkan tipe primitif (int, boolean, dll.)
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);

        // Izinkan String dan semua class dalam package "purify"
        xstream.allowTypeHierarchy(String.class);
        xstream.allowTypesByWildcard(new String[] { "purify.**" });

        // Izinkan List dan ArrayList
        xstream.allowTypeHierarchy(java.util.List.class);
        xstream.allowTypeHierarchy(ArrayList.class);

        // Set alias untuk XML
        xstream.alias("Psikolog", Psikolog.class);
        xstream.alias("list", java.util.List.class);

        return xstream;
    }

    /**
     * Menyimpan data list ke file XML.
     */
    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            ArrayList<Psikolog> plainList = new ArrayList<>(this.dataList); // konversi ke list biasa
            xstream.toXML(plainList, fos); // tulis ke file XML
            logger.log(Level.INFO, "Data psikolog berhasil disimpan ke " + XML_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan data psikolog ke XML", e);
        }
    }

    /**
     * Memuat data dari file XML jika file tersebut ada.
     * Jika gagal parsing, akan di-backup sebagai file ".corrupted".
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
            ArrayList<Psikolog> loadedList = (ArrayList<Psikolog>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList); // isi ulang observable list
                logger.log(Level.INFO, "Data psikolog berhasil dimuat dari " + XML_FILE);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saat parsing XML data psikolog. File lama akan di-backup.", e);
            file.renameTo(new File(XML_FILE + ".corrupted_" + System.currentTimeMillis()));
        }
    }
}
