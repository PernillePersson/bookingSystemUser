package com.example.bookingsystemuser;

import com.example.bookingsystemuser.model.objects.Sceneviewer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

public class UserApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        var scene = new Scene(new Pane(), 1200, 705);

        SceneviewerSwitch.setScene(scene);
        SceneviewerSwitch.switchTo(Sceneviewer.FRONT);
        stage.setTitle("Booking frontpage");
        stage.getIcons().add(new Image(UserApplication.class.getResourceAsStream("logo.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}