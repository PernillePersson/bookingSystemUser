package com.example.bookingsystemuser.controller;

import com.example.bookingsystemuser.UserApplication;
import com.example.bookingsystemuser.model.objects.Booking;
import com.example.bookingsystemuser.model.BookingDAO;
import com.example.bookingsystemuser.model.BookingDAOImpl;
import com.example.bookingsystemuser.model.SimpleThread;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class UserController {

    @FXML
    private Pane fredagPane, lørdagPane, mandagPane, onsdagPane,
            søndagPane, tirsdagPane, torsdagPane;

    @FXML
    private Label monthLabel, yearLabel, mandagDato, tirsdagDato,
            onsdagDato, torsdagDato, fredagDato, lørdagDato, søndagDato;

    @FXML
    private TextField bkTekst;
    private String bKode;

    private LocalDate shownDate;
    private LocalDate today;
    private LocalDate lde;

    private final SimpleThread simpleThread;

    private double y_start,y_end;
    ArrayList<Rectangle> manRectangles = new ArrayList<>();
    ArrayList<Rectangle> tirsRectangles = new ArrayList<>();
    ArrayList<Rectangle> onsRectangles = new ArrayList<>();
    ArrayList<Rectangle> torsRectangles = new ArrayList<>();
    ArrayList<Rectangle> freRectangles = new ArrayList<>();
    ArrayList<Rectangle> lørRectangles = new ArrayList<>();
    ArrayList<Rectangle> sønRectangles = new ArrayList<>();
    ArrayList<Label> labels = new ArrayList<>();
    HashMap<Rectangle, Booking> rectangleBooking = new HashMap<>();
    

    public UserController() throws SQLException {
        simpleThread = new SimpleThread(this);
    }

    public void initialize(){
        shownDate = LocalDate.now();
        today = LocalDate.now();
        opsætDato();
        insertSystemBookings();
        simpleThread.start();

        // sendNotificationEmails(); // er udkommenteret grundet at den ikke fungerer på EASV netværk
    } // Vores initialize der kører alle de metoder der skal køres fra starten af. En thread der kører hvert sekund
    // og opdaterer vores view

    @FXML
    void opretBookingKnap(ActionEvent event) throws IOException {
        //Skift scene til bookingformular
        opretBooking(LocalDate.now(), Time.valueOf("07:00:00"), Time.valueOf("12:00:00"));
    } // Knap der åbner bookingformular vha. opretBooking()

    public void opretBooking(LocalDate d, Time st, Time et) throws IOException {
        //åben formular med alt booking info, og knap der opdaterer
        FXMLLoader fxmlLoader = new FXMLLoader(UserApplication.class.getResource("bookingFormular.fxml"));
        Scene oversigtScene = new Scene(fxmlLoader.load());
        OpretFormularController formController = fxmlLoader.getController();
        Stage oversigtStage = new Stage();
        oversigtStage.setScene(oversigtScene);
        formController.opsæt(d, st, et);

        // Hvis der klikkes udenfor vinduet, lukkes det
        oversigtStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                oversigtStage.hide();
                insertSystemBookings();
            }
        });
        oversigtStage.show();
    } // Metode der åbner bookingformularen

    @FXML
    void findBooking(ActionEvent event) throws IOException {
        bKode = bkTekst.getText();
        Booking b = bdi.getMyBooking(bKode);
        ændreBooking(b);
    } // Når bruger har indtastet bookingkode i tekstfeltet og trykker på knappen, finder den bookingen og åbner bookingoversigten der bliver lavet i ændreBooking()

    public void ændreBooking(Booking b) throws IOException {
        try {
            //åben formular med alt booking info, og knap der opdaterer
            FXMLLoader fxmlLoader = new FXMLLoader(UserApplication.class.getResource("bookingsoversigt.fxml"));
            Scene oversigtScene = new Scene(fxmlLoader.load());
            FormularController formController = fxmlLoader.getController();
            Stage oversigtStage = new Stage();
            oversigtStage.setScene(oversigtScene);
            formController.passBooking(b);

            // Hvis der klikkes udenfor vinduet, lukkes det
            oversigtStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (! isNowFocused) {
                    oversigtStage.hide();
                    insertSystemBookings();
                }
            });

            oversigtStage.show();
        } catch (NullPointerException n){ // Hvis koden ikke findes sker nedenstående
            System.err.println("Ingen booking fundet med denne bookingkode " + n.getMessage());
            Dialog<ButtonType> dialog = new Dialog();

            dialog.setTitle("Ingen bookingkode");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            Label l1 = new Label("Kunne ikke finde bookingkode");
            l1.setFont(Font.font("ARIAL", FontWeight.BOLD, 20));

            VBox vb = new VBox(l1);
            vb.setSpacing(10);
            vb.setPadding(new Insets(10,30,10,30));

            dialog.getDialogPane().setContent(vb);

            Optional<ButtonType> knap = dialog.showAndWait();

            if (knap.get() == ButtonType.OK)
                try {

                } catch (Exception ex) {
                }
        }
    } // Metode der åbner bookingoversigt

    @FXML
    void forrigeUgeKnap(ActionEvent event) {
        shownDate = shownDate.minusWeeks(1);
        opsætDato();
        insertSystemBookings();
    } //Viser forrige uge i kalenderoversigt

    @FXML
    void todayKnap(ActionEvent event) {
        shownDate = today;
        opsætDato();
        insertSystemBookings();
    } //Viser denne uge i kalenderoversigt

    @FXML
    void næsteUgeKnap(ActionEvent event) {
        shownDate = shownDate.plusWeeks(1);
        opsætDato();
        insertSystemBookings();
        System.out.println(shownDate);
    } //Viser næste uge i kalenderoversigt

    public void opsætDato(){
        oversætMdr();
        yearLabel.setText(String.valueOf(shownDate.getYear()));
        mandagDato.setText(String.valueOf(shownDate.with(DayOfWeek.MONDAY).getDayOfMonth()));
        tirsdagDato.setText(String.valueOf(shownDate.with(DayOfWeek.TUESDAY).getDayOfMonth()));
        onsdagDato.setText(String.valueOf(shownDate.with(DayOfWeek.WEDNESDAY).getDayOfMonth()));
        torsdagDato.setText(String.valueOf(shownDate.with(DayOfWeek.THURSDAY).getDayOfMonth()));
        fredagDato.setText(String.valueOf(shownDate.with(DayOfWeek.FRIDAY).getDayOfMonth()));
        lørdagDato.setText(String.valueOf(shownDate.with(DayOfWeek.SATURDAY).getDayOfMonth()));
        søndagDato.setText(String.valueOf(shownDate.with(DayOfWeek.SUNDAY).getDayOfMonth()));
    } //Sætter alle labels til at passe med datoer

    public void oversætMdr(){
        if (shownDate.getMonth() == Month.JANUARY){
            monthLabel.setText("Jan");
        } else if (shownDate.getMonth() == Month.FEBRUARY) {
            monthLabel.setText("Feb");
        } else if (shownDate.getMonth() == Month.MARCH) {
            monthLabel.setText("Mar");
        } else if (shownDate.getMonth() == Month.APRIL) {
            monthLabel.setText("Apr");
        } else if (shownDate.getMonth() == Month.MAY) {
            monthLabel.setText("Maj");
        } else if (shownDate.getMonth() == Month.JUNE) {
            monthLabel.setText("Jun");
        } else if (shownDate.getMonth() == Month.JULY) {
            monthLabel.setText("Jul");
        } else if (shownDate.getMonth() == Month.AUGUST) {
            monthLabel.setText("Aug");
        } else if (shownDate.getMonth() == Month.SEPTEMBER) {
            monthLabel.setText("Sep");
        } else if (shownDate.getMonth() == Month.OCTOBER) {
            monthLabel.setText("Oct");
        } else if (shownDate.getMonth() == Month.NOVEMBER) {
            monthLabel.setText("Nov");
        } else if (shownDate.getMonth() == Month.DECEMBER) {
            monthLabel.setText("Dec");
        }
    } //Sætter mdr labels til at være dansk

    @FXML
    void mondayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    } // Sætter drag til true og får en y_værdi ud fra musens lokation.
                                        // Denne metode er præcis den samme for alle de andre *ugedag*Press events.

    @FXML
    void mondayRelease(MouseEvent event) throws IOException {
        try {
            y_end = event.getY();
            addStack(mandagPane, manRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    } // Får endnu en y_værdi ud fra hvor musen stopper med at være holdt nede.
                                                                // Denne metode er præcis den samme for alle de andre *ugedag*Release events.

    @FXML
    void tuesdayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    }

    @FXML
    void tuesdayRelease(MouseEvent event) throws IOException {
        try {
            y_end = event.getY();
            addStack(tirsdagPane, tirsRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    @FXML
    void wednesdayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    }

    @FXML
    void wednesdayRelease(MouseEvent event) throws IOException {
        try {
            y_end = event.getY();
            addStack(onsdagPane, onsRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    @FXML
    void thursdayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    }

    @FXML
    void thursdayRelease(MouseEvent event) throws IOException {
        try {
            y_end = event.getY();
            addStack(torsdagPane, torsRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    @FXML
    void fridayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    }

    @FXML
    void fridayRelease(MouseEvent event) throws IOException {
        try {
            y_end = event.getY();
            addStack(fredagPane, freRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    @FXML
    void saturdayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    }

    @FXML
    void saturdayRelease(MouseEvent event) throws IOException {
        try {
            y_end = event.getY();
            addStack(lørdagPane, lørRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    @FXML
    void sundayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    }

    @FXML
    void sundayRelease(MouseEvent event) throws IOException {
        try {
            y_end = event.getY();
            addStack(søndagPane, sønRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    public void addStack(Pane p, ArrayList<Rectangle> rect) throws IOException{
        Label l = new Label();

        HashMap<Double, Time> locationMap = new HashMap<>();

        // Sørger for at lde(LocalDate) har den rigtige dag
        if(p == mandagPane) {
           lde = shownDate.with(DayOfWeek.MONDAY);
        } else if(p == tirsdagPane){
            lde = shownDate.with(DayOfWeek.TUESDAY);
        } else if(p == onsdagPane){
            lde = shownDate.with(DayOfWeek.WEDNESDAY);
        } else if(p == torsdagPane){
            lde = shownDate.with(DayOfWeek.THURSDAY);
        } else if(p == fredagPane){
            lde = shownDate.with(DayOfWeek.FRIDAY);
        } else if(p == lørdagPane){
            lde = shownDate.with(DayOfWeek.SATURDAY);
        } else if(p == søndagPane){
            lde = shownDate.with(DayOfWeek.SUNDAY);
        }

        // Et HashMap med tidsværdier og deres korresponderende y-aksis værdier. Bruges til at indsætte bookings på de rigtige pladser
        locationMap.put(0.0,Time.valueOf("07:00:00")); locationMap.put(44.0,Time.valueOf("08:00:00")); locationMap.put(89.0,Time.valueOf("09:00:00"));
        locationMap.put(134.0,Time.valueOf("10:00:00")); locationMap.put(179.0,Time.valueOf("11:00:00")); locationMap.put(224.0,Time.valueOf("12:00:00"));
        locationMap.put(269.0,Time.valueOf("13:00:00")); locationMap.put(314.0,Time.valueOf("14:00:00")); locationMap.put(359.0,Time.valueOf("15:00:00"));
        locationMap.put(404.0,Time.valueOf("16:00:00")); locationMap.put(449.0,Time.valueOf("17:00:00")); locationMap.put(494.0,Time.valueOf("18:00:00"));
        locationMap.put(539.0,Time.valueOf("19:00:00")); locationMap.put(584.0,Time.valueOf("20:00:00")); locationMap.put(629.0,Time.valueOf("21:00:00"));
        locationMap.put(674.0,Time.valueOf("22:00:00")); locationMap.put(719.0,Time.valueOf("23:00:00")); locationMap.put(764.0,Time.valueOf("24:00:00"));

        // Array med de værdier som vi skal bruge mht. at indsætte rektangel på det korrekte sted
        double[] yValues = {0, 44, 89, 134, 179, 224, 269, 314, 359, 404, 449, 494, 539, 584, 629, 674, 719, 764};

        // Find start og slut indekset for den nye rektangel vha. disse ints
        int startIndex = -1;
        int endIndex = -1;

        // For loop der finder start og slut indexet (drag n drop).
        for (int i = 0; i < yValues.length; i++) {
            if (y_start >= yValues[i] && y_start < yValues[i + 1]) {
                startIndex = i;
            }
            if (y_end >= yValues[i] && y_end < yValues[i + 1]) {
                endIndex = i + 1;
                break;
            }
        }

        // Hvis startIndexet ikke er -1 og endIndex ikke er -1, så laver vi en ny rektangel
        // Vi sætter rektanglens Y værdi til at være den tættest nedrundede y_værdi og + 1. F.eks. hvis brugeren begynder at trække
        // Ved 85, så bliver starten sat ved 45.
        // Højden bliver sat ved at trække rektanglens start Y fra dens slut Y.
        if (startIndex != -1 && endIndex != -1) {
            Rectangle r = new Rectangle();
            r.setY(yValues[startIndex] + 1);
            r.setX(0);
            r.setWidth(133);
            r.setHeight(yValues[endIndex] - yValues[startIndex] - 1);
            r.setOpacity(0.3);


            boolean intersects = false;

            // Tjekker om rektangelen overlapper allerede eksisterende renktangler.
            for (Rectangle rec : rect) {
                if (rec.getY() + rec.getHeight() >= r.getY() && rec.getY() <= r.getY() + r.getHeight()) {
                    intersects = true;
                }
            }
            // Hvis den ikke overlapper allerede eksisterende rektangler, så bliver den tilføjet
            // til kalenderen
            if (!intersects) {
                try {
                    opretBooking(lde,locationMap.get(yValues[startIndex]),locationMap.get(yValues[endIndex]));
                } catch (IOException e){
                    System.err.println("Noget gik galt " + e.getMessage());
                }
            }
        }
    } // Tilføjer en rektangel til der hvor brugeren har klikket vha. mouse drag events.

    public void insertSystemBookings(){
        removeVisuals();

        // Liste med bookings
        List<Booking> bookings = bdi.showBooking(shownDate.with(DayOfWeek.MONDAY));

        HashMap<Time, Double> locationMap = new HashMap<>();

        // Et HashMap med tidsværdier og deres korresponderende y-aksis værdier. Bruges til at indsætte bookings på de rigtige pladser
        locationMap.put(Time.valueOf("07:00:00"),0.0); locationMap.put(Time.valueOf("08:00:00"),44.0); locationMap.put(Time.valueOf("09:00:00"),89.0);
        locationMap.put(Time.valueOf("10:00:00"),134.0); locationMap.put(Time.valueOf("11:00:00"),179.0); locationMap.put(Time.valueOf("12:00:00"),224.0);
        locationMap.put(Time.valueOf("13:00:00"),269.0); locationMap.put(Time.valueOf("14:00:00"),314.0); locationMap.put(Time.valueOf("15:00:00"),359.0);
        locationMap.put(Time.valueOf("16:00:00"),404.0); locationMap.put(Time.valueOf("17:00:00"),449.0); locationMap.put(Time.valueOf("18:00:00"),494.0);
        locationMap.put(Time.valueOf("19:00:00"),539.0); locationMap.put(Time.valueOf("20:00:00"),584.0); locationMap.put(Time.valueOf("21:00:00"),629.0);
        locationMap.put(Time.valueOf("22:00:00"),674.0); locationMap.put(Time.valueOf("23:00:00"),719.0); locationMap.put(Time.valueOf("24:00:00"),764.0);

        // Begynder at indsætte rektangler vha et for each
        for(Booking book : bookings){

            Rectangle r = new Rectangle();

            // sætter start og slut lokation ved hjælp af vores HashMap.
            double yStart = locationMap.get(book.getStartTid());
            double yEnd = locationMap.get(book.getSlutTid());

            // Opsæætter formular for hvor at rektanglen skal være i det pane den skal ind i
            r.setY(yStart + 1);
            r.setHeight(yEnd - yStart - 1);
            r.setX(0);
            r.setWidth(133);
            r.setOpacity(0.3);

            // Sætter farven alt efter hvilken booking type det er
            if (Objects.equals(book.getBookingCode(), bKode)){
                r.onMouseClickedProperty().set(mouseEvent -> {
                System.out.println("Clicked on: " + book.getFirstName());
                    try {
                        ændreBooking(book);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                if (book.getBookingType() == 't'){ // hvis den er temporary, sø rød farve
                    r.setFill(Color.RED);
                } else {
                    r.setFill(Color.DODGERBLUE); // ellers blå
                }
            } else {r.setFill(Color.BLACK);}

            // Tilføjer rektangel og book til HashMap der bruges til at tjekke at der ikke er overlap
            rectangleBooking.put(r,book);


            //Sætter rektanglen ind på det pane som passer til datoen
            if (book.getBookingDate().isEqual(shownDate.with(DayOfWeek.MONDAY))){
                manRectangles.add(r);
                mandagPane.getChildren().add(r);
            } else if (book.getBookingDate().isEqual(shownDate.with(DayOfWeek.TUESDAY))){
                tirsRectangles.add(r);
                tirsdagPane.getChildren().add(r);
            } else if (book.getBookingDate().isEqual(shownDate.with(DayOfWeek.WEDNESDAY))){
                onsRectangles.add(r);
                onsdagPane.getChildren().add(r);
            } else if (book.getBookingDate().isEqual(shownDate.with(DayOfWeek.THURSDAY))){
                torsRectangles.add(r);
                torsdagPane.getChildren().add(r);
            } else if (book.getBookingDate().isEqual(shownDate.with(DayOfWeek.FRIDAY))){
                freRectangles.add(r);
                fredagPane.getChildren().add(r);
            } else if (book.getBookingDate().isEqual(shownDate.with(DayOfWeek.SATURDAY))){
                lørRectangles.add(r);
                lørdagPane.getChildren().add(r);
            } else if (book.getBookingDate().isEqual(shownDate.with(DayOfWeek.SUNDAY))){
                sønRectangles.add(r);
                søndagPane.getChildren().add(r);
            }
        }
    } // Indsætter bookings fra database ind i vores kalenderoversigt

    public void removeVisuals(){
        mandagPane.getChildren().removeAll(manRectangles);
        manRectangles.clear();
        tirsdagPane.getChildren().removeAll(tirsRectangles);
        tirsRectangles.clear();
        onsdagPane.getChildren().removeAll(onsRectangles);
        onsRectangles.clear();
        torsdagPane.getChildren().removeAll(torsRectangles);
        torsRectangles.clear();
        fredagPane.getChildren().removeAll(freRectangles);
        freRectangles.clear();
        lørdagPane.getChildren().removeAll(lørRectangles);
        lørRectangles.clear();
        søndagPane.getChildren().removeAll(sønRectangles);
        sønRectangles.clear();
        mandagPane.getChildren().removeAll(labels);
        tirsdagPane.getChildren().removeAll(labels);
        onsdagPane.getChildren().removeAll(labels);
        torsdagPane.getChildren().removeAll(labels);
        fredagPane.getChildren().removeAll(labels);
        lørdagPane.getChildren().removeAll(labels);
        søndagPane.getChildren().removeAll(labels);
        labels.clear();
    } // Fjerner alt det visuelle. Dvs. rektangler og labels.

    BookingDAO bdi = new BookingDAOImpl();
}