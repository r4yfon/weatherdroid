package com.rayfon.weatherdroid.data

import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("daily") val daily: DailyWeather,
    @SerializedName("hourly") val hourly: HourlyWeather,
    @SerializedName("timezone") val timezone: String
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("precipitation_probability") val precipitationProbability: Double,
    @SerializedName("wind_speed_10m") val windSpeed: Double,
    @SerializedName("relative_humidity_2m") val humidity: Double
)

data class DailyWeather(
    @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
    @SerializedName("temperature_2m_min") val temperatureMin: List<Double>
)

data class HourlyWeather(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperature: List<Double>,
    @SerializedName("precipitation_probability") val precipitationProbability: List<Double>,
    @SerializedName("weather_code") val weatherCode: List<Int>,
    @SerializedName("is_day") val isDay: List<Int>
)