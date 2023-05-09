package com.example.bookingsystemuser;

import com.example.bookingsystemuser.Gmail.GEmail;
import com.example.bookingsystemuser.model.Booking;
import com.example.bookingsystemuser.model.BookingDAO;
import com.example.bookingsystemuser.model.BookingDAOImpl;
import com.example.bookingsystemuser.model.SimpleThread;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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

    GEmail ge = new GEmail();

    public UserController() throws SQLException {
        
        simpleThread = new SimpleThread(this);
    }

    public void initialize(){
        shownDate = LocalDate.now();
        today = LocalDate.now();
        opsætDato();
        insertSystemBookings();
        simpleThread.start();

        // sendNotificationEmails(); // Aktiver den her når vi får sat en værdi på db der tjekker om der er blevet
        // sendt en notifikation før.
    } // Vores initialize der kører alle de metoder der skal køres fra starten af. En thread der kører hvert sekund
    // og opdaterer vores view

    @FXML
    void opretBookingKnap(ActionEvent event) throws IOException {
        //Skift scene til bookingformular
        opretBooking();
    }

    public void opretBooking() throws IOException {
        //åben formular med alt booking info, og knap der opdaterer
        FXMLLoader fxmlLoader = new FXMLLoader(UserApplication.class.getResource("bookingFormular.fxml"));
        Scene oversigtScene = new Scene(fxmlLoader.load());
        OpretFormularController formController = fxmlLoader.getController();
        Stage oversigtStage = new Stage();
        oversigtStage.setScene(oversigtScene);
        formController.opsæt();

        // Hvis der klikkes udenfor vinduet, lukkes det
        oversigtStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                oversigtStage.hide();
                insertSystemBookings();
            }
        });
        oversigtStage.show();
    }

    @FXML
    void findBooking(ActionEvent event) throws IOException {
        bKode = bkTekst.getText();
        Booking b = bdi.getMyBooking(bKode);
        ændreBooking(b);
    }

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
        } catch (NullPointerException n){
            System.err.println("Ingen booking fundet med denne bookingkode ");
        }
    }

    public void sendMail(Booking b){
        //Åben tekstfelt der skal sendes som mail
        System.out.println("Sender mail til " + b.getFirstName());
        String to = b.getEmail();
        String from = "noreplybookingsystemem@gmail.com";
        String subject; // getText fra eventuel subject textfield eller lign
        String text; // getText fra textField eller lign.
    }

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
    }

    @FXML
    void mondayRelease(MouseEvent event) {
        try {
            y_end = event.getY();
            addStack(mandagPane, manRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    @FXML
    void tuesdayPress(MouseEvent event) {
        event.setDragDetect(true);
        y_start = event.getY();
    }

    @FXML
    void tuesdayRelease(MouseEvent event) {
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
    void wednesdayRelease(MouseEvent event) {
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
    void thursdayRelease(MouseEvent event) {
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
    void fridayRelease(MouseEvent event) {
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
    void saturdayRelease(MouseEvent event) {
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
    void sundayRelease(MouseEvent event) {
        try {
            y_end = event.getY();
            addStack(søndagPane, sønRectangles);
        }catch (IndexOutOfBoundsException e){
            System.err.println("Kan ikke oprette booking udenfor kalenderen");
        }
    }

    public void addStack(Pane p, ArrayList<Rectangle> rect){
        Label l = new Label();

        // Array med de værdier som vi skal bruge mht. at indsætte rektangel på det korrekte sted
        double[] yValues = {0, 44, 89, 134, 179, 224, 269, 314, 359, 404, 449, 494, 539, 584, 629, 674, 719, 764};

        // Find start og slut indekset for den nye rektangel

        int startIndex = -1;
        int endIndex = -1;

        // For loop der finder start og slut index.
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
        if (startIndex != -1 && endIndex != -1) {
            Rectangle r = new Rectangle();
            Booking book = new Booking(3,"Mognus","Hansen","EASV","madmedmig@gmail.com",1234,'t','y', LocalDate.of(2023,04,03),LocalDate.of(2023,03,30),"131231",Time.valueOf("10:00:00"),Time.valueOf("15:00:00"), 10);

            r.setY(yValues[startIndex] + 1);
            r.setX(0);
            r.setWidth(133);
            r.setHeight(yValues[endIndex] - yValues[startIndex] - 1);
            r.setOpacity(0.3);

            l.setLayoutY(yValues[endIndex] - r.getHeight() / 2);
            l.setText(book.toString());

            r.layoutYProperty().addListener((obs,oldVal,newVal) -> {
                l.setLayoutY(newVal.doubleValue() / 2);
            });

            boolean intersects = false;

            // Tjekker om rektangelen overlapper allerede eksisterende renktangler.
            for (Rectangle rec : rect) {
                if (rec.getY() + rec.getHeight() >= r.getY() && rec.getY() <= r.getY() + r.getHeight()) {
                    intersects = true;
                    break;
                }
            }
            // Hvis den ikke overlapper allerede eksisterende rektangler, så bliver den tilføjet
            // til kalenderen
            if (!intersects) {
                // indsæt en tilføjelse af booking her eller i vores MandagPane release event
                rectangleBooking.put(r,book);
                rect.add(r);
                labels.add(l);
                p.getChildren().add(r);
                p.getChildren().add(l);
            }

            // MouseEvent der gør, at når man klikker på en rektangel, så vil der komme et nyt vindue op
            // med kontakt info i forhold til personen der har lavet bookingen.
            r.onMouseClickedProperty().set(mouseEvent -> {
                //Booking bk = bookings.get(book); // Får information om lige præcis den Booking der hører til objektet
                System.out.println("Clicked on: " + book.getFirstName());
            });
        }
    } // Tilføjer en rektangel til der hvor brugeren har klikket vha. mouse drag events.

    public void insertSystemBookings(){
        removeVisuals();

        List<Booking> bookings = bdi.showBooking(shownDate.with(DayOfWeek.MONDAY));

        HashMap<Time, Double> locationMap = new HashMap<>();

        // Et HashMap med tidsværdier og deres korresponderende y-aksis værdier. Bruges til at indsætte bookings på de rigtige pladser
        locationMap.put(Time.valueOf("07:00:00"),0.0); locationMap.put(Time.valueOf("08:00:00"),44.0); locationMap.put(Time.valueOf("09:00:00"),89.0);
        locationMap.put(Time.valueOf("10:00:00"),134.0); locationMap.put(Time.valueOf("11:00:00"),179.0); locationMap.put(Time.valueOf("12:00:00"),224.0);
        locationMap.put(Time.valueOf("13:00:00"),269.0); locationMap.put(Time.valueOf("14:00:00"),314.0); locationMap.put(Time.valueOf("15:00:00"),359.0);
        locationMap.put(Time.valueOf("16:00:00"),404.0); locationMap.put(Time.valueOf("17:00:00"),449.0); locationMap.put(Time.valueOf("18:00:00"),494.0);
        locationMap.put(Time.valueOf("19:00:00"),539.0); locationMap.put(Time.valueOf("20:00:00"),584.0); locationMap.put(Time.valueOf("21:00:00"),629.0);
        locationMap.put(Time.valueOf("22:00:00"),674.0); locationMap.put(Time.valueOf("23:00:00"),719.0); locationMap.put(Time.valueOf("24:00:00"),764.0);

        // Begynder at indsætte rektangler
        for(Booking book : bookings){

            Rectangle r = new Rectangle();

            double yStart = locationMap.get(book.getStartTid());
            double yEnd = locationMap.get(book.getSlutTid());

            // Opsæætter formular for hvor at rektanglen skal være i det pane den skal ind i
            r.setY(yStart + 1);
            r.setHeight(yEnd - yStart - 1);
            r.setX(0);
            r.setWidth(133);
            r.setOpacity(0.3);

            // Sætter farven alt efter booking type
            if (Objects.equals(book.getBookingCode(), bKode)){
                r.onMouseClickedProperty().set(mouseEvent -> {
                System.out.println("Clicked on: " + book.getFirstName());
                    try {
                        ændreBooking(book);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                if (book.getBookingType() == 't'){
                    r.setFill(Color.RED);
                } else {
                    r.setFill(Color.DODGERBLUE);
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
    } //Indsætter bookings fra database i kalenderoversigt

    public void removeVisuals(){
        mandagPane.getChildren().removeAll(manRectangles);
        tirsdagPane.getChildren().removeAll(tirsRectangles);
        onsdagPane.getChildren().removeAll(onsRectangles);
        torsdagPane.getChildren().removeAll(torsRectangles);
        fredagPane.getChildren().removeAll(freRectangles);
        lørdagPane.getChildren().removeAll(lørRectangles);
        søndagPane.getChildren().removeAll(sønRectangles);
        mandagPane.getChildren().removeAll(labels);
        tirsdagPane.getChildren().removeAll(labels);
        onsdagPane.getChildren().removeAll(labels);
        torsdagPane.getChildren().removeAll(labels);
        fredagPane.getChildren().removeAll(labels);
        lørdagPane.getChildren().removeAll(labels);
        søndagPane.getChildren().removeAll(labels);
    } // Fjerner alt det visuelle. Dvs. rektangler og labels.

    public void sendNotificationEmails(){

        // Laver en liste med de Bookings der passer til det vi efterlyser
        List<Booking> sendEmails = bdi.sendEmailNotification();

        // Laver en ny GEmail
        GEmail gmailSender = new GEmail();

        // For hver booking der opfylder vores betingelser, sender vi en mail til den person
        for(Booking book : sendEmails){

            String to = book.getEmail();
            String from = "noreplybookingsystemem@gmail.com";
            String subject = "Booking påmindelse";
            String text = "Hej" + book.getFirstName() + " " + book.getLastName() + "\n "
                    + "Dette er en påmindelse om at du har en booking til den " + book.getBookingDate() + "i tidsrummet mellem "
                    + book.getStartTid() + " til " + book.getSlutTid() + ". Glæder os til at se jer.";

            // Sender emailen med de ting som vi gerne vil have den til at tage med
            gmailSender.sendEmail(to,from,subject,text);
        }
    } // Sender mails med påmindelse om at de har en booking 1 uge inden.
    BookingDAO bdi = new BookingDAOImpl();
}