package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatHistoryList {
    private static final Logger logger = Logger.getLogger(ChatHistoryList.class.getName());
    private static final String XML_FILE = "chat_history.xml";

    private final ObservableList<ChatRecord> dataList;

    public ChatHistoryList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML();
    }

    public ObservableList<ChatRecord> getData() {
        return this.dataList;
    }

    public void addChatRecord(ChatRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("ChatRecord tidak boleh null");
        }
        dataList.add(record);
        saveToXML();
    }

    private XStream createXStream() {
        XStream.setupDefaultSecurity(null);
        XStream xstream = new XStream(new StaxDriver());

        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(String.class);
        xstream.allowTypesByWildcard(new String[] { "purify.**" });
        xstream.allowTypeHierarchy(java.util.List.class);
        xstream.allowTypeHierarchy(ArrayList.class);

        xstream.alias("ChatRecord", ChatRecord.class);
        xstream.alias("list", java.util.List.class);

        return xstream;
    }

    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            ArrayList<ChatRecord> plainList = new ArrayList<>(this.dataList);
            xstream.toXML(plainList, fos);
            logger.log(Level.INFO, "Riwayat chat berhasil disimpan ke " + XML_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan riwayat chat ke XML", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists()) {
            logger.log(Level.INFO, "File " + XML_FILE + " tidak ditemukan.");
            return;
        }

        XStream xstream = createXStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<ChatRecord> loadedList = (ArrayList<ChatRecord>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList);
                logger.log(Level.INFO, "Riwayat chat berhasil dimuat dari " + XML_FILE);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saat parsing XML riwayat chat. File lama akan di-backup.", e);
            file.renameTo(new File(XML_FILE + ".corrupted_" + System.currentTimeMillis()));
        }
    }
} 

