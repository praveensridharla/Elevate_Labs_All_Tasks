package com.weather;

import com.weather.controller.WeatherController;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WeatherApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        WeatherController controller = new WeatherController();
        Scene scene = new Scene(controller.getRoot(), 900, 680);

        // Load CSS
        String css = getClass().getResource("/com/weather/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Weather Forecast");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(550);

        // Center on screen
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX((screenBounds.getWidth() - 900) / 2);
        primaryStage.setY((screenBounds.getHeight() - 680) / 2);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
