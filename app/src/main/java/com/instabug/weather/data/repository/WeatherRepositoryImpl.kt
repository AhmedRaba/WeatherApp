package com.instabug.weather.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.instabug.weather.data.model.WeatherDto
import com.instabug.weather.data.remote.WeatherApiService
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.repository.WeatherRepository
import com.instabug.weather.utils.ConnectivityHelper
import com.instabug.weather.utils.Resource
import org.json.JSONObject

class WeatherRepositoryImpl(private val context: Context) : WeatherRepository {

    private val api = WeatherApiService()
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("weather_cache", Context.MODE_PRIVATE)

    private fun saveWeatherDataToCache(lat: Double, lng: Double, data: JSONObject) {
        val editor = sharedPreferences.edit()
        val cacheKey = "weather_${lat}_$lng"
        editor.putString(cacheKey, data.toString())
        editor.putLong("${cacheKey}_timestamp", System.currentTimeMillis()) // Add timestamp
        editor.apply()
    }

    fun isCacheValid(lat: Double, lng: Double): Boolean {
        val cacheKey = "weather_${lat}_$lng"
        val timestamp = sharedPreferences.getLong("${cacheKey}_timestamp", 0)
        return (System.currentTimeMillis() - timestamp) < 3_600_000 // 1 hour expiry
    }

    fun getWeatherDataFromCache(lat: Double, lng: Double): WeatherDto? {
        val cacheKey = "weather_${lat}_$lng"
        val cachedData = sharedPreferences.getString(cacheKey, null)
        return if (cachedData != null) {
            val jsonObject = JSONObject(cachedData)
            val daysArray = jsonObject.getJSONArray("days")
            val today = daysArray.getJSONObject(0)
            WeatherDto(
                date = today.getString("datetime"),
                temperature = today.getDouble("temp"),
                tempMax = today.getDouble("tempmax"),
                tempMin = today.getDouble("tempmin"),
                conditions = today.getString("conditions"),
                icon = today.getString("icon")
            )
        } else {
            null
        }
    }

    override fun getCurrentWeather(lat: Double, lng: Double): Resource<WeatherData> {
        return try {
            // 1. Check internet first
            if (ConnectivityHelper.isConnectedToInternet(context) || !isCacheValid(lat, lng)) {
                val json = api.fetchWeatherJson(lat, lng) ?: return Resource.Error("No API data")
                saveWeatherDataToCache(lat, lng, json) // Cache the new data

                val daysArray = json.getJSONArray("days")
                val today = daysArray.getJSONObject(0)
                val currentHourIcon = api.getCurrentHourIcon(lat, lng)

                val dto = WeatherDto(
                    date = today.getString("datetime"),
                    temperature = today.getDouble("temp"),
                    tempMax = today.getDouble("tempmax"),
                    tempMin = today.getDouble("tempmin"),
                    conditions = today.getString("conditions"),
                    icon = currentHourIcon ?: today.getString("icon")
                )
                Resource.Success(dto.toDomain())
            }
            // 2. No internet? Check cache
            else {
                val cachedData = getWeatherDataFromCache(lat, lng)
                if (cachedData != null) {
                    val daysArray = JSONObject(cachedData.toString()).getJSONArray("days")
                    val today = daysArray.getJSONObject(0)
                    val currentHourIcon = api.getCurrentHourIcon(lat, lng) // Optional: Can skip if offline

                    val dto = WeatherDto(
                        date = today.getString("datetime"),
                        temperature = today.getDouble("temp"),
                        tempMax = today.getDouble("tempmax"),
                        tempMin = today.getDouble("tempmin"),
                        conditions = today.getString("conditions"),
                        icon = currentHourIcon ?: today.getString("icon")
                    )
                    Resource.Success(dto.toDomain())
                } else {
                    // 3. No cache and no internet
                    Resource.Error("No internet connection and no cached data")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
    override fun getFiveDayForecast(lat: Double, lng: Double): Resource<List<WeatherData>> {
        return try {
            // 1. Check internet first
            if (ConnectivityHelper.isConnectedToInternet(context) || !isCacheValid(lat, lng)) {
                val json = api.fetchWeatherJson(lat, lng) ?: return Resource.Error("No API data")
                saveWeatherDataToCache(lat, lng, json)

                val daysArray = json.getJSONArray("days")
                val forecast = mutableListOf<WeatherData>()

                for (i in 0 until minOf(5, daysArray.length())) {
                    val day = daysArray.getJSONObject(i)
                    val icon = if (i == 0) {
                        api.getCurrentHourIcon(lat, lng) ?: day.getString("icon")
                    } else {
                        day.getString("icon")
                    }

                    val dto = WeatherDto(
                        date = day.getString("datetime"),
                        temperature = day.getDouble("temp"),
                        tempMax = day.getDouble("tempmax"),
                        tempMin = day.getDouble("tempmin"),
                        conditions = day.getString("conditions"),
                        icon = icon
                    )
                    forecast.add(dto.toDomain())
                }
                Resource.Success(forecast)
            }
            // 2. No internet? Check cache
            else {
                val cachedData = getWeatherDataFromCache(lat, lng)
                if (cachedData != null) {
                    val daysArray = JSONObject(cachedData.toString()).getJSONArray("days")
                    val forecast = mutableListOf<WeatherData>()

                    for (i in 0 until minOf(5, daysArray.length())) {
                        val day = daysArray.getJSONObject(i)
                        val icon = if (i == 0) {
                            // Skip hourly icon if offline (or use cached day icon)
                            day.getString("icon")
                        } else {
                            day.getString("icon")
                        }

                        val dto = WeatherDto(
                            date = day.getString("datetime"),
                            temperature = day.getDouble("temp"),
                            tempMax = day.getDouble("tempmax"),
                            tempMin = day.getDouble("tempmin"),
                            conditions = day.getString("conditions"),
                            icon = icon
                        )
                        forecast.add(dto.toDomain())
                    }
                    Resource.Success(forecast)
                } else {
                    // 3. No cache and no internet
                    Resource.Error("No internet connection and no cached data")
                }
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }}
