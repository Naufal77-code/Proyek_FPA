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

public class RiwayatBlokirList {
    private static final String XML_FILE = "riwayat_blokir.xml";
    private final ObservableList<RiwayatBlokir> dataList;

    public RiwayatBlokirList() {
        dataList = FXCollections.observableArrayList();
        loadFromXML();
    }

    public ObservableList<RiwayatBlokir> getData() {
        return this.dataList;
    }

    public void setData(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        RiwayatBlokir newRiwayat = new RiwayatBlokir(nomor, tanggalMulai, durasi, status, aktivitas);
        dataList.add(newRiwayat);
        saveToXML();
    }
    
    public void remove(int nomor) {
        if (nomor > 0 && nomor <= dataList.size()) {
            dataList.remove(nomor - 1);
            updateNomor();
            saveToXML();
        } else {
            throw new IndexOutOfBoundsException("Nomor riwayat tidak valid untuk dihapus.");
        }
    }
    
    private void updateNomor() {
        for (int i = 0; i < dataList.size(); i++) {
            dataList.get(i).setNomor(i + 1);
        }
    }

    public void editAktivitas(int nomor, String aktivitasBaru) {
        if (nomor > 0 && nomor <= dataList.size()) {
            RiwayatBlokir riwayat = dataList.get(nomor - 1);
            riwayat.setAktivitas(aktivitasBaru.trim());
            saveToXML();
        }
    }

    private XStream createXStream() {
        XStream xstream = new XStream(new StaxDriver());
        XStream.setupDefaultSecurity(xstream);
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(String.class);
        xstream.allowTypesByWildcard(new String[] { "purify.**", "java.util.**" });
        xstream.alias("list", java.util.List.class);
        xstream.alias("RiwayatBlokir", RiwayatBlokir.class);
        return xstream;
    }

    public void saveToXML() {
        XStream xstream = createXStream();
        try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
            xstream.toXML(new ArrayList<>(this.dataList), fos);
        } catch (IOException e) {
            Logger.getLogger(RiwayatBlokirList.class.getName()).log(Level.SEVERE, "Gagal menyimpan data riwayat.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromXML() {
        File file = new File(XML_FILE);
        if (!file.exists()) return;

        XStream xstream = createXStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            ArrayList<RiwayatBlokir> loadedList = (ArrayList<RiwayatBlokir>) xstream.fromXML(fis);
            if (loadedList != null) {
                dataList.setAll(loadedList);
            }
        } catch (Exception e) {
            Logger.getLogger(RiwayatBlokirList.class.getName()).log(Level.SEVERE, "Gagal memuat data riwayat.", e);
        }
    }
}