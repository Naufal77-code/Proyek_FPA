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
import java.util.logging.Level;
import java.util.logging.Logger;

public class KomunitasDataList {
    private static final String XML_FILE = "komunitas.xml";
    private final ObservableList<Post> postList;
    private static KomunitasDataList instance;

    private KomunitasDataList() {
        postList = FXCollections.observableArrayList();
        loadFromXML();
    }

    public static KomunitasDataList getInstance() {
        if (instance == null) {
            instance = new KomunitasDataList();
        }
        return instance;
    }

    public ObservableList<Post> getPostList() {
        return this.postList;
    }

    public void tambahPost(String isi, String penulis) {
        int nextId = postList.size() + 1;
        Post newPost = new Post(nextId, isi, penulis);
        postList.add(0, newPost); // Tambahkan di awal agar muncul paling atas
        updateIds();
        saveToXML();
    }

    private void updateIds() {
        for (int i = 0; i < postList.size(); i++) {
            postList.get(i).setIdPost(i + 1);
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
            xstream.toXML(new ArrayList<>(this.postList), fos);
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
                postList.setAll(loadedList);
            }
        } catch (Exception e) {
            Logger.getLogger(KomunitasDataList.class.getName()).log(Level.SEVERE, "Gagal memuat data komunitas.", e);
        }
    }
}