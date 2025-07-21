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
    private static final String XML_FILE = "komunitas.xml";
    // [MODIFIKASI] Nama variabel diubah untuk merepresentasikan semua unggahan
    private final ObservableList<Post> allPosts; 
    private static KomunitasDataList instance;

    private KomunitasDataList() {
        allPosts = FXCollections.observableArrayList();
        loadFromXML();
    }

    public static KomunitasDataList getInstance() {
        if (instance == null) {
            instance = new KomunitasDataList();
        }
        return instance;
    }

    // [MODIFIKASI] Metode ini diganti dengan metode baru yang lebih spesifik
    public ObservableList<Post> getPostsForUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            return FXCollections.observableArrayList(); // Return list kosong jika tidak ada user
        }
        // Filter daftar semua unggahan untuk mendapatkan yang sesuai dengan username
        List<Post> userPosts = allPosts.stream()
                .filter(post -> username.equalsIgnoreCase(post.getPenulis()))
                .collect(Collectors.toList());
        return FXCollections.observableArrayList(userPosts);
    }

    public void tambahPost(String isi, String penulis) {
        int nextId = allPosts.size() + 1;
        Post newPost = new Post(nextId, isi, penulis);
        allPosts.add(0, newPost); // Tambahkan ke daftar utama
        updateIds();
        saveToXML();
    }

    private void updateIds() {
        for (int i = 0; i < allPosts.size(); i++) {
            allPosts.get(i).setIdPost(i + 1);
        }
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(java.time.LocalDateTime.class);
        xstream.allowTypesByWildcard(new String[]{"purify.**", "java.util.**"});
        xstream.alias("list", java.util.List.class);
        xstream.alias("post", Post.class);
        xstream.alias("komentar", Komentar.class);
        return xstream;
    }

    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            // Simpan daftar utama yang berisi semua unggahan
            xstream.toXML(new ArrayList<>(this.allPosts), fos);
        } catch (IOException e) {
            Logger.getLogger(KomunitasDataList.class.getName()).log(Level.SEVERE, "Gagal menyimpan data komunitas.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists()) return;

        XStream xstream = createXStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<Post> loadedList = (ArrayList<Post>) xstream.fromXML(fis);
            if (loadedList != null) {
                // Muat semua unggahan ke daftar utama
                allPosts.setAll(loadedList);
            }
        } catch (Exception e) {
            Logger.getLogger(KomunitasDataList.class.getName()).log(Level.SEVERE, "Gagal memuat data komunitas.", e);
        }
    }
}