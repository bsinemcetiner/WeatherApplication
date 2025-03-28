package com.example.weatherapplication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WeatherStationApp extends Application {
    private final TextArea textArea = new TextArea();
    private final List<String> collectedData = new ArrayList<>();
    private final List<SensorThread> sensorThreads = new ArrayList<>();

    String userHome = System.getProperty("user.home");
    private final String[] filenames = {
            userHome + "/Downloads/num01.txt",
            userHome + "/Downloads/num02.txt",
            userHome + "/Downloads/num03.txt",
            userHome + "/Downloads/num04.txt"
    };


    @Override
    public void start(Stage primaryStage) {
        Button addSensorBtn = new Button("Add Sensor");
        Button stopSensorsBtn = new Button("Stop Sensors");
        Button writeFileBtn = new Button("Write Weather File");
        writeFileBtn.setDisable(true);

        addSensorBtn.setOnAction(e -> {
            if (sensorThreads.isEmpty()) {
                for (int i = 0; i < filenames.length; i++) {
                    SensorThread sensor = new SensorThread(filenames[i], i + 1, textArea, collectedData);
                    sensorThreads.add(sensor);
                    sensor.start();
                }
            }
        });

        stopSensorsBtn.setOnAction(e -> {
            for (SensorThread sensor : sensorThreads) {
                sensor.terminate();
            }
            sensorThreads.clear();
            writeFileBtn.setDisable(false);
        });

        writeFileBtn.setOnAction(e -> {
            try (PrintWriter writer = new PrintWriter("weather.txt")) {
                for (String line : collectedData) {
                    writer.println(line);
                }
                Platform.runLater(() -> textArea.appendText("\nData written to weather.txt\n"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        VBox root = new VBox(10, addSensorBtn, stopSensorsBtn, writeFileBtn, textArea);
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Weather Station Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

