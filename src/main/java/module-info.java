module com.example.weatherapplication {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.weatherapplication to javafx.fxml;
    exports com.example.weatherapplication;
}