package com.weather.controller;

import com.weather.model.WeatherData;
import com.weather.service.WeatherService;
import com.weather.service.WeatherService.CityResult;
import com.weather.util.WeatherUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;

import java.util.List;

public class WeatherController {

    private final WeatherService weatherService = new WeatherService();
    private BorderPane root;

    // Search components
    private TextField searchField;
    private Button searchButton;
    private ListView<CityResult> suggestionList;
    private VBox suggestionPopup;

    // Content areas
    private StackPane contentArea;
    private VBox mainContent;
    private VBox errorPane;
    private VBox welcomePane;
    private ProgressIndicator loader;

    // Hero card labels
    private Label cityLabel, countryLabel, temperatureLabel, emojiLabel,
                  descriptionLabel, feelsLikeLabel, tempRangeLabel;

    // Stat labels
    private Label humidityLabel, windLabel, pressureLabel,
                  visibilityLabel, sunriseLabel, sunsetLabel, cloudinessLabel;

    // Forecast
    private HBox forecastBox;

    // Error
    private Label errorLabel;

    // State
    private String currentWeatherClass = "weather-default";

    public WeatherController() { buildUI(); }
    public BorderPane getRoot() { return root; }

    // ── Build UI ──────────────────────────────────────────────────
    private void buildUI() {
        root = new BorderPane();
        root.getStyleClass().addAll("root-pane", "weather-default");
        root.setTop(buildSearchBar());

        contentArea = new StackPane();
        welcomePane = buildWelcomePane();
        mainContent = buildMainContent();
        mainContent.setVisible(false);
        errorPane   = buildErrorPane();
        errorPane.setVisible(false);
        loader = new ProgressIndicator();
        loader.getStyleClass().add("loader");
        loader.setMaxSize(60, 60);
        loader.setVisible(false);

        contentArea.getChildren().addAll(welcomePane, mainContent, errorPane, loader);
        root.setCenter(contentArea);
        root.setBottom(buildFooter());
    }

    // ── Search Bar ────────────────────────────────────────────────
    private VBox buildSearchBar() {
        HBox bar = new HBox(12);
        bar.getStyleClass().add("search-bar");
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(16, 28, 16, 28));

