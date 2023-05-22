package com.example.bookingsystemuser.model.objects;

public enum Sceneviewer {
    FRONT("frontpage.fxml"),
    OVERSIGT("weekView.fxml");

    private String fileName;

    Sceneviewer(String fileName){this.fileName = fileName;}

    public String getFileName(){return fileName;}


}
