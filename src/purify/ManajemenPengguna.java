package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManajemenPengguna {
    private static final String XML_FILE = "pengguna.xml";
    private final List<Pengguna> daftarPengguna;
    private static ManajemenPengguna instance;

    private ManajemenPengguna() {
        daftarPengguna = new ArrayList<>();
        loadFromXML();
    }

    public static ManajemenPengguna getInstance() {
        if (instance == null) {
            instance = new ManajemenPengguna();
        }
        return instance;
    }

    public boolean register(String nama, String password) {
        if (findPenggunaByNama(nama).isPresent()) {
            return false; // Pengguna sudah ada
        }
        daftarPengguna.add(new Pengguna(nama, password));
        saveToXML();
        return true;
    }

    public boolean login(String nama, String password) {
        return findPenggunaByNama(nama)
                .map(pengguna -> pengguna.getPassword().equals(password))
                .orElse(false);
    }

    private Optional<Pengguna> findPenggunaByNama(String nama) {
        return daftarPengguna.stream()
                .filter(pengguna -> pengguna.getNama().equalsIgnoreCase(nama))
                .findFirst();
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypesByWildcard(new String[]{"purify.**", "java.util.**"});
        xstream.alias("list", List.class);
        xstream.alias("pengguna", Pengguna.class);
        return xstream;
    }

    private void saveToXML() {
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            createXStream().toXML(daftarPengguna, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists()) return;
        try (FileInputStream fis = new FileInputStream(file)) {
            List<Pengguna> loadedList = (List<Pengguna>) createXStream().fromXML(fis);
            if (loadedList != null) {
                daftarPengguna.addAll(loadedList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}