package com.weather.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.weather.model.WeatherData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WeatherService {

    private static final String API_KEY = "7f7aeec746fd2535f2a65fec2721bbdf";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5";
    private static final String GEO_URL  = "https://api.openweathermap.org/geo/1.0";
    private static final String UNITS    = "metric";

    private final OkHttpClient httpClient;

    public WeatherService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    /** Autocomplete: returns up to 5 city suggestions for a partial name */
    public List<CityResult> searchCities(String query) throws WeatherException {
        if (query == null || query.trim().length() < 2) return List.of();
        String url = String.format("%s/direct?q=%s&limit=5&appid=%s",
                GEO_URL, encodeCity(query), API_KEY);
        String json = makeRequest(url);
        return parseCities(json);
    }

    /** Fetch weather by city name */
    public WeatherData fetchWeather(String cityName) throws WeatherException {
        String currentUrl = String.format(
                "%s/weather?q=%s&appid=%s&units=%s",
                BASE_URL, encodeCity(cityName), API_KEY, UNITS);
        WeatherData data = parseCurrentWeather(makeRequest(currentUrl));

        String forecastUrl = String.format(
                "%s/forecast?q=%s&appid=%s&units=%s&cnt=40",
                BASE_URL, encodeCity(cityName), API_KEY, UNITS);
        data.setForecast(parseForecast(makeRequest(forecastUrl)));
        return data;
    }

    /** Fetch weather by lat/lon (used after picking a city from autocomplete) */
    public WeatherData fetchWeatherByCoords(double lat, double lon) throws WeatherException {
        String currentUrl = String.format(
                "%s/weather?lat=%.4f&lon=%.4f&appid=%s&units=%s",
                BASE_URL, lat, lon, API_KEY, UNITS);
        WeatherData data = parseCurrentWeather(makeRequest(currentUrl));

        String forecastUrl = String.format(
                "%s/forecast?lat=%.4f&lon=%.4f&appid=%s&units=%s&cnt=40",
                BASE_URL, lat, lon, API_KEY, UNITS);
        data.setForecast(parseForecast(makeRequest(forecastUrl)));
        return data;
    }

    // ── HTTP ──────────────────────────────────────────────────────
    private String makeRequest(String url) throws WeatherException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = httpClient.newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            switch (response.code()) {
                case 401 -> throw new WeatherException(
                    "API key not yet active.\n\nNew OpenWeatherMap keys take up to 2 hours to activate.\nPlease wait and try again.");
                case 404 -> throw new WeatherException(
                    "City not found. Please check the spelling and try again.");
                case 429 -> throw new WeatherException(
                    "Too many requests. Please wait a moment and try again.");
            }
            if (!response.isSuccessful())
                throw new WeatherException("Server error (" + response.code() + "). Please try again.");
            return body;
        } catch (IOException e) {
            throw new WeatherException("Network error: " + e.getMessage()
                    + "\n\nPlease check your internet connection.");
        }
    }

    // ── Parsers ───────────────────────────────────────────────────
    private List<CityResult> parseCities(String json) throws WeatherException {
        try {
            JsonArray arr = JsonParser.parseString(json).getAsJsonArray();
            List<CityResult> results = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                JsonObject o = arr.get(i).getAsJsonObject();
                String name    = o.get("name").getAsString();
                String country = o.get("country").getAsString();
                String state   = o.has("state") ? o.get("state").getAsString() : "";
                double lat     = o.get("lat").getAsDouble();
                double lon     = o.get("lon").getAsDouble();
                results.add(new CityResult(name, state, country, lat, lon));
            }
            return results;
        } catch (Exception e) {
            throw new WeatherException("Failed to parse city list: " + e.getMessage());
        }
    }

    private WeatherData parseCurrentWeather(String json) throws WeatherException {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            WeatherData data = new WeatherData();

            data.setCityName(root.get("name").getAsString());
            data.setTimezone(root.get("timezone").getAsLong());

            JsonObject sys = root.getAsJsonObject("sys");
            data.setCountry(sys.get("country").getAsString());
            data.setSunrise(sys.get("sunrise").getAsLong());
            data.setSunset(sys.get("sunset").getAsLong());

            JsonObject main = root.getAsJsonObject("main");
            data.setTemperature(main.get("temp").getAsDouble());
            data.setFeelsLike(main.get("feels_like").getAsDouble());
            data.setTempMin(main.get("temp_min").getAsDouble());
            data.setTempMax(main.get("temp_max").getAsDouble());
            data.setHumidity(main.get("humidity").getAsInt());
            data.setPressure(main.get("pressure").getAsInt());

            JsonObject wind = root.getAsJsonObject("wind");
            data.setWindSpeed(wind.get("speed").getAsDouble());
            if (wind.has("deg")) data.setWindDegree(wind.get("deg").getAsInt());

            if (root.has("clouds"))
                data.setCloudiness(root.getAsJsonObject("clouds").get("all").getAsInt());
            if (root.has("visibility"))
                data.setVisibility(root.get("visibility").getAsLong());

            JsonObject weather = root.getAsJsonArray("weather").get(0).getAsJsonObject();
            data.setMainCondition(weather.get("main").getAsString());
            data.setDescription(weather.get("description").getAsString());
            data.setIconCode(weather.get("icon").getAsString());

            return data;
        } catch (Exception e) {
            throw new WeatherException("Failed to parse weather data: " + e.getMessage());
        }
    }

    private List<WeatherData.ForecastItem> parseForecast(String json) throws WeatherException {
        try {
            JsonArray list = JsonParser.parseString(json).getAsJsonObject().getAsJsonArray("list");
            List<WeatherData.ForecastItem> items = new ArrayList<>();
            for (int i = 0; i < list.size(); i += 8) {
                JsonObject entry = list.get(i).getAsJsonObject();
                WeatherData.ForecastItem item = new WeatherData.ForecastItem();
                item.setTimestamp(entry.get("dt").getAsLong());
                JsonObject main = entry.getAsJsonObject("main");
                item.setTemperature(main.get("temp").getAsDouble());
                item.setTempMin(main.get("temp_min").getAsDouble());
                item.setTempMax(main.get("temp_max").getAsDouble());
                item.setHumidity(main.get("humidity").getAsInt());
                item.setWindSpeed(entry.getAsJsonObject("wind").get("speed").getAsDouble());
                JsonObject w = entry.getAsJsonArray("weather").get(0).getAsJsonObject();
                item.setMainCondition(w.get("main").getAsString());
                item.setDescription(w.get("description").getAsString());
                item.setIconCode(w.get("icon").getAsString());
                items.add(item);
                if (items.size() >= 5) break;
            }
            return items;
        } catch (Exception e) {
            throw new WeatherException("Failed to parse forecast: " + e.getMessage());
        }
    }

    private String encodeCity(String city) {
        return city.trim().replace(" ", "+");
    }

    // ── Inner classes ─────────────────────────────────────────────
    public static class CityResult {
        public final String name, state, country;
        public final double lat, lon;
        public CityResult(String name, String state, String country, double lat, double lon) {
            this.name = name; this.state = state; this.country = country;
            this.lat = lat; this.lon = lon;
        }
        @Override public String toString() {
            return state.isEmpty()
                ? name + ", " + country
                : name + ", " + state + ", " + country;
        }
    }

    public static class WeatherException extends Exception {
        public WeatherException(String message) { super(message); }
    }
}
