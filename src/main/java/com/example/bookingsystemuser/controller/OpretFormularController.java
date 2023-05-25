package com.example.bookingsystemuser.controller;

import com.example.bookingsystemuser.model.*;
import com.example.bookingsystemuser.model.objects.Booking;
import com.example.bookingsystemuser.model.objects.Forløb;
import com.example.bookingsystemuser.model.objects.Organisation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.TRANSPARENT;

public class OpretFormularController {

    @FXML
    private TextField eNavn, email, fNavn, org, tlf;

    @FXML
    private DatePicker bookingDato;

    @FXML
    private ToggleGroup bookingType, forplejning;

    @FXML
    private Hyperlink forplejningLink, forløbLink;

    @FXML
    private RadioButton midlType, permType, noToggle, yesToggle;

    @FXML
    private Button opretBookingKnap;

    @FXML
    private ComboBox orgBox, formål, forløb, slutTid, startTid;

    @FXML
    private Text bemærkning, optagetTekst;

    @FXML
    private Spinner antalDeltagere;

    private char type;
    private char forp;

    private String bKode;

    private Boolean midlertidig;

    private Forløb f1;
    private Organisation o1;

    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();

    public OpretFormularController() throws SQLException {}

    public void opsæt(LocalDate d, Time st, Time et) {
        // Sætter alle de tidspunkter vi vil have, ind i vores comboboxes.
        startTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        slutTid.getItems().addAll("07:00", "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
                "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");
        startTid.setValue(String.valueOf(st).substring(0, 5));
        slutTid.setValue(String.valueOf(et).substring(0, 5));

        // Sætter alle de formål vi vil have i vores combobox
        formål.getItems().addAll("Lokaleleje", "Åbent skoleforløb", "Andet");
        formål.setValue("Lokaleleje");

        // Liste med organisationer
        List<Organisation> org = odi.getAllOrg();
        for (Organisation o : org) {
            orgBox.getItems().add(o);
        }

        // Liste med forløb
        List<Forløb> forl = bdi.getAllForløb();
        for (Forløb f : forl) {
            forløb.getItems().add(f);
        }
        f1 = (Forløb) forløb.getItems().get(6); //Det index hvor forløb er "ingen"
        forplejningLink.setVisible(false);
        bookingDato.setValue(d);
        forp = 'n';
        type = 'p';

        // Hvis bookingen falder enten lørdag eller søndag, eller er efter kl. 18
        // vil ens booking være midlertid
        if (slutTid.getSelectionModel().getSelectedIndex() >= 11 ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SUNDAY) {
            opretBookingKnap.setText("Anmod om booking");
            bemærkning.setVisible(true);
            type = 't';
            midlertidig = true;

        } else { // Ellers er det en normal booking
            opretBookingKnap.setText("Opret booking");
            bemærkning.setVisible(false);
            type = 'p';
            midlertidig = false;
        }
    } // Opsætter vores comboboxes med det rigtige indhold

