package purify;

// Import pustaka XStream untuk menyimpan dan memuat data ke/dari file XML
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;

// Import pustaka JavaFX untuk list yang bisa diamati oleh UI
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

// Import pustaka I/O dan logging
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppointmentList {

    // Logger digunakan untuk mencatat aktivitas dan error saat memproses file XML
    private static final Logger logger = Logger.getLogger(AppointmentList.class.getName());

    // Nama file XML tempat data appointment akan disimpan
    private static final String XML_FILE = "appointment_history.xml";

    // List observabel yang dapat digunakan untuk binding data ke UI (JavaFX)
    private final ObservableList<Appointment> dataList;

    // Konstruktor: inisialisasi list kosong dan memuat data dari file XML (jika
    // ada)
    public AppointmentList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML(); // Load data dari file XML saat objek dibuat
    }

    // Mengembalikan seluruh data appointment yang tersimpan
    public ObservableList<Appointment> getData() {
        return this.dataList;
    }

    // Menambahkan satu appointment ke daftar dan langsung menyimpannya ke XML
    public void addAppointment(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment tidak boleh null");
        }
        dataList.add(appointment); // Tambahkan ke list
        saveToXML(); // Simpan ke file XML
    }

    // Mengonfigurasi dan mengamankan instance XStream agar hanya bisa memproses
    // tipe tertentu
    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream); // Penting: aktifkan sistem keamanan XStream

        // Izinkan hanya tipe-tipe yang aman untuk diserialisasi
        xstream.addPermission(NoTypePermission.NONE); // Awalnya tidak izinkan tipe apapun
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES); // Izinkan tipe primitif (int, boolean, dll)
        xstream.allowTypeHierarchy(String.class); // Izinkan String dan turunannya
        xstream.allowTypesByWildcard(new String[] { "purify.**" }); // Izinkan semua class di package `purify`
        xstream.allowTypeHierarchy(java.util.List.class); // Izinkan struktur list
        xstream.allowTypeHierarchy(ArrayList.class); // Izinkan tipe ArrayList

        // Alias nama tag XML agar mudah dibaca manusia
        xstream.alias("Appointment", Appointment.class);
        xstream.alias("list", java.util.List.class);

        return xstream;
    }

    // Menyimpan seluruh data appointment ke file XML
    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            // Konversi ObservableList ke ArrayList biasa agar bisa disimpan
            ArrayList<Appointment> plainList = new ArrayList<>(this.dataList);
            xstream.toXML(plainList, fos); // Serialisasi data ke dalam file XML
            logger.log(Level.INFO, "Riwayat jadwal berhasil disimpan ke " + XML_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan riwayat jadwal ke XML", e);
        }
    }

    // Memuat data appointment dari file XML dan menampilkannya ke dalam dataList
    @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists()) {
            logger.log(Level.INFO, "File " + XML_FILE + " tidak ditemukan.");
            return; // Tidak ada file yang bisa dimuat, maka proses dihentikan
        }

        XStream xstream = createXStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            // Deserialize isi file XML menjadi ArrayList<Appointment>
            ArrayList<Appointment> loadedList = (ArrayList<Appointment>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList); // Tampilkan ke dalam ObservableList agar bisa ditampilkan di UI
                logger.log(Level.INFO, "Riwayat jadwal berhasil dimuat dari " + XML_FILE);
            }
        } catch (Exception e) {
            // Jika terjadi kesalahan parsing, file akan di-rename sebagai backup
            logger.log(Level.SEVERE, "Error saat parsing XML riwayat jadwal. File lama akan di-backup.", e);
            file.renameTo(new File(XML_FILE + ".corrupted_" + System.currentTimeMillis()));
        }
    }
}
