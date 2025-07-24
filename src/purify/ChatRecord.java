package purify;

// Import untuk properti observabel JavaFX (berguna untuk binding dengan TableView, Label, dll)
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ChatRecord {

    // --- ATRIBUT DATA MURNI (bukan JavaFX property) ---

    // Nama psikolog yang melakukan sesi chat
    private String psikologNama;

    // Waktu saat sesi chat dimulai (dalam format string)
    private String waktuMulaiChat;

    // Ringkasan isi percakapan antara pengguna dan psikolog
    private String ringkasanChat;

    // --- ATRIBUT PROPERTY (khusus JavaFX untuk binding dengan UI seperti
    // TableView) ---

    // Property nama psikolog, memungkinkan update UI otomatis saat data berubah
    private transient StringProperty psikologNamaProperty;

    // Property waktu mulai chat
    private transient StringProperty waktuMulaiChatProperty;

    // Property ringkasan chat
    private transient StringProperty ringkasanChatProperty;

    // --- KONSTRUKTOR ---

    // Konstruktor default (diperlukan untuk deserialisasi XML/JSON)
    public ChatRecord() {
    }

    // Konstruktor lengkap untuk inisialisasi objek chat dengan data lengkap
    public ChatRecord(String psikologNama, String waktuMulaiChat, String ringkasanChat) {
        this.psikologNama = psikologNama;
        this.waktuMulaiChat = waktuMulaiChat;
        this.ringkasanChat = ringkasanChat;
    }

    // --- GETTER & SETTER ATRIBUT DATA MURNI ---

    // Mengembalikan nama psikolog
    public String getPsikologNama() {
        return psikologNama;
    }

    // Mengatur nama psikolog dan memperbarui property jika sudah diinisialisasi
    public void setPsikologNama(String psikologNama) {
        this.psikologNama = psikologNama;
        if (psikologNamaProperty != null)
            psikologNamaProperty.set(psikologNama);
    }

    // Mengembalikan waktu mulai sesi chat
    public String getWaktuMulaiChat() {
        return waktuMulaiChat;
    }

    // Mengatur waktu mulai chat dan memperbarui property jika sudah diinisialisasi
    public void setWaktuMulaiChat(String waktuMulaiChat) {
        this.waktuMulaiChat = waktuMulaiChat;
        if (waktuMulaiChatProperty != null)
            waktuMulaiChatProperty.set(waktuMulaiChat);
    }

    // Mengembalikan ringkasan isi chat
    public String getRingkasanChat() {
        return ringkasanChat;
    }

    // Mengatur ringkasan chat dan memperbarui property jika sudah diinisialisasi
    public void setRingkasanChat(String ringkasanChat) {
        this.ringkasanChat = ringkasanChat;
        if (ringkasanChatProperty != null)
            ringkasanChatProperty.set(ringkasanChat);
    }

    // --- PROPERTY GETTERS (UNTUK PENGGUNAAN DI JavaFX TableView / Label) ---

    // Mengembalikan properti nama psikolog untuk binding ke UI
    public StringProperty psikologNamaProperty() {
        if (psikologNamaProperty == null)
            psikologNamaProperty = new SimpleStringProperty(this, "psikologNama", psikologNama);
        return psikologNamaProperty;
    }

    // Mengembalikan properti waktu mulai chat
    public StringProperty waktuMulaiChatProperty() {
        if (waktuMulaiChatProperty == null)
            waktuMulaiChatProperty = new SimpleStringProperty(this, "waktuMulaiChat", waktuMulaiChat);
        return waktuMulaiChatProperty;
    }

    // Mengembalikan properti ringkasan chat
    public StringProperty ringkasanChatProperty() {
        if (ringkasanChatProperty == null)
            ringkasanChatProperty = new SimpleStringProperty(this, "ringkasanChat", ringkasanChat);
        return ringkasanChatProperty;
    }

    // --- SERIALIZATION SUPPORT (saat membaca ulang objek dari file XML/JSON) ---

    // Method ini akan dipanggil otomatis setelah deserialisasi
    // Tujuannya agar properti JavaFX tetap terhubung dengan nilai aktual
    private Object readResolve() {
        psikologNamaProperty();
        waktuMulaiChatProperty();
        ringkasanChatProperty();
        return this;
    }
}
