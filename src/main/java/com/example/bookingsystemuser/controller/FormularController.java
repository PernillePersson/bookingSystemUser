package com.example.bookingsystemuser.controller;

import com.example.bookingsystemuser.model.OrganisationDAO;
import com.example.bookingsystemuser.model.OrganisationDAOImpl;
import com.example.bookingsystemuser.model.objects.Booking;
import com.example.bookingsystemuser.model.BookingDAO;
import com.example.bookingsystemuser.model.BookingDAOImpl;
import com.example.bookingsystemuser.model.objects.Organisation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.util.List;

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

    public FormularController() throws SQLException {}

    public void passBooking(Booking b){
        booking = b;
        setDetails(booking);
    } // Sætter Booking b som værende den booking setDetails() metoden skal udfyldes med information om.

    public void setDetails(Booking b){
        // Sætter start og slut tiderne op i combobox
        startTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        slutTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");

        // Sætter fornavn og efternavn ind i navne label, og sætter email ind i email label, alt sammen fra den booking der er valgt.
        navnLabel.setText(b.getFirstName() + " " + b.getLastName());
        mailLabel.setText(b.getEmail());

        // Hvis man har valgt "andet" ved organisation, bliver organisations labelens navn det firmaet hedder.
        try {
           Organisation o = odi.getOrg(b.getId());
            if (o.getId() == 6){
                organisationLabel.setText(odi.getCompany(b.getId()).getCompany());
            }else{
                organisationLabel.setText(o.getOrganisation());
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        // Sætter telefon label med den balgte bookings telefonnummer
        tlfLabel.setText(String.valueOf(b.getPhoneNumber()));

        if (b.getCatering() == 'y'){
            forplejning.selectToggle(yesToggle);
        } else {
            forplejning.selectToggle(noToggle);
        }

        bookingDato.setValue(b.getBookingDate());
        startTid.setValue(String.valueOf(b.getStartTid()).substring(0,5));
        slutTid.setValue(String.valueOf(b.getSlutTid()).substring(0,5));
    } // Metode der fylder tekst i tekstfelterne og lign., med tekst fra den valgte booking
    @FXML
    void forplejningToggle(ActionEvent event) {
        if (forplejning.getSelectedToggle() == yesToggle){
            booking.setCatering('y');
        } else if (forplejning.getSelectedToggle() == noToggle) {
            booking.setCatering('n');
        }
    } // Sætter forplejning. Hvis ja(y) eller nej(n)

    @FXML
    void opdaterStartTid(MouseEvent event) {
        startTid.setOnAction((e) -> {
            if (startTid.getSelectionModel().getSelectedIndex() >= slutTid.getSelectionModel().getSelectedIndex()){
                startTid.setValue(startTid.getItems().get(slutTid.getSelectionModel().getSelectedIndex() -1));
            }
            booking.setStartTid(Time.valueOf(startTid.getValue() + ":00"));
        });
    } // Metode der sørger for at start tiden ikke er større end slut tiden

    @FXML
    void opdaterSlutTid(MouseEvent event) {
        slutTid.setOnAction((e) -> {
            if (slutTid.getSelectionModel().getSelectedIndex() <= startTid.getSelectionModel().getSelectedIndex()){ // Sørger for at slut tiden ikke kan være før start tiden
                slutTid.setValue(slutTid.getItems().get(startTid.getSelectionModel().getSelectedIndex() +1));
            }
            if (slutTid.getSelectionModel().getSelectedIndex() >= 11 || // Hvis slut tiden er efter 18, eller ligger i weekenden, er booking midlertidig(t)
                    bookingDato.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                    bookingDato.getValue().getDayOfWeek() == DayOfWeek.SUNDAY){
                booking.setBookingType('t');
            }else {
                booking.setBookingType('p');
            }
            booking.setSlutTid(Time.valueOf(slutTid.getValue() + ":00"));
        });
    } // Metode der sørger for at slut tiden ikke kan være mindre end start tiden, og styrer om en booking er midlertidig eller ej

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
    } // Metode der også styrer om booking er midlertidig eller permanent

    @FXML
    void opdaterBooking(ActionEvent event) throws SQLException {
        // Laver liste med alle bookinger
        List<Booking> allBookings = bdi.getAllBooking();
        boolean overlaps = false;

        for(Booking b : allBookings){

            // Laver substring af start og slut tidspunkter. Får de 2 første tal.
            String value = String.valueOf(b.getStartTid());
            String strt = value.substring(0,2);
            int start = Integer.valueOf(strt);

            String value2 = String.valueOf(b.getSlutTid());
            String slt = value2.substring(0,2);
            int slut = Integer.valueOf(slt);

            String value3 = String.valueOf(startTid.getValue());
            String comboStart = value3.substring(0,2);
            int comboStrt = Integer.valueOf(comboStart);

            String value4 = String.valueOf(slutTid.getValue());
            String comboSlut = value4.substring(0,2);
            int comboSlt = Integer.valueOf(comboSlut);

            // Tjekker om den tid man har indsat ikke overlapper andre bookinger. HVIS den overlapper sig selv, så kan man stadig godt ændre i den.
            if(bookingDato.getValue().equals(b.getBookingDate()) & b.getId() != b.getId() && comboSlt >= start && comboStrt <= slut){
                overlaps = true;
                break;
            }
        }

        // Hvis ikke den overlapper, så opdaterer vi bookingen med de nye informationer der blev skrevet ind. Derefter lukke vi vinduet.
        if(!overlaps) {

            bdi.updateBooking(booking.getId(), booking.getBookingType(), booking.getCatering(),
                    booking.getBookingDate(), booking.getStartTid(), booking.getSlutTid());

            //gmailSender.ændringsMail(booking.getEmail());

            Node source = (Node) event.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        }
    } // Metode der står for at opdatere den valgte booking. Opdaterer booking med de nye ting brugeren taster ind.

    @FXML
    void sletBooking(ActionEvent event) throws SQLException {
        // Sætter dialog vinduet up
        Dialog<ButtonType> dialog = new Dialog();

        dialog.setTitle("Slet booking");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label infoLabel = new Label("Er du sikker på, at denne booking skal slettes?");
        dialog.getDialogPane().setContent(infoLabel);

        Optional<ButtonType> knap = dialog.showAndWait();

        // Hvis man trykker ok, sletter man den valgte booking
        if (knap.get() == ButtonType.OK)
            try {
                bdi.cancelBooking(booking);
            } catch (Exception e) {
                System.err.println("Noget gik galt");
                System.err.println(e.getMessage());
            }
    } // Metode der står for at slette en valgt booking
    BookingDAO bdi = new BookingDAOImpl();
    OrganisationDAO odi = new OrganisationDAOImpl();
}