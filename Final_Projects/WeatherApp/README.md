# ⛅ WeatherNow — JavaFX Weather Forecast App

A beautiful real-time weather application built with **Java 17**, **JavaFX 21**, **OkHttp**, and **Gson**, powered by the **OpenWeatherMap API**.

---

## 📸 Features

- 🔍 Search weather by any city name
- 🌡 Current temperature, feels-like, min/max
- 💧 Humidity, wind speed/direction, pressure, visibility
- 🌅 Sunrise & sunset times (local to the city)
- ☁️ Cloud coverage
- 📅 5-day forecast with daily summaries
- 🎨 Dynamic background themes per weather condition
- ⚠️ Graceful error handling (invalid city, network errors, bad API key)

---

## 🛠 Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 17 or higher |
| Maven | 3.8+ |
| OpenWeatherMap Account | Free tier |

---

## 🔑 Step 1 — Get Your API Key

1. Go to [https://openweathermap.org/api](https://openweathermap.org/api)
2. Click **Sign Up** (it's free)
3. After registering, go to **My API Keys**
4. Copy your key (it may take 10–30 minutes to activate after registration)

---

## ⚙️ Step 2 — Add Your API Key

Open this file:

```
src/main/java/com/weather/service/WeatherService.java
```

Find this line (line 15) and replace the placeholder:

```java
private static final String API_KEY = "YOUR_API_KEY_HERE";
```

Replace with your actual key:

```java
private static final String API_KEY = "a1b2c3d4e5f6...";
```

---

## 🚀 Step 3 — Build & Run

### Option A — Maven (Recommended)

```bash
# From the project root (WeatherApp/)
mvn clean install
mvn javafx:run
```

### Option B — Build a fat JAR

```bash
mvn clean package -DskipTests
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/WeatherApp-1.0-SNAPSHOT.jar
```

### Option C — Run in IntelliJ IDEA

1. Open the project (File → Open → select `WeatherApp/`)
2. Maven auto-imports dependencies
3. Right-click `WeatherApp.java` → **Run 'WeatherApp.main()'**

---

## 📁 Project Structure

```
WeatherApp/
├── pom.xml                             ← Maven build config
├── README.md
└── src/main/
    ├── java/
    │   ├── module-info.java
    │   └── com/weather/
    │       ├── WeatherApp.java          ← Entry point (extends Application)
    │       ├── controller/
    │       │   └── WeatherController.java  ← UI logic, event handlers
    │       ├── model/
    │       │   └── WeatherData.java     ← Data model (current + forecast)
    │       ├── service/
    │       │   └── WeatherService.java  ← HTTP calls + JSON parsing
    │       └── util/
    │           └── WeatherUtil.java     ← Emoji mapping, time formatting
    └── resources/
        └── com/weather/
            └── css/
                └── style.css           ← Full UI stylesheet
```

---

## 🌐 API Endpoints Used

| Endpoint | Purpose |
|----------|---------|
| `GET /data/2.5/weather?q={city}` | Current weather |
| `GET /data/2.5/forecast?q={city}&cnt=40` | 5-day / 3-hour forecast |

Both use `units=metric` (Celsius). Change to `units=imperial` in `WeatherService.java` for Fahrenheit.

---

## 🎨 Weather Themes

The app dynamically changes its background gradient based on conditions:

| Condition | Theme |
|-----------|-------|
| Clear Sky | Deep blue gradient |
| Clouds | Slate grey gradient |
| Rain / Drizzle | Indigo-blue gradient |
| Thunderstorm | Near-black gradient |
| Snow | Cool blue-white gradient |
| Mist / Fog / Haze | Purple-grey gradient |

---

## ⚠️ Error Handling

| Error | Message shown |
|-------|--------------|
| Invalid API key | "Invalid API key. Please check your OpenWeatherMap API key." |
| City not found | "City not found. Please check the city name and try again." |
| Rate limit hit | "Too many requests. Please wait a moment before searching again." |
| No internet | "Network error: ... Please check your internet connection." |
| Server error | "Server error (5xx). Please try again later." |

---

## 🔧 Customisation

- **Temperature units**: Change `UNITS = "metric"` to `"imperial"` in `WeatherService.java`
- **Forecast days**: The `cnt=40` parameter (40 × 3-hour steps = 5 days). Adjust as needed.
- **Accent colour**: Change `#3b82f6` in `style.css` to any colour you like.

---

## 📦 Dependencies

```xml
<!-- JavaFX 21 (controls + fxml) -->
org.openjfx:javafx-controls:21.0.2
org.openjfx:javafx-fxml:21.0.2

<!-- HTTP client -->
com.squareup.okhttp3:okhttp:4.12.0

<!-- JSON parsing -->
com.google.code.gson:gson:2.10.1
```

---

## 📄 License

MIT — free for personal and educational use.
