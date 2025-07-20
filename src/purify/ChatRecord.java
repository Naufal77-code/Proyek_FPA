package purify;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatRecord {
    private String psikologNama;
    private String waktuMulaiChat;
    private String ringkasanChat; // Akan berisi seluruh percakapan atau ringkasannya

    // Properti JavaFX sebagai transient
    private transient StringProperty psikologNamaProperty;
    private transient StringProperty waktuMulaiChatProperty;
    private transient StringProperty ringkasanChatProperty;

    public ChatRecord() {
    }

    public ChatRecord(String psikologNama, String waktuMulaiChat, String ringkasanChat) {
        this.psikologNama = psikologNama;
        this.waktuMulaiChat = waktuMulaiChat;
        this.ringkasanChat = ringkasanChat;
    }

    // --- GETTER & SETTER Data Murni ---
    public String getPsikologNama() { return psikologNama; }
    public void setPsikologNama(String psikologNama) {
        this.psikologNama = psikologNama;
        if (psikologNamaProperty != null) psikologNamaProperty.set(psikologNama);
    }

    public String getWaktuMulaiChat() { return waktuMulaiChat; }
    public void setWaktuMulaiChat(String waktuMulaiChat) {
        this.waktuMulaiChat = waktuMulaiChat;
        if (waktuMulaiChatProperty != null) waktuMulaiChatProperty.set(waktuMulaiChat);
    }

    public String getRingkasanChat() { return ringkasanChat; }
    public void setRingkasanChat(String ringkasanChat) {
        this.ringkasanChat = ringkasanChat;
        if (ringkasanChatProperty != null) ringkasanChatProperty.set(ringkasanChat);
    }

    // --- PROPERTY GETTERS (untuk TableView) ---
    public StringProperty psikologNamaProperty() {
        if (psikologNamaProperty == null) psikologNamaProperty = new SimpleStringProperty(this, "psikologNama", psikologNama);
        return psikologNamaProperty;
    }

    public StringProperty waktuMulaiChatProperty() {
        if (waktuMulaiChatProperty == null) waktuMulaiChatProperty = new SimpleStringProperty(this, "waktuMulaiChat", waktuMulaiChat);
        return waktuMulaiChatProperty;
    }

    public StringProperty ringkasanChatProperty() {
        if (ringkasanChatProperty == null) ringkasanChatProperty = new SimpleStringProperty(this, "ringkasanChat", ringkasanChat);
        return ringkasanChatProperty;
    }

    private Object readResolve() {
        psikologNamaProperty();
        waktuMulaiChatProperty();
        ringkasanChatProperty();
        return this;
    }
}

