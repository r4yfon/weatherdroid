## Overview

This is a Weather Application for Android OS. The app is built with **Kotlin** and **Jetpack Compose**, targeting API level 28 (Android 9) and above. It enables users to view the current weather for their location or select from a list of major cities.

---

## Features

- **Current Location Weather:**  
  - Obtains the user's current latitude and longitude using Android's location services (LocationManager).
  - Performs reverse geocoding to convert coordinates to a city name using the Android SDK's `Geocoder`.

- **City Selection:**  
  - Displays the detected city name in a dropdown menu.
  - Allows users to select from a list of major cities worldwide (e.g., İstanbul, Paris, Berlin, London, Tokyo, New York).
  - Fetches and displays weather data for the selected city.

- **Weather Data:**  
  - Uses the **Retrofit** library to fetch current weather information from a public REST API (such as [open-meteo.com](https://open-meteo.com/),
  - Displays temperature and a visual weather indicator (Sunny, Cloudy, Rainy, etc.) using SVG icons.

- **Modern UI:**  
  - Built with Jetpack Compose for a modern, responsive interface.
  - UI optimized for functionality, with the flexibility to enhance aesthetics.

- **Permissions:**  
  - Requests both `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` at runtime for compatibility with Android 12+ (API 31+).
  - Supports approximate location permissions as per Android guidelines.

---

## Screenshots

<img width="1187" height="2513" alt="Screenshot_20250730_165104" src="https://github.com/user-attachments/assets/8dd85e48-0924-4715-94d6-99feedf10461" />

---

## How it Works

1. **On Launch:**  
   - The app requests location permissions.
   - Retrieves the current location.
   - Performs reverse geocoding to obtain the city name.

2. **Weather Fetch:**  
   - Makes a network call using Retrofit to fetch weather data for the detected city.

3. **Display:**  
   - Shows the city name, temperature, and a corresponding weather icon.
   - Dropdown allows city selection, updating the weather accordingly.

---

## Major Dependencies

- [Kotlin](https://kotlinlang.org/)  
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Retrofit](https://square.github.io/retrofit/)
- [Lucide Icons](https://lucide.dev/icons/) (for SVG weather icons)  
- [open-meteo.com](https://open-meteo.com/) for weather API

---

## Weather Icons

Weather condition icons are provided by [Lucide Icons](https://lucide.dev/icons/).  
**Credit:** [Lucide Icons - Open Source SVG Icon Library](https://lucide.dev/)

---

## Setup & Running

1. **Clone the Repository:**  
   ```sh
   git clone [https://github.com/r4yfon/weatherdroid.git](https://github.com/r4yfon/weatherdroid.git)
   ```

2. **Open in Android Studio:**  
   - Use an emulator with Android 13 (API 33) or higher for testing.

3. **Build & Run.**

---

## Acknowledgements

- **Lucide Icons:** For providing the SVG weather icons used in the app UI.
- **Weather APIs:** open-meteo.com, weatherstack.com, weatherapi.com for free weather data.
- **Android Developers Documentation:** For guidance on location, permissions, and Compose UI.
