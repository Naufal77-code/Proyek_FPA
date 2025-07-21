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
    private Pengguna currentUser; // Variabel ini krusial untuk mengetahui siapa yang login

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

    // Metode ini yang akan kita gunakan untuk mendapatkan info user
    public Pengguna getCurrentUser() {
        return currentUser;
    }

    public boolean login(String nama, String password) {
        Optional<Pengguna> userOpt = findPenggunaByNama(nama);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            this.currentUser = userOpt.get(); // Set user saat login berhasil
            return true;
        }
        return false;
    }

    private Optional<Pengguna> findPenggunaByNama(String nama) {
        return daftarPengguna.stream()
                .filter(pengguna -> pengguna.getNama().equalsIgnoreCase(nama))
                .findFirst();
    }
    
    public boolean ubahUsername(String usernameBaru) {
        if (findPenggunaByNama(usernameBaru).isPresent()) {
            return false;
        }
        if (currentUser != null) {
            currentUser.setNama(usernameBaru);
            saveToXML();
            return true;
        }
        return false;
    }

    public boolean ubahSandi(String sandiLama, String sandiBaru) {
        if (currentUser != null && currentUser.getPassword().equals(sandiLama)) {
            currentUser.setPassword(sandiBaru);
            saveToXML();
            return true;
        }
        return false;
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
            createXStream().toXML(new ArrayList<>(daftarPengguna), fos);
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
                daftarPengguna.clear();
                daftarPengguna.addAll(loadedList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean register(String nama, String password) {
        if (findPenggunaByNama(nama).isPresent()) {
            return false;
        }
        daftarPengguna.add(new Pengguna(nama, password));
        saveToXML();
        return true;
    }

    public String getPasswordForUser(String nama) {
    Optional<Pengguna> userOpt = findPenggunaByNama(nama);
    return userOpt.map(Pengguna::getPassword).orElse(null);
}

}