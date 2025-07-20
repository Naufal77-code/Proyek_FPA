package purify;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter; 
import javafx.scene.control.Alert;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FXMLJadwalKonsultasiController implements Initializable {

    @FXML
    private Label psikologNamaLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField timeField;
    @FXML
    private TextField locationField;
    @FXML
    private Button btnSimpanJadwal;
    @FXML
    private Button btnBatal;

    private Psikolog selectedPsikolog;
    private FXMLKonsultasiPsikologController mainController;
    private AppointmentList appointmentList; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appointmentList = new AppointmentList(); 

        if (btnSimpanJadwal != null) {
            btnSimpanJadwal.setOnAction(this::handleSimpanJadwal);
        }
        if (btnBatal != null) {
            btnBatal.setOnAction(this::handleBatal);
        }
    }

    public void setSelectedPsikolog(Psikolog psikolog) {
        this.selectedPsikolog = psikolog;
        if (psikologNamaLabel != null) {
            psikologNamaLabel.setText("Jadwalkan Konsultasi dengan " + psikolog.getNama() + " (" + psikolog.getSpesialisasi() + ")");
        }
    }

    public void setMainController(FXMLKonsultasiPsikologController controller) {
        this.mainController = controller;
    }

    @FXML
    private void handleSimpanJadwal(ActionEvent event) {
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeField.getText().trim();
        String selectedLocation = locationField.getText().trim();

        if (selectedPsikolog == null) {
            showAlert("Error", "Psikolog belum dipilih.");
            return;
        }
        if (selectedDate == null || selectedTime.isEmpty() || selectedLocation.isEmpty()) {
            showAlert("Peringatan", "Mohon lengkapi semua detail jadwal.");
            return;
        }

        String tanggalJadwal = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Appointment newAppointment = new Appointment(selectedPsikolog.getNama(), tanggalJadwal, selectedTime, selectedLocation, "Terjadwal");
        appointmentList.addAppointment(newAppointment);

        showAlert("Sukses", "Jadwal berhasil disimpan!");

        try {
            if (mainController != null) {
                mainController.refreshPsikologTable();
            }
            Stage currentStage = (Stage) btnSimpanJadwal.getScene().getWindow();
            currentStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/purify/FXMLKonsultasiPsikolog.fxml"))));
            currentStage.setTitle("Konsultasi dengan Psikolog");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }

    @FXML
    private void handleBatal(ActionEvent event) {
        try {
            if (mainController != null) {
                mainController.refreshPsikologTable();
            }
            Stage currentStage = (Stage) btnBatal.getScene().getWindow();
            currentStage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/purify/FXMLKonsultasiPsikolog.fxml"))));
            currentStage.setTitle("Konsultasi dengan Psikolog");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal kembali ke layar konsultasi: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}