        Label appTitle = new Label("⛅ WeatherNow");
        appTitle.getStyleClass().add("app-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        searchField = new TextField();
        searchField.setPromptText("Type a city name...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(300);

        searchButton = new Button("Search");
        searchButton.getStyleClass().add("search-btn");
        searchButton.setOnAction(e -> triggerSearch());

        bar.getChildren().addAll(appTitle, spacer, searchField, searchButton);

        // ── Autocomplete dropdown ──────────────────────────────
        suggestionList = new ListView<>();
        suggestionList.getStyleClass().add("suggestion-list");
        suggestionList.setMaxHeight(220);
        suggestionList.setPrefWidth(300);
        suggestionList.setOnMouseClicked(e -> selectSuggestion());
        suggestionList.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) selectSuggestion();
        });

        suggestionPopup = new VBox(suggestionList);
        suggestionPopup.getStyleClass().add("suggestion-popup");
        suggestionPopup.setVisible(false);
        suggestionPopup.setMaxWidth(300);

        // Type-ahead listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() >= 2) {
                fetchCitySuggestions(newVal);
            } else {
                hideSuggestions();
            }
        });

        // Enter key on field: pick first suggestion or direct search
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                if (!suggestionList.getItems().isEmpty()) {
                    suggestionList.getSelectionModel().selectFirst();
                    selectSuggestion();
                } else {
                    triggerSearch();
                }
            } else if (e.getCode() == KeyCode.DOWN) {
                suggestionList.requestFocus();
                suggestionList.getSelectionModel().selectFirst();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                hideSuggestions();
            }
        });

        // Layout: bar + popup stacked
        VBox wrapper = new VBox(bar, suggestionPopup);
        wrapper.setAlignment(Pos.TOP_RIGHT);

        // Align popup under search field
        suggestionPopup.setAlignment(Pos.TOP_RIGHT);
        VBox.setMargin(suggestionPopup, new Insets(0, 28, 0, 0));

        return wrapper;
    }

    private void fetchCitySuggestions(String query) {
        Task<List<CityResult>> task = new Task<>() {
            @Override protected List<CityResult> call() throws Exception {
                return weatherService.searchCities(query);
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> {
            List<CityResult> results = task.getValue();
            if (results.isEmpty()) {
                hideSuggestions();
            } else {
                suggestionList.getItems().setAll(results);
                suggestionPopup.setVisible(true);
            }
        }));
        task.setOnFailed(e -> Platform.runLater(this::hideSuggestions));
        new Thread(task, "city-suggest").start();
    }

    private void selectSuggestion() {
        CityResult selected = suggestionList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        hideSuggestions();
        searchField.setText(selected.toString());
        fetchWeatherByCoords(selected.lat, selected.lon, selected.toString());
    }

    private void hideSuggestions() {
        suggestionPopup.setVisible(false);
        suggestionList.getItems().clear();
    }

    private void triggerSearch() {
        String city = searchField.getText().trim();
        if (city.isEmpty()) {
            searchField.getStyleClass().add("field-error");
            return;
        }
        searchField.getStyleClass().remove("field-error");
        hideSuggestions();
        fetchWeather(city);
    }

    // ── Welcome ───────────────────────────────────────────────────
    private VBox buildWelcomePane() {
        VBox pane = new VBox(16);
        pane.setAlignment(Pos.CENTER);
        Label icon  = new Label("🌍");  icon.getStyleClass().add("welcome-icon");
        Label title = new Label("Search any city"); title.getStyleClass().add("welcome-title");
        Label sub   = new Label("Type a city name above to get real-time weather and 5-day forecast");
        sub.getStyleClass().add("welcome-sub");
        sub.setWrapText(true); sub.setMaxWidth(400);
        pane.getChildren().addAll(icon, title, sub);
        return pane;
    }

    // ── Main Content ──────────────────────────────────────────────
    private VBox buildMainContent() {
        VBox vbox = new VBox(20);
        vbox.getStyleClass().add("main-content");
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setPadding(new Insets(24, 32, 24, 32));
        vbox.getChildren().addAll(buildHeroCard(), buildStatsGrid(), buildForecastSection());
        return vbox;
    }

    private VBox buildHeroCard() {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("card", "hero-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26, 24, 22, 24));

        HBox locationRow = new HBox(8);
        locationRow.setAlignment(Pos.CENTER);
        cityLabel    = new Label("—"); cityLabel.getStyleClass().add("city-label");
        countryLabel = new Label(""); countryLabel.getStyleClass().add("country-label");
        locationRow.getChildren().addAll(cityLabel, countryLabel);

        HBox tempRow = new HBox(16);
        tempRow.setAlignment(Pos.CENTER);
        emojiLabel       = new Label("☀️"); emojiLabel.getStyleClass().add("weather-emoji");
        temperatureLabel = new Label("—°"); temperatureLabel.getStyleClass().add("temperature-label");
        tempRow.getChildren().addAll(emojiLabel, temperatureLabel);

        descriptionLabel = new Label("—"); descriptionLabel.getStyleClass().add("description-label");

        HBox feelsRow = new HBox(24);
        feelsRow.setAlignment(Pos.CENTER);
        feelsLikeLabel = new Label("Feels like —°"); feelsLikeLabel.getStyleClass().add("feels-label");
        tempRangeLabel = new Label("H: —°  L: —°"); tempRangeLabel.getStyleClass().add("feels-label");
        feelsRow.getChildren().addAll(feelsLikeLabel, tempRangeLabel);

        card.getChildren().addAll(locationRow, tempRow, descriptionLabel, feelsRow);
        return card;
    }

    private GridPane buildStatsGrid() {
        humidityLabel   = new Label("—");
        windLabel       = new Label("—");
        pressureLabel   = new Label("—");
        visibilityLabel = new Label("—");
        sunriseLabel    = new Label("—");
        sunsetLabel     = new Label("—");
        cloudinessLabel = new Label("—");

        GridPane grid = new GridPane();
        grid.getStyleClass().add("stats-grid");
        grid.setHgap(12); grid.setVgap(12);
        grid.add(buildStatCard("💧","Humidity",   humidityLabel),  0, 0);
        grid.add(buildStatCard("💨","Wind",        windLabel),       1, 0);
        grid.add(buildStatCard("🔵","Pressure",    pressureLabel),   2, 0);
        grid.add(buildStatCard("👁","Visibility",  visibilityLabel), 3, 0);
        grid.add(buildStatCard("🌅","Sunrise",     sunriseLabel),    0, 1);
        grid.add(buildStatCard("🌇","Sunset",      sunsetLabel),     1, 1);
        grid.add(buildStatCard("☁️","Cloudiness",  cloudinessLabel), 2, 1);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(25);
            grid.getColumnConstraints().add(cc);
        }
        return grid;
    }

    private VBox buildStatCard(String icon, String title, Label valueLabel) {
        VBox card = new VBox(4);
        card.getStyleClass().addAll("card", "stat-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(14, 10, 14, 10));
        Label iconLbl  = new Label(icon);  iconLbl.getStyleClass().add("stat-icon");
        Label titleLbl = new Label(title); titleLbl.getStyleClass().add("stat-title");
        valueLabel.getStyleClass().add("stat-value");
        card.getChildren().addAll(iconLbl, titleLbl, valueLabel);
        return card;
    }

    private VBox buildForecastSection() {
        VBox section = new VBox(10);
        Label header = new Label("5-Day Forecast");
        header.getStyleClass().add("section-header");
        forecastBox = new HBox(10);
        forecastBox.setAlignment(Pos.CENTER_LEFT);
        section.getChildren().addAll(header, forecastBox);
        return section;
    }

    // ── Error Pane ────────────────────────────────────────────────
    private VBox buildErrorPane() {
        VBox pane = new VBox(16);
        pane.setAlignment(Pos.CENTER);
        Label icon = new Label("🌩"); icon.getStyleClass().add("welcome-icon");
        errorLabel = new Label("Something went wrong.");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(460);
        Button retry = new Button("Try Again");
        retry.getStyleClass().add("search-btn");
        retry.setOnAction(e -> { errorPane.setVisible(false); welcomePane.setVisible(true); searchField.requestFocus(); });
        pane.getChildren().addAll(icon, errorLabel, retry);
        return pane;
    }

    private HBox buildFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(5, 16, 5, 16));
        Label credit = new Label("Powered by OpenWeatherMap API");
        credit.getStyleClass().add("footer-label");
        footer.getChildren().add(credit);
        return footer;
    }

    // ── Fetch ─────────────────────────────────────────────────────
    private void fetchWeather(String city) {
        setLoading(true);
        Task<WeatherData> task = new Task<>() {
            @Override protected WeatherData call() throws Exception {
                return weatherService.fetchWeather(city);
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> { setLoading(false); displayWeather(task.getValue()); }));
        task.setOnFailed(e -> Platform.runLater(() -> { setLoading(false); showError(task.getException().getMessage()); }));
        new Thread(task, "weather-fetch").start();
    }

    private void fetchWeatherByCoords(double lat, double lon, String label) {
        setLoading(true);
        Task<WeatherData> task = new Task<>() {
            @Override protected WeatherData call() throws Exception {
                return weatherService.fetchWeatherByCoords(lat, lon);
            }
        };
        task.setOnSucceeded(e -> Platform.runLater(() -> { setLoading(false); displayWeather(task.getValue()); }));
        task.setOnFailed(e -> Platform.runLater(() -> { setLoading(false); showError(task.getException().getMessage()); }));
        new Thread(task, "weather-coords").start();
    }

    private void setLoading(boolean loading) {
        loader.setVisible(loading);
        searchButton.setDisable(loading);
        searchField.setDisable(loading);
        if (loading) {
            welcomePane.setVisible(false);
            mainContent.setVisible(false);
            errorPane.setVisible(false);
        }
    }

    // ── Display ───────────────────────────────────────────────────
    private void displayWeather(WeatherData data) {
        cityLabel.setText(data.getCityName());
        countryLabel.setText("🏳 " + data.getCountry());
        temperatureLabel.setText(WeatherUtil.roundTemp(data.getTemperature()) + "°C");
        emojiLabel.setText(WeatherUtil.getWeatherEmoji(data.getIconCode()));
        String desc = data.getDescription();
        descriptionLabel.setText(Character.toUpperCase(desc.charAt(0)) + desc.substring(1));
        feelsLikeLabel.setText("Feels like " + WeatherUtil.roundTemp(data.getFeelsLike()) + "°C");
        tempRangeLabel.setText("H: " + WeatherUtil.roundTemp(data.getTempMax()) + "°  L: " + WeatherUtil.roundTemp(data.getTempMin()) + "°");

        humidityLabel.setText(data.getHumidity() + "%");
        windLabel.setText(String.format("%.1f m/s %s", data.getWindSpeed(), WeatherUtil.getWindDirection(data.getWindDegree())));
        pressureLabel.setText(data.getPressure() + " hPa");
        visibilityLabel.setText(WeatherUtil.formatVisibility(data.getVisibility()));
        sunriseLabel.setText(WeatherUtil.formatTime(data.getSunrise(), data.getTimezone()));
        sunsetLabel.setText(WeatherUtil.formatTime(data.getSunset(), data.getTimezone()));
        cloudinessLabel.setText(data.getCloudiness() + "%");

        updateTheme(data.getMainCondition());
        updateForecast(data.getForecast(), data.getTimezone());

        welcomePane.setVisible(false);
        errorPane.setVisible(false);
        mainContent.setVisible(true);
    }

    private void updateTheme(String condition) {
        root.getStyleClass().remove(currentWeatherClass);
        currentWeatherClass = WeatherUtil.getWeatherStyleClass(condition);
        root.getStyleClass().add(currentWeatherClass);
    }

    private void updateForecast(List<WeatherData.ForecastItem> forecast, long timezone) {
        forecastBox.getChildren().clear();
        if (forecast == null) return;
        for (WeatherData.ForecastItem item : forecast) {
            VBox card = new VBox(5);
            card.getStyleClass().addAll("card", "forecast-card");
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(14, 16, 14, 16));

            Label day   = new Label(WeatherUtil.formatDay(item.getTimestamp(), timezone));   day.getStyleClass().add("forecast-day");
            Label date  = new Label(WeatherUtil.formatDate(item.getTimestamp(), timezone));  date.getStyleClass().add("forecast-date");
            Label emoji = new Label(WeatherUtil.getWeatherEmoji(item.getIconCode()));         emoji.getStyleClass().add("forecast-emoji");
            Label temp  = new Label(WeatherUtil.roundTemp(item.getTemperature()) + "°C");    temp.getStyleClass().add("forecast-temp");
            String d    = item.getDescription();
            Label desc  = new Label(Character.toUpperCase(d.charAt(0)) + d.substring(1));   desc.getStyleClass().add("forecast-desc"); desc.setWrapText(true); desc.setMaxWidth(110);
            Label range = new Label("H " + WeatherUtil.roundTemp(item.getTempMax()) + "° / L " + WeatherUtil.roundTemp(item.getTempMin()) + "°"); range.getStyleClass().add("forecast-range");

            card.getChildren().addAll(day, date, emoji, temp, desc, range);
            HBox.setHgrow(card, Priority.ALWAYS);
            forecastBox.getChildren().add(card);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message != null ? message : "An unexpected error occurred.");
        welcomePane.setVisible(false);
        mainContent.setVisible(false);
        errorPane.setVisible(true);
    }
}