    public void initialize() {
        tjekCharacter();
    }
    public void tjekCharacter(){
        eNavn.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                eNavn.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });

        fNavn.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                fNavn.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });

        email.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z-\\d-@-.*")) {
                email.setText(newValue.replaceAll("[^\\sa-zA-Z-\\d-.-@]", ""));
            }
        });

        org.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\sa-zA-Z*")) {
                org.setText(newValue.replaceAll("[^\\sa-zA-Z]", ""));
            }
        });

        tlf.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tlf.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (tlf.getLength() > 8) {
                String MAX = tlf.getText().substring(0,8);
                tlf.setText(MAX);
            }
        });
    } // Metode der fungerer som et filtreringssytem. Systemet sørger for at brugeren ikke indtaster uønskede ting
      // i de forskellige tekstfelter.

    public void åbenForløb() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://skoletjenesten.dk/mind-factory-ecco"));
    } // Åbner forløbet i en browser

    @FXML
    void forplejningToggle(ActionEvent event) {
        if (forplejning.getSelectedToggle() == yesToggle) {
            forplejningLink.setVisible(true);
            forp = 'y';
        } else if (forplejning.getSelectedToggle() == noToggle) {
            forplejningLink.setVisible(false);
            forp = 'n';
        }
    } // Sætter forplejning som enten ja(y) eller nej(n)

    @FXML
    void orgValgt(ActionEvent event) {
        if (orgBox.getSelectionModel().getSelectedIndex() == 5) {
            org.setVisible(true);
        } else {
            org.setVisible(false);
        }
        o1 = (Organisation) orgBox.getValue();
    } // Sætter organisationen til det brugeren har valgt

    @FXML
    void formålValgt(ActionEvent event) {
        if (formål.getSelectionModel().getSelectedIndex() == 1) { // Hvis første formål valgt gør følgende...
            forløb.setVisible(true);
            forløbLink.setVisible(true);
        } else { // Ellers gør følgende...
            forløb.setVisible(false);
            forløbLink.setVisible(false);
            f1 = (Forløb) forløb.getItems().get(6); //Det index hvor forløb er "ingen"
        }
    } // Hvis det er det første formål der er valgt skal man kunne se de forskellige forløb.

    @FXML
    void forløbValgt(ActionEvent event) {
        f1 = (Forløb) forløb.getValue();
    } // Får det valgte forløb

    public void hentForplejning(ActionEvent event) {
        try {
            File pdf = new File("src/main/resources/com/example/bookingsystemuser/forplejning.pdf");
            Desktop.getDesktop().open(pdf);
        } catch (Exception e) {
            System.out.println("Kunne ikke hente pdf" + e.getMessage());
        }
    } // Åbner forplejningsformular hvis brugeren klikker på den

    @FXML
    void tjekDato(ActionEvent event) {
        if (slutTid.getSelectionModel().getSelectedIndex() >= 11 ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SUNDAY) {
            opretBookingKnap.setText("Anmod om booking");
            bemærkning.setVisible(true);
            type = 't';
            midlertidig = true;
        } else {
            opretBookingKnap.setText("Opret booking");
            bemærkning.setVisible(false);
            type = 'p';
            midlertidig = false;
        }
    } // Hvis dato er i søndag, lørdag eller hvis klokken er over 18 mht. booking, bliver booking sat som midlertidig(t)

    @FXML
    void opdaterSlutTid(ActionEvent event) {
        if (slutTid.getSelectionModel().getSelectedIndex() <= startTid.getSelectionModel().getSelectedIndex()) { // Hvis man prøver at sætte sluttidspunktet til at være før starttidspunktet gør følgende..
            slutTid.setValue(slutTid.getItems().get(startTid.getSelectionModel().getSelectedIndex() + 1));
        }
        if (slutTid.getSelectionModel().getSelectedIndex() >= 11 ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                bookingDato.getValue().getDayOfWeek() == DayOfWeek.SUNDAY) {
            opretBookingKnap.setText("Anmod om booking");
            bemærkning.setVisible(true);
            type = 't';
            midlertidig = true;
        } else {
            opretBookingKnap.setText("Opret booking");
            bemærkning.setVisible(false);
            type = 'p';
            midlertidig = false;
        }
    } // Metode til at opdatere slut tid

    @FXML
    void opdaterStartTid(ActionEvent event) {
        if (startTid.getSelectionModel().getSelectedIndex() >= slutTid.getSelectionModel().getSelectedIndex()) { // Hvis starttidspunktet er efter sluttidspunktet, gør følgende...
            startTid.setValue(startTid.getItems().get(slutTid.getSelectionModel().getSelectedIndex() - 1));
        }
    } // Metode der opdaterer start tidspunktet.

    @FXML
    void typeToggle(ActionEvent event) {
        if (bookingType.getSelectedToggle() == midlType) {
            type = 't';
        } else if (bookingType.getSelectedToggle() == permType) {
            if (!midlertidig) {
                type = 'p';
            }
        }
    } // Metode der ser hvilken type booking det er

    @FXML
    void opretBooking(ActionEvent event) {

        // Hvis der ikke er blevet sat noget ind i de forskellige felter, vil den blive markeret med rød
        if (fNavn.getLength() == 0) {
            fNavn.setBorder(new Border(new BorderStroke(RED, BorderStrokeStyle.SOLID, null, null)));

        } else if (eNavn.getLength() == 0) {
            eNavn.setBorder(new Border(new BorderStroke(RED, BorderStrokeStyle.SOLID, null, null)));

        } else if (email.getLength() == 0) {
            email.setBorder(new Border(new BorderStroke(RED, BorderStrokeStyle.SOLID, null, null)));

        } else if (tlf.getLength() != 8) {
            tlf.setBorder(new Border(new BorderStroke(RED, BorderStrokeStyle.SOLID, null, null)));

        } else if (antalDeltagere.getValue().equals(0)) {
            antalDeltagere.setBorder(new Border(new BorderStroke(RED, BorderStrokeStyle.SOLID, null, null)));

        } else if (formål.getValue().equals("Åbent skoleforløb") && forløb.getValue().equals("Intet")) {
            forløb.setBorder(new Border(new BorderStroke(RED, BorderStrokeStyle.SOLID, null, null)));


        // Hvis alt er i orden, bliver bookingkode genereret.
        } else {
            int nr = Integer.parseInt(tlf.getText());
            bKode = BookingCode.generateBookingCode();

            String organisation = org.getText();
            if (org.getText().isEmpty()) {
                organisation = "Ingen";
            }

            //GEmail gmailSender = new GEmail();

            // Laver liste med alle bookings
            List<Booking> allBookings = bdi.getAllBooking();
            boolean overlaps = false;

            for (Booking b : allBookings) {

                // Laver substrings af de forskellige starttider og sluttider. Vi ender med kun at få de første 2 tal.
                String value = String.valueOf(b.getStartTid());
                String strt = value.substring(0, 2);
                int start = Integer.valueOf(strt);

                String value2 = String.valueOf(b.getSlutTid());
                String slt = value2.substring(0, 2);
                int slut = Integer.valueOf(slt);

                String value3 = String.valueOf(startTid.getValue());
                String comboStart = value3.substring(0, 2);
                int comboStrt = Integer.valueOf(comboStart);

                String value4 = String.valueOf(slutTid.getValue());
                String comboSlut = value4.substring(0, 2);
                int comboSlt = Integer.valueOf(comboSlut);

                // Tjekker om de tider man har sat ind i sin booking allerede er optaget. Hvis ja, bliver overlaps sat som true
                if (bookingDato.getValue().equals(b.getBookingDate()) && comboSlt >= start && comboStrt <= slut) {
                    overlaps = true;
                    break;
                }
            }
            // Hvis der ikke er nogen overlap, bliver der oprettet en booking med alt det brugeren har fyldt ud.
            if (!overlaps) {
                bdi.addBooking(fNavn.getText(), eNavn.getText(), email.getText(), nr,
                        type, forp, bookingDato.getValue(), bKode, Time.valueOf(startTid.getValue() + ":00"),
                        Time.valueOf(slutTid.getValue() + ":00"), (Integer) antalDeltagere.getValue());

                // Tilføjer bookingkode og forløb, og bookingkode og organisation til henholdsvis BookingForløb og BookingOrg i databasen.
                bdi.addForløb(bKode, f1.getId());
                odi.addOrg(bKode, o1.getId());

                // Hvis der er valgt "andet" ved organisation, bliver der indsat en ny company i Company tablen i db'en.
                if (o1.getId() == 6) {
                    odi.addCompany(bKode, org.getText());
                }

                Dialog<ButtonType> dialog = new Dialog();

                dialog.setTitle("Din bookingkode");
                dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                // Viser bookingkode på label
                Label l1 = new Label("Din bookingkode:");
                Label kodeLabel = new Label(bKode);
                kodeLabel.setFont(Font.font("ARIAL", FontWeight.BOLD, 20));
                Label infoLabel = new Label("Gem denne kode til senere brug");

                //gmailSender.sendBookingCode(email.getText(), fNavn.getText(),bKode);

                VBox vb = new VBox(l1, kodeLabel, infoLabel);
                vb.setSpacing(10);
                vb.setPadding(new Insets(10, 30, 10, 30));

                dialog.getDialogPane().setContent(vb);

                Optional<ButtonType> knap = dialog.showAndWait();

                // Kopierer bookingkode til ens clipboard automatisk
                if (knap.get() == ButtonType.OK){
                    try {
                        content.putString(bKode);
                        clipboard.setContent(content);
                    } catch (Exception e) {
                    }
                }
            } else {
                optagetTekst.setVisible(true);
            }
        }
    } // Metode der opretter bookingen i db'en

    @FXML
    void anullerBooking(ActionEvent event) {
        Node source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    } // Hvis man afbryder sin oprettelse af booking lukker man vinduet.

    BookingDAO bdi = new BookingDAOImpl();
    OrganisationDAO odi = new OrganisationDAOImpl();
}