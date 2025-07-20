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
    private static final String XML_FILE = "accounts.xml";
    private final List<Pengguna> daftarPengguna;
    private static ManajemenPengguna instance;
    
    // --- VARIABEL BARU UNTUK MENYIMPAN SESI PENGGUNA ---
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

    // --- METODE BARU UNTUK MENDAPATKAN PENGGUNA SAAT INI ---
    public Pengguna getCurrentUser() {
        return currentUser;
    }

    public boolean login(String nama, String password) {
        Optional<Pengguna> userOpt = findPenggunaByNama(nama);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            // Jika login berhasil, simpan pengguna sebagai currentUser
            this.currentUser = userOpt.get();
            return true;
        }
        return false;
    }

    private Optional<Pengguna> findPenggunaByNama(String nama) {
        return daftarPengguna.stream()
                .filter(pengguna -> pengguna.getUsername().equalsIgnoreCase(nama))
                .findFirst();
    }

    // --- METODE UBAH USERNAME (DIPERBAIKI) ---
    public boolean ubahUsername(String usernameBaru) {
        // Cek apakah username baru sudah digunakan oleh pengguna lain
        if (findPenggunaByNama(usernameBaru).isPresent()) {
            return false;
        }
        // Langsung ubah username dari currentUser
        if (currentUser != null) {
            currentUser.setUsername(usernameBaru);
            saveToXML();
            return true;
        }
        return false;
    }

    // --- METODE UBAH SANDI (DIPERBAIKI) ---
    public boolean ubahSandi(String sandiLama, String sandiBaru) {
        // Pastikan sandi lama yang dimasukkan benar
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
        xstream.alias("account", Pengguna.class);
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
    
    // Metode register dan lainnya tidak perlu diubah
    public boolean register(String nama, String password) {
        if (findPenggunaByNama(nama).isPresent()) {
            return false;
        }
        daftarPengguna.add(new Pengguna(nama, password));
        saveToXML();
        return true;
    }
}