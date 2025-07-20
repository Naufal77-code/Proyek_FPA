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
    // --- PERUBAHAN 1: Nama file dikembalikan ke "pengguna.xml" ---
    private static final String XML_FILE = "pengguna.xml";
    private final List<Pengguna> daftarPengguna;
    private static ManajemenPengguna instance;
    private Pengguna currentUser;

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

    public Pengguna getCurrentUser() {
        return currentUser;
    }

    public boolean login(String nama, String password) {
        Optional<Pengguna> userOpt = findPenggunaByNama(nama);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            this.currentUser = userOpt.get();
            return true;
        }
        return false;
    }

    private Optional<Pengguna> findPenggunaByNama(String nama) {
        return daftarPengguna.stream()
                // --- PERUBAHAN 3: Memanggil getNama() ---
                .filter(pengguna -> pengguna.getNama().equalsIgnoreCase(nama))
                .findFirst();
    }
    
    // --- Metode untuk Mengubah Data ---
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

    // --- Logika Baca/Tulis XML ---
    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypesByWildcard(new String[]{"purify.**", "java.util.**"});
        xstream.alias("list", List.class);
        // --- PERUBAHAN 2: Alias dikembalikan ke "pengguna" ---
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
}