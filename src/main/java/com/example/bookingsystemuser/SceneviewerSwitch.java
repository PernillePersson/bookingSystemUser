package com.example.bookingsystemuser;

import com.example.bookingsystemuser.model.objects.Sceneviewer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class SceneviewerSwitch {

    private static Scene scene;

    public static void setScene(Scene scene) {
        SceneviewerSwitch.scene = scene;
    }
    public static void switchTo(Sceneviewer sceneViewer) throws IOException {

        Parent root = FXMLLoader.load(SceneviewerSwitch.class.getResource(sceneViewer.getFileName()));
        scene.setRoot(root);
    }
}
