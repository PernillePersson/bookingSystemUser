package com.example.bookingsystemuser;

import com.example.bookingsystemuser.model.BookingCode;
import com.example.bookingsystemuser.model.BookingDAO;
import com.example.bookingsystemuser.model.BookingDAOImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

public class OpretFormularController {

    @FXML
    private TextField eNavn,  email, fNavn, org, tlf;

    @FXML
    private DatePicker bookingDato;

    @FXML
    private ToggleGroup bookingType, forplejning;

    @FXML
    private Hyperlink forplejningLink;

    @FXML
    private RadioButton midlType, permType, noToggle, yesToggle;

    @FXML
    private Button opretBookingKnap;

    @FXML
    private ComboBox formål, forløb, slutTid, startTid;

    @FXML
    private Text bemærkning;

    @FXML
    private Spinner antalDeltagere;

    private char type;
    private char forp;

    private String bKode;

    private Boolean midlertidig;

    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();


    public OpretFormularController() throws SQLException {
    }

    public void opsæt(){
        startTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        slutTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        startTid.setValue("07:00");
        slutTid.setValue("12:00");

        formål.getItems().addAll("Lokaleleje", "Åbent skoleforløb", "Andet");

        forløb.getItems().addAll("Idéfabrikken", "Digital fabrikation med laserskærer", "Robot på job",
                "Robotten rydder op", "Naturturisme ved Vadehavet", "Skab sikkerhedi i Vadehavet");
        forløb.setPromptText("Vælg forløb");

        forplejningLink.setVisible(false);
        //forplejningLink.setV
        bookingDato.setValue(LocalDate.now());
        forp = 'n';
        type = 'p';

    }

    @FXML
    void forplejningToggle(ActionEvent event) {
        if (forplejning.getSelectedToggle() == yesToggle){
            forplejningLink.setVisible(true);
            forp = 'y';
        } else if (forplejning.getSelectedToggle() == noToggle) {
            forplejningLink.setVisible(false);
            forp = 'n';
        }
    }

    @FXML
    void formålValgt(ActionEvent event) {
        formål.setOnAction((e) -> {
            if (formål.getSelectionModel().getSelectedIndex() == 1){
                forløb.setVisible(true);
            } else {
                forløb.setVisible(false);
            }
        });
    }

    @FXML
    void tjekDato(ActionEvent event) {
        if (slutTid.getSelectionModel().getSelectedIndex() >= 11 ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SUNDAY){
            opretBookingKnap.setText("Anmod om booking");
            bemærkning.setVisible(true);
            type = 't';
            midlertidig = true;

        }else {
            opretBookingKnap.setText("Opret booking");
            bemærkning.setVisible(false);
            type = 'p';
            midlertidig = false;
        }
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
                opretBookingKnap.setText("Anmod om booking");
                bemærkning.setVisible(true);
                type = 't';
                midlertidig = true;
            }else {
                opretBookingKnap.setText("Opret booking");
                bemærkning.setVisible(false);
                type = 'p';
                midlertidig = false;
            }
        });
    }

    @FXML
    void opdaterStartTid(MouseEvent event) {
        startTid.setOnAction((e) -> {
            if (startTid.getSelectionModel().getSelectedIndex() >= slutTid.getSelectionModel().getSelectedIndex()){
                startTid.setValue(startTid.getItems().get(slutTid.getSelectionModel().getSelectedIndex() -1));
            }
        });
    }

    @FXML
    void typeToggle(ActionEvent event) {
        if (bookingType.getSelectedToggle() == midlType){
            type = 't';
        } else if (bookingType.getSelectedToggle() == permType) {
            if (!midlertidig){
                type = 'p';
            }
        }
    }

    @FXML
    void opretBooking(ActionEvent event) {
        int nr = Integer.parseInt(tlf.getText());
        bKode = BookingCode.generateBookingCode();

        String organisation = org.getText();
        if (org.getText().isEmpty()){
            organisation = "Ingen";
        }


        List<Booking> allBookings = bdi.getAllBooking();
        boolean overlaps = false;

        for(Booking b : allBookings){

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

            if(bookingDato.getValue().equals(b.getBookingDate()) && comboSlt >= start && comboStrt <= slut){
                overlaps = true;
                break;
            }
        }

        if(!overlaps){
            bdi.addBooking(fNavn.getText(), eNavn.getText(), organisation, email.getText(), nr,
                    type, forp, bookingDato.getValue(), bKode, Time.valueOf(startTid.getValue() + ":00"),
                    Time.valueOf(slutTid.getValue() + ":00"), (Integer) antalDeltagere.getValue());

            Dialog<ButtonType> dialog = new Dialog();

            dialog.setTitle("Din bookingkode");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            Label l1 = new Label("Din bookingkode:");
            Label kodeLabel = new Label(bKode);
            kodeLabel.setFont(Font.font("ARIAL", FontWeight.BOLD, 20));
            Label infoLabel = new Label("Gem denne kode til senere brug");

            VBox vb = new VBox(l1, kodeLabel, infoLabel);
            vb.setSpacing(10);
            vb.setPadding(new Insets(10,30,10,30));

            dialog.getDialogPane().setContent(vb);

            Optional<ButtonType> knap = dialog.showAndWait();

            if (knap.get() == ButtonType.OK)
                try {
                    content.putString(bKode);
                    clipboard.setContent(content);
                } catch (Exception e) {
                }
        }
    }

    @FXML
    void anullerBooking(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    BookingDAO bdi = new BookingDAOImpl();
}
