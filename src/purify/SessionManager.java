package purify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Properties;

public class SessionManager {

    // Nama file properti yang digunakan untuk menyimpan data sesi login
    private static final String SESSION_FILE = "session.properties";

    // Kunci untuk menyimpan username pengguna dalam file properti
    private static final String USER_KEY = "currentUser";

    // Kunci untuk menyimpan tanggal login dalam file properti
    private static final String DATE_KEY = "loginDate";

    /**
     * Menyimpan sesi login pengguna ke dalam file session.properties.
     * Akan menyimpan username dan tanggal login hari ini.
     *
     * @param username Nama pengguna yang sedang login
     */
    public static void saveSession(String username) {
        Properties props = new Properties();
        props.setProperty(USER_KEY, username);
        props.setProperty(DATE_KEY, LocalDate.now().toString());

        try (FileOutputStream fos = new FileOutputStream(SESSION_FILE)) {
            props.store(fos, "User Session");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Memuat sesi login dari file jika valid untuk hari ini.
     * Jika ditemukan, sistem mencoba login otomatis menggunakan username tersebut.
     *
     * @return Objek Pengguna jika sesi valid dan login berhasil, atau null jika
     *         gagal
     */
    public static Pengguna getValidSession() {
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            return null; // File sesi tidak ditemukan
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(sessionFile)) {
            props.load(fis);
            String username = props.getProperty(USER_KEY);
            String dateStr = props.getProperty(DATE_KEY);

            if (username == null || dateStr == null) {
                return null; // Data sesi tidak lengkap
            }

            LocalDate loginDate = LocalDate.parse(dateStr);

            // Validasi apakah tanggal login sama dengan hari ini
            if (loginDate.equals(LocalDate.now())) {
                // Coba login otomatis menggunakan data pengguna yang tersimpan
                ManajemenPengguna mu = ManajemenPengguna.getInstance();
                if (mu.login(username, mu.getPasswordForUser(username))) {
                    return mu.getCurrentUser(); // Sesi valid, kembalikan objek Pengguna
                }
            }

        } catch (IOException e) {
            e.printStackTrace(); // Tangani error pembacaan file
        }

        // Jika sesi tidak valid, hapus file session
        clearSession();
        return null;
    }

    /**
     * Menghapus file sesi login yang tersimpan.
     * Biasanya dipanggil jika sesi tidak valid atau saat logout.
     */
    public static void clearSession() {
        File sessionFile = new File(SESSION_FILE);
        if (sessionFile.exists()) {
            sessionFile.delete(); // Hapus file sesi jika ada
        }
    }
}
