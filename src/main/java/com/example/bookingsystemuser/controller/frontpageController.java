package com.example.bookingsystemuser.controller;

import com.example.bookingsystemuser.model.objects.Sceneviewer;
import com.example.bookingsystemuser.SceneviewerSwitch;
import javafx.event.ActionEvent;
import javafx.scene.Scene;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class frontpageController {

    public void switchToOversigtScene(ActionEvent actionEvent) throws IOException {
        SceneviewerSwitch.switchTo(Sceneviewer.OVERSIGT);
    }

    public void åbenHjemmeside() throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://mindfactorybyecco.dk/"));
    }

}