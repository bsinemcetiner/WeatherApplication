package com.example.weatherapplication;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.*;
import java.util.List;

public class SensorThread extends Thread {
    private final String filename;
    private final int sensorType; // 1: Temp, 2: Humidity, 3: Pressure, 4: Wind
    private final TextArea textArea;
    private final List<String> sharedList;
    private volatile boolean running = true;

    public SensorThread(String filename, int sensorType, TextArea textArea, List<String> sharedList) {
        this.filename = filename;
        this.sensorType = sensorType;
        this.textArea = textArea;
        this.sharedList = sharedList;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while (running && (line = reader.readLine()) != null) {
                try {
                    int raw = Integer.parseInt(line.trim());
                    String measurement = convertRawToMeasurement(raw);
                    String output = getSensorName() + ": " + measurement;

                    Platform.runLater(() -> textArea.appendText(output + "\n"));
                    synchronized (sharedList) {
                        sharedList.add(output);
                    }
                    Thread.sleep(2000);
                } catch (NumberFormatException | InterruptedException e) {
                    break;
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> textArea.appendText("Error reading file: " + filename + "\n"));
        }
    }

    public void terminate() {
        running = false;
        this.interrupt();
    }

    private String convertRawToMeasurement(int raw) {
        return switch (sensorType) {
            case 1 -> String.format("%.2f °C", raw / 100.0);
            case 2 -> String.format("%.1f %% Humidity", raw / 10.0 + 30);
            case 3 -> String.format("%.2f hPa", raw / 5.5);
            case 4 -> String.format("%.2f km/h", raw / 100.0);
            default -> "Unknown";
        };
    }

    private String getSensorName() {
        return switch (sensorType) {
            case 1 -> "Temperature";
            case 2 -> "Humidity";
            case 3 -> "Pressure";
            case 4 -> "Wind Speed";
            default -> "Unknown Sensor";
        };
    }
}

