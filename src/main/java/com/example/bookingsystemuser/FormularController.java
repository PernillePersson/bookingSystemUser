package com.example.bookingsystemuser;

import com.example.bookingsystemuser.Gmail.GEmail;
import com.example.bookingsystemuser.model.Booking;
import com.example.bookingsystemuser.model.BookingDAO;
import com.example.bookingsystemuser.model.BookingDAOImpl;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Optional;

public class FormularController {

    @FXML
    private DatePicker bookingDato;

    @FXML
    private ToggleGroup forplejning;

    @FXML
    private RadioButton yesToggle, noToggle;

    @FXML
    private Label mailLabel, navnLabel, organisationLabel, tlfLabel;

    @FXML
    private ComboBox slutTid, startTid;

    @FXML
    private Button opdaterBookingKnap;

    private char type;

    private Booking booking;

    public FormularController() throws SQLException {
    }


    public void passBooking(Booking b){
        booking = b;
        setDetails(booking);
    }

    public void setDetails(Booking b){
        startTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        slutTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");

        navnLabel.setText(b.getFirstName() + " " + b.getLastName());
        mailLabel.setText(b.getEmail());
        organisationLabel.setText(b.getOrganisation());
        tlfLabel.setText(String.valueOf(b.getPhoneNumber()));

        if (b.getCatering() == 'y'){
            forplejning.selectToggle(yesToggle);
        } else {
            forplejning.selectToggle(noToggle);
        }

        bookingDato.setValue(b.getBookingDate());
        startTid.setValue(String.valueOf(b.getStartTid()).substring(0,5));
        slutTid.setValue(String.valueOf(b.getSlutTid()).substring(0,5));

    }
    @FXML
    void forplejningToggle(ActionEvent event) {
        if (forplejning.getSelectedToggle() == yesToggle){
            booking.setCatering('y');
        } else if (forplejning.getSelectedToggle() == noToggle) {
            booking.setCatering('n');
        }
    }

    @FXML
    void opdaterStartTid(MouseEvent event) {
        startTid.setOnAction((e) -> {
            if (startTid.getSelectionModel().getSelectedIndex() >= slutTid.getSelectionModel().getSelectedIndex()){
                startTid.setValue(startTid.getItems().get(slutTid.getSelectionModel().getSelectedIndex() -1));
            }
            booking.setStartTid(Time.valueOf(startTid.getValue() + ":00"));
        });
    }

    @FXML
    void opdaterSlutTid(MouseEvent event) {
        slutTid.setOnAction((e) -> {
            if (slutTid.getSelectionModel().getSelectedIndex() <= startTid.getSelectionModel().getSelectedIndex()){
                slutTid.setValue(slutTid.getItems().get(startTid.getSelectionModel().getSelectedIndex() +1));
            }
            if (slutTid.getSelectionModel().getSelectedIndex() >= 11 ||
                    bookingDato.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                    bookingDato.getValue().getDayOfWeek() == DayOfWeek.SUNDAY){
                booking.setBookingType('t');
            }else {
                booking.setBookingType('p');
            }
            booking.setSlutTid(Time.valueOf(slutTid.getValue() + ":00"));
        });
    }

    @FXML
    void ændreDato(ActionEvent event) {
        booking.setBookingDate(bookingDato.getValue());
            if (slutTid.getSelectionModel().getSelectedIndex() >= 11 ||
                    bookingDato.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                    bookingDato.getValue().getDayOfWeek() == DayOfWeek.SUNDAY){
                booking.setBookingType('t');

            }else {
                booking.setBookingType('p');
            }

    }


    @FXML
    void opdaterBooking(ActionEvent event) throws SQLException {
        bdi.updateBooking(booking.getId(), booking.getBookingType(), booking.getCatering(),
                booking.getBookingDate(), booking.getStartTid(), booking.getSlutTid());


        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();

    }

    public void ændringsMail(String t){

        GEmail gmailSender = new GEmail();

        String from = "noreplybookingsystemem@gmail.com";
        String subject = "Ændring af booking";
        String text = "Der er blevet foretaget en ændring i din booking. Gå venligst ind og tjek ændringerne";
        gmailSender.sendEmail(t,from,subject,text);
    }


    @FXML
    void sletBooking(ActionEvent event) throws SQLException {
        Dialog<ButtonType> dialog = new Dialog();

        dialog.setTitle("Slet booking");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label infoLabel = new Label("Er du sikker på, at denne booking skal slettes?");
        dialog.getDialogPane().setContent(infoLabel);

        Optional<ButtonType> knap = dialog.showAndWait();

        if (knap.get() == ButtonType.OK)
            try {
                bdi.cancelBooking(booking);
            } catch (Exception e) {
                System.err.println("Noget gik galt");
                System.err.println(e.getMessage());
            }

    }

    BookingDAO bdi = new BookingDAOImpl();
}