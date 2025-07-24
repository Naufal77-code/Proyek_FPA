package purify;

// Import library XStream untuk serialisasi/deserialisasi objek ke/dari XML
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

// Import untuk list observable milik JavaFX
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatHistoryList {

    // Logger untuk mencatat informasi dan error selama operasi penyimpanan/muat
    // data
    private static final Logger logger = Logger.getLogger(ChatHistoryList.class.getName());

    // Nama file XML tempat menyimpan riwayat chat
    private static final String XML_FILE = "chat_history.xml";

    // List observabel untuk menyimpan dan menampilkan data ke UI JavaFX
    private final ObservableList<ChatRecord> dataList;

    // Konstruktor utama: inisialisasi data list dan langsung memuat dari file XML
    // (jika ada)
    public ChatHistoryList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML();
    }

    // Getter untuk mengakses daftar riwayat chat
    public ObservableList<ChatRecord> getData() {
        return this.dataList;
    }

    // Method untuk menambahkan riwayat chat baru ke daftar dan menyimpannya ke file
    // XML
    public void addChatRecord(ChatRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("ChatRecord tidak boleh null");
        }
        dataList.add(record);
        saveToXML(); // Simpan otomatis setiap kali ada data baru
    }

    // Method untuk mengatur dan mengamankan konfigurasi XStream
    private XStream createXStream() {
        // Nonaktifkan semua izin awal (keamanan)
        XStream.setupDefaultSecurity(null);
        XStream xstream = new XStream(new StaxDriver());

        // Aktifkan hanya tipe data yang dibutuhkan
        xstream.addPermission(NoTypePermission.NONE); // Tidak izinkan apapun secara default
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES); // Izinkan tipe primitif (int, boolean, dsb)
        xstream.allowTypeHierarchy(String.class); // Izinkan String
        xstream.allowTypesByWildcard(new String[] { "purify.**" }); // Izinkan semua class dari package `purify`
        xstream.allowTypeHierarchy(java.util.List.class); // Izinkan interface List
        xstream.allowTypeHierarchy(ArrayList.class); // Izinkan ArrayList

        // Tentukan alias XML agar lebih mudah dibaca
        xstream.alias("ChatRecord", ChatRecord.class); // <ChatRecord>...</ChatRecord>
        xstream.alias("list", java.util.List.class); // <list>...</list>

        return xstream;
    }

    // Method untuk menyimpan daftar chat ke dalam file XML
    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            // Convert ObservableList ke ArrayList biasa sebelum diserialisasi
            ArrayList<ChatRecord> plainList = new ArrayList<>(this.dataList);
            xstream.toXML(plainList, fos); // Tulis ke file XML
            logger.log(Level.INFO, "Riwayat chat berhasil disimpan ke " + XML_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan riwayat chat ke XML", e);
        }
    }

    // Method untuk memuat isi file XML ke dalam dataList
    @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);

        // Jika file belum ada, maka tidak perlu dimuat
        if (!file.exists()) {
            logger.log(Level.INFO, "File " + XML_FILE + " tidak ditemukan.");
            return;
        }

        XStream xstream = createXStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            // Deserialize isi file ke dalam ArrayList<ChatRecord>
            ArrayList<ChatRecord> loadedList = (ArrayList<ChatRecord>) xstream.fromXML(fis);
            if (loadedList != null) {
                // Masukkan isi ke dalam ObservableList agar langsung terlihat di UI
                dataList.setAll(loadedList);
                logger.log(Level.INFO, "Riwayat chat berhasil dimuat dari " + XML_FILE);
            }
        } catch (Exception e) {
            // Jika gagal, backup file lama agar tidak hilang data
            logger.log(Level.SEVERE, "Error saat parsing XML riwayat chat. File lama akan di-backup.", e);
            file.renameTo(new File(XML_FILE + ".corrupted_" + System.currentTimeMillis()));
        }
    }
}
