package purify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class SessionManager {

    private static final String SESSION_FILE = "session.properties";
    private static final String USER_KEY = "currentUser";
    private static final String DATE_KEY = "loginDate";

    // Menyimpan sesi login
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

    // Memuat sesi login yang valid untuk hari ini
    public static Pengguna getValidSession() {
        File sessionFile = new File(SESSION_FILE);
        if (!sessionFile.exists()) {
            return null;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(sessionFile)) {
            props.load(fis);
            String username = props.getProperty(USER_KEY);
            String dateStr = props.getProperty(DATE_KEY);

            if (username == null || dateStr == null) {
                return null;
            }

            LocalDate loginDate = LocalDate.parse(dateStr);

            // Cek apakah tanggal login sama dengan hari ini
            if (loginDate.equals(LocalDate.now())) {
                // Lakukan login otomatis untuk mendapatkan objek Pengguna
                ManajemenPengguna mu = ManajemenPengguna.getInstance();
                if (mu.login(username, mu.getPasswordForUser(username))) {
                    return mu.getCurrentUser();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Jika sesi tidak valid atau sudah kedaluwarsa, hapus file sesi
        clearSession();
        return null;
    }

    // Menghapus sesi
    public static void clearSession() {
        File sessionFile = new File(SESSION_FILE);
        if (sessionFile.exists()) {
            sessionFile.delete();
        }
    }
}