package purify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

public class SessionManager {

    // Nama file properti yang digunakan untuk menyimpan data sesi login.
    private static final String SESSION_FILE = "session.properties";

    // Kunci (key) untuk menyimpan username pengguna dalam file properti.
    private static final String USER_KEY = "currentUser";

    // Kunci (key) untuk menyimpan tanggal login dalam file properti.
    private static final String DATE_KEY = "loginDate";

    /**
     * Menyimpan sesi login pengguna ke dalam file session.properties.
     * Akan menyimpan username dan tanggal login hari ini.
     *
     * @param username Nama pengguna yang sedang login.
     */
    public static void saveSession(String username) {
        // Membuat objek Properties baru untuk menampung data sesi.
        Properties props = new Properties();
        // Menetapkan username pengguna ke dalam objek Properties dengan kunci USER_KEY.
        props.setProperty(USER_KEY, username);
        // Menetapkan tanggal hari ini sebagai tanggal login dengan kunci DATE_KEY.
        props.setProperty(DATE_KEY, LocalDate.now().toString());

        try (FileOutputStream fos = new FileOutputStream(SESSION_FILE)) {
            // Menyimpan objek Properties ke dalam file sesi.
            props.store(fos, "User Session");
        } catch (IOException e) {
            // Menangani (handle) error jika terjadi masalah saat menulis file.
            e.printStackTrace();
        }
    }

    /**
     * Memuat sesi login dari file jika valid untuk hari ini.
     * Jika ditemukan, sistem mencoba login otomatis menggunakan username tersebut.
     *
     * @return Objek Pengguna jika sesi valid dan login berhasil, atau null jika
     *         gagal.
     */
    public static Pengguna getValidSession() {
        // Membuat objek File untuk merepresentasikan file sesi.
        File sessionFile = new File(SESSION_FILE);
        // Memeriksa apakah file sesi ada.
        if (!sessionFile.exists()) {
            return null; // File sesi tidak ditemukan, kembalikan null.
        }

        // Membuat objek Properties baru untuk memuat data dari file.
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(sessionFile)) {
            // Memuat data dari file sesi ke dalam objek Properties.
            props.load(fis);
            // Mengambil username dari Properties.
            String username = props.getProperty(USER_KEY);
            // Mengambil tanggal login dari Properties.
            String dateStr = props.getProperty(DATE_KEY);

            // Memeriksa apakah data sesi lengkap (username dan tanggal tidak null).
            if (username == null || dateStr == null) {
                return null; // Data sesi tidak lengkap, kembalikan null.
            }

            // Mengonversi string tanggal menjadi objek LocalDate.
            LocalDate loginDate = LocalDate.parse(dateStr);

            // Validasi apakah tanggal login sama dengan tanggal hari ini.
            if (loginDate.equals(LocalDate.now())) {
                // Mendapatkan instance dari ManajemenPengguna (Singleton).
                ManajemenPengguna mu = ManajemenPengguna.getInstance();
                // Mencoba login otomatis menggunakan username dan password yang disimpan.
                if (mu.login(username, mu.getPasswordForUser(username))) {
                    // Jika login berhasil, kembalikan objek Pengguna yang sedang aktif.
                    return mu.getCurrentUser();
                }
            }

        } catch (IOException e) {
            // Menangani error jika terjadi masalah saat membaca file.
            e.printStackTrace();
        }

        // Jika sesi tidak valid karena alasan apa pun (tanggal tidak cocok, login
        // gagal, dll.), hapus file sesi.
        clearSession();
        return null;
    }

    /**
     * Menghapus file sesi login yang tersimpan.
     * Biasanya dipanggil jika sesi tidak valid atau saat logout.
     */
    public static void clearSession() {
        // Membuat objek File untuk merepresentasikan file sesi.
        File sessionFile = new File(SESSION_FILE);
        // Memeriksa apakah file sesi ada.
        if (sessionFile.exists()) {
            // Menghapus file sesi jika ada.
            sessionFile.delete();
        }
    }
}