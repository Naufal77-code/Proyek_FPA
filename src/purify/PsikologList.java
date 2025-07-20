package purify;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PsikologList {

    private static final Logger logger = Logger.getLogger(PsikologList.class.getName());
    private static final String XML_FILE = "data_psikolog.xml";

    private final ObservableList<Psikolog> dataList;

    public PsikologList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML();
    }

    public ObservableList<Psikolog> getData() {
        return this.dataList;
    }

    public void addPsikolog(Psikolog psikolog) {
        if (psikolog == null) {
            throw new IllegalArgumentException("Objek Psikolog tidak boleh null");
        }
        dataList.add(psikolog);
        saveToXML();
    }

    public void initializeDefaultPsikolog() {
        if (dataList.isEmpty()) {
            addPsikolog(new Psikolog("Dr. Ana Wijaya", "Psikolog Klinis", "Tersedia"));
            addPsikolog(new Psikolog("Budi Santoso, M.Psi", "Psikolog Pendidikan", "Sibuk"));
            addPsikolog(new Psikolog("Citra Dewi, S.Psi", "Psikolog Remaja", "Tersedia"));
            addPsikolog(new Psikolog("Doni Pratama, M.Psi", "Psikolog Keluarga", "Offline"));
            addPsikolog(new Psikolog("Ella Putri, S.Psi", "Konselor Karir", "Tersedia"));
            saveToXML();
        }
    }

    public void editPsikolog(String namaLama, String namaBaru, String spesialisasiBaru, String statusBaru) {
        for (Psikolog p : dataList) {
            if (p.getNama().equalsIgnoreCase(namaLama)) {
                p.setNama(namaBaru);
                p.setSpesialisasi(spesialisasiBaru);
                p.setStatus(statusBaru);
                saveToXML();
                return;
            }
        }
        logger.log(Level.WARNING, "Psikolog dengan nama " + namaLama + " tidak ditemukan.");
    }

    public void removePsikolog(String nama) {
        dataList.removeIf(p -> p.getNama().equalsIgnoreCase(nama));
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

        xstream.alias("Psikolog", Psikolog.class);
        xstream.alias("list", java.util.List.class);

        return xstream;
    }

    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            ArrayList<Psikolog> plainList = new ArrayList<>(this.dataList);
            xstream.toXML(plainList, fos);
            logger.log(Level.INFO, "Data psikolog berhasil disimpan ke " + XML_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal menyimpan data psikolog ke XML", e);
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
            ArrayList<Psikolog> loadedList = (ArrayList<Psikolog>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList);
                logger.log(Level.INFO, "Data psikolog berhasil dimuat dari " + XML_FILE);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saat parsing XML data psikolog. File lama akan di-backup.", e);
            file.renameTo(new File(XML_FILE + ".corrupted_" + System.currentTimeMillis()));
        }
    }
}
