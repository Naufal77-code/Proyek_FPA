package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class KomunitasDataList {
    // Nama file XML untuk menyimpan data post komunitas
    private static final String XML_FILE = "komunitas.xml";

    // List utama yang menyimpan seluruh post dalam bentuk ObservableList agar dapat
    // langsung digunakan di JavaFX TableView/ListView
    private final ObservableList<Post> allPosts;

    // Singleton instance agar data komunitas hanya ada satu di seluruh aplikasi
    private static KomunitasDataList instance;

    // Konstruktor privat untuk mencegah instansiasi langsung dan memuat data dari
    // XML
    private KomunitasDataList() {
        allPosts = FXCollections.observableArrayList();
        loadFromXML(); // Memuat data dari file XML saat inisialisasi
    }

    // Method untuk mendapatkan instance tunggal dari kelas ini
    public static KomunitasDataList getInstance() {
        if (instance == null) {
            instance = new KomunitasDataList();
        }
        return instance;
    }

    /**
     * Mengembalikan daftar post milik pengguna tertentu berdasarkan username.
     * Jika username null atau kosong, mengembalikan daftar kosong.
     */
    public ObservableList<Post> getPostsForUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return FXCollections.observableArrayList(); // Return list kosong jika tidak ada user
        }
        // Filter post berdasarkan penulis
        List<Post> userPosts = allPosts.stream()
                .filter(post -> username.equalsIgnoreCase(post.getPenulis()))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(userPosts);
    }

    /**
     * Menambahkan post baru ke daftar utama berdasarkan isi dan penulis.
     * Post baru akan ditambahkan di urutan teratas (index 0).
     */
    public void tambahPost(String isi, String penulis) {
        int nextId = allPosts.size() + 1; // ID post ditentukan berdasarkan jumlah post saat ini
        Post newPost = new Post(nextId, isi, penulis);
        allPosts.add(0, newPost); // Tambahkan post di awal daftar
        updateIds(); // Perbarui semua ID post
        saveToXML(); // Simpan ke file XML
    }

    /**
     * Menyesuaikan ulang ID post berdasarkan urutan di daftar.
     * Penting agar ID tetap konsisten setelah penambahan/urutan berubah.
     */
    private void updateIds() {
        for (int i = 0; i < allPosts.size(); i++) {
            allPosts.get(i).setIdPost(i + 1);
        }
    }

    /**
     * Membuat objek XStream dengan pengaturan keamanan dan alias yang sesuai.
     * Digunakan untuk serialisasi/deserialisasi XML.
     */
    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(NoTypePermission.NONE); // Tidak izinkan semua type secara default
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES); // Izinkan primitive types
        xstream.allowTypeHierarchy(java.time.LocalDateTime.class); // Izinkan LocalDateTime
        xstream.allowTypesByWildcard(new String[] { "purify.**", "java.util.**" }); // Izinkan semua dari package
                                                                                    // tertentu
        xstream.alias("list", java.util.List.class); // Alias XML
        xstream.alias("post", Post.class);
        xstream.alias("komentar", Komentar.class);
        return xstream;
    }

    /**
     * Menyimpan seluruh post komunitas ke file XML.
     */
    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            xstream.toXML(new ArrayList<>(this.allPosts), fos);
        } catch (IOException e) {
            Logger.getLogger(KomunitasDataList.class.getName()).log(Level.SEVERE, "Gagal menyimpan data komunitas.", e);
        }
    }

    /**
     * Memuat data komunitas dari file XML ke dalam `allPosts`.
     */
    @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists())
            return;

        XStream xstream = createXStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<Post> loadedList = (ArrayList<Post>) xstream.fromXML(fis);
            if (loadedList != null) {
                allPosts.setAll(loadedList); // Ganti isi list dengan data dari file
            }
        } catch (Exception e) {
            Logger.getLogger(KomunitasDataList.class.getName()).log(Level.SEVERE, "Gagal memuat data komunitas.", e);
        }
    }
}
