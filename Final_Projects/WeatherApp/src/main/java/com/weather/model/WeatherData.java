package com.weather.model;

import java.util.List;

public class WeatherData {
    private String cityName;
    private String country;
    private double temperature;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private int humidity;
    private int pressure;
    private double windSpeed;
    private int windDegree;
    private int cloudiness;
    private long visibility;
    private String description;
    private String mainCondition;
    private String iconCode;
    private long sunrise;
    private long sunset;
    private long timezone;
    private List<ForecastItem> forecast;

    // Getters and Setters
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double tempMin) { this.tempMin = tempMin; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double tempMax) { this.tempMax = tempMax; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public int getWindDegree() { return windDegree; }
    public void setWindDegree(int windDegree) { this.windDegree = windDegree; }

    public int getCloudiness() { return cloudiness; }
    public void setCloudiness(int cloudiness) { this.cloudiness = cloudiness; }

    public long getVisibility() { return visibility; }
    public void setVisibility(long visibility) { this.visibility = visibility; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMainCondition() { return mainCondition; }
    public void setMainCondition(String mainCondition) { this.mainCondition = mainCondition; }

    public String getIconCode() { return iconCode; }
    public void setIconCode(String iconCode) { this.iconCode = iconCode; }

    public long getSunrise() { return sunrise; }
    public void setSunrise(long sunrise) { this.sunrise = sunrise; }

    public long getSunset() { return sunset; }
    public void setSunset(long sunset) { this.sunset = sunset; }

    public long getTimezone() { return timezone; }
    public void setTimezone(long timezone) { this.timezone = timezone; }

    public List<ForecastItem> getForecast() { return forecast; }
    public void setForecast(List<ForecastItem> forecast) { this.forecast = forecast; }

    // Inner class for 5-day forecast items
    public static class ForecastItem {
        private long timestamp;
        private double temperature;
        private double tempMin;
        private double tempMax;
        private String description;
        private String iconCode;
        private String mainCondition;
        private int humidity;
        private double windSpeed;

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }

        public double getTempMin() { return tempMin; }
        public void setTempMin(double tempMin) { this.tempMin = tempMin; }

        public double getTempMax() { return tempMax; }
        public void setTempMax(double tempMax) { this.tempMax = tempMax; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getIconCode() { return iconCode; }
        public void setIconCode(String iconCode) { this.iconCode = iconCode; }

        public String getMainCondition() { return mainCondition; }
        public void setMainCondition(String mainCondition) { this.mainCondition = mainCondition; }

        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }

        public double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
    }
}
