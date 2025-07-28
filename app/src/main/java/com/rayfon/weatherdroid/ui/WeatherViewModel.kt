package com.rayfon.weatherdroid.ui

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.Manifest
import com.rayfon.weatherdroid.R
import com.rayfon.weatherdroid.data.WeatherApi
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


data class WeatherUiState(
    val city: String = "",
    val temperature: String = "--",
    val highTemperature: String = "--",
    val lowTemperature: String = "--",
    val weatherDescription: String = "...",
    val precipitationProbability: String = "--",
    val windSpeed: String = "--",
    val humidity: String = "--",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val cityList: List<String> = listOf(
        "Istanbul", "Paris", "Berlin", "London", "Tokyo", "New York"
    ),
    val hourlyForecast: List<HourlyForecastItem> = emptyList(),
    val timezone: String = "UTC"
)

data class HourlyForecastItem(
    val time: String,
    val temperature: Double,
    val precipitationProbability: Double,
    val weatherCode: Int,
    val isDay: Int
)

class WeatherViewModel: ViewModel() {
    var uiState by mutableStateOf(WeatherUiState())
        private set

    private val cityCoordinates = mapOf(
        "Istanbul" to (41.0082 to 28.9784),
        "Paris" to (48.8566 to 2.3522),
        "Berlin" to (52.5200 to 13.4050),
        "London" to (51.5072 to -0.1276),
        "Tokyo" to (35.6762 to 139.6503),
        "New York" to (40.7128 to -74.0060)
    )

    fun getWeatherForCity(city: String) {
        cityCoordinates[city]?.let { (lat, lon) ->
            fetchWeather(lat, lon, city)
        }
    }

    private fun fetchWeather(
        latitude: Double,
        longitude: Double,
        cityName: String? = null
    ) {
        viewModelScope.launch {
            try {
                val weatherData = WeatherApi.retrofitService.getWeatherData(latitude, longitude)
                uiState = uiState.copy(
                    city = cityName ?: uiState.city,
                    temperature = "${weatherData.current.temperature.toInt()}°C",
                    highTemperature = "${weatherData.daily.temperatureMax[0].toInt()}°C",
                    lowTemperature = "${weatherData.daily.temperatureMin[0].toInt()}°C",
                    weatherDescription = mapWeatherCodeToDescription(weatherData.current.weatherCode),
                    precipitationProbability = "${weatherData.current.precipitationProbability.toInt()}%",
                    windSpeed = "${weatherData.current.windSpeed} km/h",
                    humidity = "${weatherData.current.humidity.toInt()}%",
                    timezone = weatherData.timezone,
                    hourlyForecast = weatherData.hourly.time.indices.map { i ->
                        HourlyForecastItem(
                            time = weatherData.hourly.time[i],
                            temperature = weatherData.hourly.temperature[i],
                            precipitationProbability = weatherData.hourly.precipitationProbability[i],
                            weatherCode = weatherData.hourly.weatherCode[i],
                            isDay = weatherData.hourly.isDay[i]
                        )
                    }
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    errorMessage = "Failed to fetch weather data"
                )
            }
        }
    }

    fun fetchWeatherForCurrentLocation(context: Context) {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isFindLocationPermitted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isCoarseLocationPermitted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isFindLocationPermitted && !isCoarseLocationPermitted) {
            uiState = uiState.copy(errorMessage = "Location permission is required")
            return
        }

        try {
            val location =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude

                getCityNameAndFetchWeather(context, lat, lon)
            } else {
                uiState = uiState.copy(errorMessage = "Could not retrieve location.")
            }
        } catch (e: SecurityException) {
            uiState = uiState.copy(errorMessage = "Location permission not granted.")
        }
    }

    private fun getCityNameAndFetchWeather(
        context: Context,
        lat: Double,
        lon: Double
    ) {
        val geocoder = Geocoder(context, Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(lat, lon, 1) { addresses ->
                val cityName = addresses.firstOrNull()?.locality ?: "Unknown Location"
                fetchWeather(lat, lon, cityName)
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            val cityName = addresses?.firstOrNull()?.locality ?: "Unknown Location"
            fetchWeather(lat, lon, cityName)
        }
    }

    private fun mapWeatherCodeToDescription(code: Int): String {
        return when (code) {
            0 -> "Clear sky"
            1 -> "Mainly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast"
            45 -> "Fog"
            48 -> "Depositing rime fog"
            51 -> "Light drizzle"
            53 -> "Moderate drizzle"
            55 -> "Dense drizzle"
            56 -> "Light freezing drizzle"
            57 -> "Dense freezing drizzle"
            61 -> "Slight rain"
            63 -> "Moderate rain"
            65 -> "Heavy rain"
            66 -> "Light freezing rain"
            67 -> "Heavy freezing rain"
            71 -> "Slight snowfall"
            73 -> "Moderate snowfall"
            75 -> "Heavy snowfall"
            77 -> "Snow grains"
            80 -> "Slight rain showers"
            81 -> "Moderate rain showers"
            82 -> "Violent rain showers"
            85 -> "Slight snow showers"
            86 -> "Heavy snow showers"
            95 -> "Slight thunderstorm"
            96 -> "Thunderstorm with slight hail"
            99 -> "Thunderstorm with heavy hail"
            else -> "Unknown"
        }
    }

    fun mapWeatherCodeToIconRes(code: Int, isDay: Int): Int {
        return when (code) {
            0 -> if (isDay == 1) R.drawable.sun_medium else R.drawable.moon
            1, 2, 3 -> if (isDay == 1) R.drawable.cloud else R.drawable.moon
            45, 48 -> R.drawable.cloud_fog
            51, 53, 55, 56, 57 -> R.drawable.cloud_rain
            61, 63, 65, 66, 67, 80, 81, 82 -> R.drawable.cloud_rain
            71, 73, 75, 77, 85, 86 -> R.drawable.snowflake
            95, 96, 99 -> R.drawable.cloud_lightning
            else -> if (isDay == 1) R.drawable.sun_medium else R.drawable.moon
        }
    }

    fun getNext24HourlyForecast(): List<HourlyForecastItem> {
        val allHours = uiState.hourlyForecast
        if (allHours.isEmpty()) return emptyList()

        val apiZoneId = try {
            ZoneId.of(uiState.timezone)
        } catch (e: Exception) {
            ZoneId.of("UTC")
        }

        val nowInApiZone = ZonedDateTime.now(apiZoneId)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

        val currentIndex = allHours.indexOfFirst {
            try {
                val forecastTime = LocalDateTime.parse(it.time.substring(0, 16), formatter)
                    .atZone(apiZoneId)
                forecastTime.hour == nowInApiZone.hour && forecastTime.dayOfYear == nowInApiZone.dayOfYear
            } catch (e: Exception) {
                false
            }
        }.let { if (it == -1) 0 else it }

        return (allHours + allHours).subList(currentIndex, currentIndex + 24)
    }
}