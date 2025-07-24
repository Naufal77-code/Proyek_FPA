package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Kelas singleton yang mengelola daftar pengguna, termasuk proses login,
 * register,
 * perubahan data akun, dan penyimpanan ke file XML menggunakan XStream.
 */
public class ManajemenPengguna {

    // ===== ATRIBUT =====

    // Lokasi file XML yang digunakan untuk menyimpan data pengguna secara permanen
    private static final String XML_FILE = "pengguna.xml";

    // Daftar pengguna yang telah terdaftar (dimuat dari file XML)
    private final List<Pengguna> daftarPengguna;

    // Instance tunggal (singleton pattern) dari kelas ini
    private static ManajemenPengguna instance;

    // Menyimpan siapa pengguna yang sedang login saat ini
    private Pengguna currentUser;

    // ===== KONSTRUKTOR PRIVATE =====

    /**
     * Konstruktor private agar hanya bisa diakses melalui getInstance (singleton).
     * Meload data dari file XML ke dalam daftar pengguna.
     */
    private ManajemenPengguna() {
        daftarPengguna = new ArrayList<>();
        loadFromXML();
    }

    // ===== METHOD SINGLETON =====

    /**
     * Mengembalikan instance tunggal dari kelas ini.
     * Membuat instance baru jika belum ada.
     *
     * @return instance ManajemenPengguna
     */
    public static ManajemenPengguna getInstance() {
        if (instance == null) {
            instance = new ManajemenPengguna();
        }
        return instance;
    }

    // ===== GETTER =====

    /**
     * Mengembalikan pengguna yang sedang login saat ini.
     *
     * @return Pengguna aktif
     */
    public Pengguna getCurrentUser() {
        return currentUser;
    }

    // ===== LOGIN =====

    /**
     * Melakukan login dengan nama dan password yang diberikan.
     * Jika cocok, set sebagai pengguna aktif.
     *
     * @param nama     Nama pengguna
     * @param password Password pengguna
     * @return true jika login berhasil, false jika gagal
     */
    public boolean login(String nama, String password) {
        Optional<Pengguna> userOpt = findPenggunaByNama(nama);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            this.currentUser = userOpt.get();
            return true;
        }
        return false;
    }

    // ===== CARI PENGGUNA =====

    /**
     * Mencari pengguna berdasarkan nama.
     *
     * @param nama Nama pengguna yang dicari
     * @return Optional berisi pengguna jika ditemukan
     */
    private Optional<Pengguna> findPenggunaByNama(String nama) {
        return daftarPengguna.stream()
                .filter(pengguna -> pengguna.getNama().equalsIgnoreCase(nama))
                .findFirst();
    }

    // ===== UBAH USERNAME =====

    /**
     * Mengubah username pengguna aktif.
     * Tidak boleh sama dengan nama pengguna lain yang sudah terdaftar.
     *
     * @param usernameBaru Nama pengguna baru
     * @return true jika berhasil, false jika gagal (duplikat atau belum login)
     */
    public boolean ubahUsername(String usernameBaru) {
        if (findPenggunaByNama(usernameBaru).isPresent()) {
            return false; // Nama sudah digunakan
        }
        if (currentUser != null) {
            currentUser.setNama(usernameBaru);
            saveToXML();
            return true;
        }
        return false;
    }

    // ===== UBAH PASSWORD =====

    /**
     * Mengubah password pengguna aktif jika password lama sesuai.
     *
     * @param sandiLama Password lama
     * @param sandiBaru Password baru
     * @return true jika berhasil, false jika gagal
     */
    public boolean ubahSandi(String sandiLama, String sandiBaru) {
        if (currentUser != null && currentUser.getPassword().equals(sandiLama)) {
            currentUser.setPassword(sandiBaru);
            saveToXML();
            return true;
        }
        return false;
    }

    // ===== KONFIGURASI XSTREAM =====

    /**
     * Membuat objek XStream yang dikonfigurasi dengan izin keamanan yang sesuai.
     *
     * @return objek XStream yang aman
     */
    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypesByWildcard(new String[] { "purify.**", "java.util.**" });
        xstream.alias("list", List.class);
        xstream.alias("pengguna", Pengguna.class);
        return xstream;
    }

    // ===== SIMPAN KE XML =====

    /**
     * Menyimpan daftar pengguna ke file XML.
     * Digunakan saat register, ubah username/password.
     */
    private void saveToXML() {
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            createXStream().toXML(new ArrayList<>(daftarPengguna), fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===== MUAT DARI XML =====

    /**
     * Memuat data pengguna dari file XML ke dalam daftar pengguna.
     * Dipanggil sekali saat objek dibuat.
     */
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists())
            return;
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

    // ===== REGISTER =====

    /**
     * Menambahkan pengguna baru ke daftar jika nama belum digunakan.
     *
     * @param nama     Nama pengguna
     * @param password Password pengguna
     * @return true jika berhasil ditambahkan, false jika nama sudah dipakai
     */
    public boolean register(String nama, String password) {
        if (findPenggunaByNama(nama).isPresent()) {
            return false;
        }
        daftarPengguna.add(new Pengguna(nama, password));
        saveToXML();
        return true;
    }

    // ===== GET PASSWORD (POTENSIAL MASALAH KEAMANAN) =====

    /**
     * Mengambil password dari pengguna berdasarkan nama.
     * ⚠️ PERINGATAN: Ini berisiko keamanan jika tidak digunakan dengan benar.
     *
     * @param nama Nama pengguna
     * @return Password jika ditemukan, null jika tidak
     */
    public String getPasswordForUser(String nama) {
        Optional<Pengguna> userOpt = findPenggunaByNama(nama);
        return userOpt.map(Pengguna::getPassword).orElse(null);
    }
}
