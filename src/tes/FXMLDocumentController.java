package tes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Label statusLabel;

    @FXML
    private Button btnBlokir;

    @FXML
    private TextField durasiField;

    @FXML
    private TableView<RiwayatBlokir> riwayatTable;

    @FXML
    private TableColumn<RiwayatBlokir, String> colNomor;

    @FXML
    private TableColumn<RiwayatBlokir, String> colRiwayat;

    private ObservableList<RiwayatBlokir> dataRiwayat;
    private int nomorCounter = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNomor.setCellValueFactory(new PropertyValueFactory<>("nomor"));
        colRiwayat.setCellValueFactory(new PropertyValueFactory<>("riwayat"));

        dataRiwayat = FXCollections.observableArrayList();
        riwayatTable.setItems(dataRiwayat);
    }

    @FXML
    private void handleBlokir(ActionEvent event) {
        String durasi = durasiField.getText();

        if (durasi == null || durasi.trim().isEmpty()) {
            statusLabel.setText("Durasi kosong!");
            return;
        }

        statusLabel.setText("HP Diblokir");

        String nomor = "";
        for (int i = 1; i <= nomorCounter; i++) {
            if (i == nomorCounter) {
                nomor = String.valueOf(i);
            }
        }

        String riwayat = "Blokir selama " + durasi + " menit";
        dataRiwayat.add(new RiwayatBlokir(nomor, riwayat));

        nomorCounter++;
    }
}
