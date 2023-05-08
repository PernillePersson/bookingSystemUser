package com.example.bookingsystemuser.model;


import com.example.bookingsystemuser.UserController;
import javafx.application.Platform;

import java.sql.SQLException;

public class SimpleThread extends Thread {
    private final UserController controller;
    private boolean isRunning;

    public SimpleThread(UserController controller) {
        this.controller = controller;
        isRunning = true;
    }

    public void run() {
        while (isRunning) {
            Platform.runLater(() -> {
                controller.insertSystemBookings();
            });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        isRunning = false;
    }
}
