module com.example.bookingsystemuser {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.mail;


    opens com.example.bookingsystemuser to javafx.fxml;
    exports com.example.bookingsystemuser;
}