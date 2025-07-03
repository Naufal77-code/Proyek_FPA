package purify;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RiwayatBlokir {
    private static final Logger logger = Logger.getLogger(RiwayatBlokir.class.getName());
    private static final String XML_FILE = "riwayat_blokir_single.xml";
    private final IntegerProperty nomor;
    private final StringProperty tanggalMulai;
    private final IntegerProperty durasi;
    private final StringProperty status;
    private final StringProperty aktivitas;

    public RiwayatBlokir() {
        this(0, "", 0, "", "");
    }

    public RiwayatBlokir(int nomor, String tanggalMulai, int durasi, String status, String aktivitas) {
        this.nomor = new SimpleIntegerProperty(nomor);
        this.tanggalMulai = new SimpleStringProperty(tanggalMulai);
        this.durasi = new SimpleIntegerProperty(durasi);
        this.status = new SimpleStringProperty(status);
        this.aktivitas = new SimpleStringProperty(aktivitas);
    }

    public int getNomor() {
        return nomor.get();
    }

    public void setNomor(int nomor) {
        this.nomor.set(nomor);
    }

    public IntegerProperty nomorProperty() {
        return nomor;
    }


    public String getTanggalMulai() {
        return tanggalMulai.get();
    }

    public void setTanggalMulai(String tanggalMulai) {
        this.tanggalMulai.set(tanggalMulai);
    }

    public StringProperty tanggalMulaiProperty() {
        return tanggalMulai;
    }


    public int getDurasi() {
        return durasi.get();
    }

    public void setDurasi(int durasi) {
        this.durasi.set(durasi);
    }

    public IntegerProperty durasiProperty() {
        return durasi;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getAktivitas() {
        return aktivitas.get();
    }

    public void setAktivitas(String aktivitas) {
        this.aktivitas.set(aktivitas);
    }

    public StringProperty aktivitasProperty() {
        return aktivitas;
    }

    @Override
    public String toString() {
        return "RiwayatBlokir{" +
                "nomor=" + getNomor() +
                ", tanggalMulai='" + getTanggalMulai() + '\'' +
                ", durasi=" + getDurasi() +
                ", status='" + getStatus() + '\'' +
                ", aktivitas='" + getAktivitas() + '\'' +
                '}';
    }

public void saveToXML(){
    XStream xstream = new XStream(new DomDriver());
    xstream.alias("RiwayatBlokir", RiwayatBlokir.class);
    xstream.alias("RiwayatBlokirList", RiwayatBlokir.class);

    try (FileOutputStream fos = new FileOutputStream(XML_FILE)) {
        xstream.toXML(this,fos);
        logger.log(Level.INFO, "Data riwayat berhasil disimpam ke "+XML_FILE);
    } catch (Exception e) {
        logger.log(Level.SEVERE, "Gagal menyimpan data riwayat ke XML",e);
    }
}

public static RiwayatBlokir loadFromXML(){
   File file = new File(XML_FILE);
   if (!file.exists()){
    logger.log(Level.INFO,"File "+XML_FILE+" tidak ditemukan");
    return null;
   }

   XStream xstream = createSecureXStream();
   xstream.alias("RiwayatBlokir", RiwayatBlokir.class);

   try (FileInputStream fis = new FileInputStream(file)) {
            RiwayatBlokir loadedRiwayat = (RiwayatBlokir) xstream.fromXML(fis);
            logger.log(Level.INFO, "Data riwayat berhasil dimuat dari " + XML_FILE);
            return loadedRiwayat;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Gagal memuat data riwayat dari XML", e);
            return null;
        }
}


    private static XStream createSecureXStream() {
        XStream xstream = new XStream(new DomDriver());
        
        xstream.addPermission(NoTypePermission.NONE);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(RiwayatBlokir.class);
        xstream.allowTypesByWildcard(new String[] {
            "purify.**"
        });
        
        return xstream;
    }

}