package com.rayfon.weatherdroid.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.open-meteo.com/"

private val retrofit = Retrofit.Builder()
  .addConverterFactory(GsonConverterFactory.create())
  .baseUrl(BASE_URL)
  .build()

interface WeatherApiService {
  @GET("v1/forecast")
  suspend fun getWeatherData(
    @Query("latitude") latitude: Double,
    @Query("longitude") longitude: Double,
    @Query("current") current: String = "temperature_2m,weather_code,precipitation_probability,wind_speed_10m,relative_humidity_2m",
    @Query("hourly") hourly: String = "temperature_2m,precipitation_probability,weather_code,is_day",
    @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",
    @Query("timezone") timezone: String = "auto"
  ): WeatherData
}

object WeatherApi {
  val retrofitService: WeatherApiService by lazy {
    retrofit.create(WeatherApiService::class.java)
  }
}