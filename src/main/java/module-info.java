module com.example.bookingsystemuser {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.mail;
    requires java.desktop;


    opens com.example.bookingsystemuser to javafx.fxml;
    exports com.example.bookingsystemuser;
}