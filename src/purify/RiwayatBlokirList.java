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

    // Instance statis untuk menerapkan pola Singleton
    private static RiwayatBlokirList instance;

    // Nama file XML yang digunakan untuk menyimpan dan memuat data riwayat
    private static final String XML_FILE = "riwayat_blokir.xml";

    // Daftar data riwayat yang ditampilkan dan dikelola dalam aplikasi
    private final ObservableList<RiwayatBlokir> dataList;

    /**
     * Konstruktor default yang menginisialisasi daftar dan memuat data dari XML.
     */
    public RiwayatBlokirList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML(); // langsung muat data jika file XML tersedia
    }

    /**
     * Mengembalikan instance tunggal dari RiwayatBlokirList.
     * Jika belum ada, akan dibuat instance baru (Singleton Pattern).
     */
    public static RiwayatBlokirList getInstance() {
        if (instance == null) {
            instance = new RiwayatBlokirList();
        }
        return instance;
    }

    /**
     * Getter untuk mendapatkan seluruh data riwayat dalam bentuk ObservableList.
     */
    public ObservableList<RiwayatBlokir> getData() {
        return this.dataList;
    }

    /**
     * Menambahkan data baru ke daftar dan menyimpannya ke file XML.
     * 
     * @param nomor        Nomor urutan riwayat
     * @param tanggalMulai Tanggal mulai sesi blokir
     * @param durasi       Durasi blokir
     * @param status       Status blokir (selesai/aktif/dll.)
     * @param aktivitas    Aktivitas yang dicatat
     */
    public void setData(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        RiwayatBlokir newRiwayat = new RiwayatBlokir(nomor, tanggalMulai, durasi, status, aktivitas);
        dataList.add(newRiwayat);
        saveToXML();
    }

    /**
     * Menghapus data riwayat berdasarkan nomor urut (1-based index).
     * Jika nomor tidak valid, akan dilemparkan exception.
     */
    public void remove(int nomor) {
        if (nomor > 0 && nomor <= dataList.size()) {
            dataList.remove(nomor - 1);
            updateNomor(); // setelah penghapusan, nomor-nomor diperbarui
            saveToXML();
        } else {
            throw new IndexOutOfBoundsException("Nomor riwayat tidak valid untuk dihapus.");
        }
    }

    /**
     * Memperbarui penomoran seluruh data dalam daftar berdasarkan urutan terbaru.
     */
    private void updateNomor() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setNomor(i + 1);
        }
    }

    /**
     * Mengedit aktivitas dari data riwayat tertentu berdasarkan nomor.
     * 
     * @param nomor         Nomor urutan (1-based index)
     * @param aktivitasBaru Teks aktivitas yang baru
     */
    public void editAktivitas(int nomor, String aktivitasBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokir riwayat = dataList.get(nomor - 1);
            riwayat.setAktivitas(aktivitasBaru.trim());
            saveToXML();
        }
    }

    /**
     * Membuat dan mengatur konfigurasi keamanan untuk objek XStream.
     * Digunakan untuk serialisasi/deserialisasi data XML.
     */
    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(String.class);
        xstream.allowTypesByWildcard(new String[] { "purify.**", "java.util.**" });
        xstream.alias("list", java.util.List.class);
        xstream.alias("RiwayatBlokir", RiwayatBlokir.class);
        return xstream;
    }

    /**
     * Menyimpan seluruh data riwayat ke file XML.
     */
    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            xstream.toXML(new ArrayList<>(this.dataList), fos);
        } catch (IOException e) {
            Logger.getLogger(RiwayatBlokirList.class.getName()).log(Level.SEVERE, "Gagal menyimpan data riwayat.", e);
        }
    }

    /**
     * Memuat data riwayat dari file XML dan menyalinnya ke dataList jika tersedia.
     */
    @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists())
            return;

        XStream xstream = createXStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<RiwayatBlokir> loadedList = (ArrayList<RiwayatBlokir>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList);
            }
        } catch (Exception e) {
            Logger.getLogger(RiwayatBlokirList.class.getName()).log(Level.SEVERE, "Gagal memuat data riwayat.", e);
        }
    }
}
