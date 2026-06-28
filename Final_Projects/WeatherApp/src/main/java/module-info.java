module com.weather {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires okhttp3;
    requires kotlin.stdlib;

    exports com.weather;
    exports com.weather.controller;
    exports com.weather.model;
    exports com.weather.service;
    exports com.weather.util;
}
