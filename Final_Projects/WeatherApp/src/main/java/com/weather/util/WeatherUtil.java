package com.weather.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class WeatherUtil {

    /**
     * Maps OpenWeatherMap icon codes to Unicode weather emoji
     */
    public static String getWeatherEmoji(String iconCode) {
        if (iconCode == null) return "🌡";
        return switch (iconCode.substring(0, Math.min(2, iconCode.length()))) {
            case "01" -> "☀️";   // clear sky
            case "02" -> "⛅";   // few clouds
            case "03" -> "🌥";   // scattered clouds
            case "04" -> "☁️";   // broken/overcast clouds
            case "09" -> "🌧";   // shower rain
            case "10" -> "🌦";   // rain
            case "11" -> "⛈";   // thunderstorm
            case "13" -> "❄️";   // snow
            case "50" -> "🌫";   // mist/fog
            default   -> "🌡";
        };
    }

    /**
     * Maps weather condition to a background gradient style class
     */
    public static String getWeatherStyleClass(String mainCondition) {
        if (mainCondition == null) return "weather-default";
        return switch (mainCondition.toLowerCase()) {
            case "clear"        -> "weather-clear";
            case "clouds"       -> "weather-cloudy";
            case "rain",
                 "drizzle"      -> "weather-rainy";
            case "thunderstorm" -> "weather-storm";
            case "snow"         -> "weather-snow";
            case "mist",
                 "fog",
                 "haze",
                 "smoke",
                 "dust",
                 "sand"         -> "weather-mist";
            default             -> "weather-default";
        };
    }

    /**
     * Get wind direction label from degrees
     */
    public static String getWindDirection(int degrees) {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        int index = (int) Math.round(degrees / 45.0) % 8;
        return dirs[index];
    }

    /**
     * Format Unix timestamp to time string using city's timezone offset
     */
    public static String formatTime(long unixSeconds, long timezoneOffset) {
        return Instant.ofEpochSecond(unixSeconds)
                .atOffset(ZoneOffset.ofTotalSeconds((int) timezoneOffset))
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * Format Unix timestamp to day-of-week string
     */
    public static String formatDay(long unixSeconds, long timezoneOffset) {
        return Instant.ofEpochSecond(unixSeconds)
                .atOffset(ZoneOffset.ofTotalSeconds((int) timezoneOffset))
                .format(DateTimeFormatter.ofPattern("EEE"));
    }

    /**
     * Format Unix timestamp to date string
     */
    public static String formatDate(long unixSeconds, long timezoneOffset) {
        return Instant.ofEpochSecond(unixSeconds)
                .atOffset(ZoneOffset.ofTotalSeconds((int) timezoneOffset))
                .format(DateTimeFormatter.ofPattern("MMM d"));
    }

    /**
     * Convert visibility in meters to readable string
     */
    public static String formatVisibility(long meters) {
        if (meters >= 1000) {
            return String.format("%.1f km", meters / 1000.0);
        }
        return meters + " m";
    }

    /**
     * Round temperature to integer
     */
    public static int roundTemp(double temp) {
        return (int) Math.round(temp);
    }
}
