package com.instabug.weather.data.repository

import com.instabug.weather.data.model.WeatherDto
import com.instabug.weather.data.remote.WeatherApiService
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.repository.WeatherRepository
import com.instabug.weather.utils.Resource

class WeatherRepositoryImpl : WeatherRepository {

    private val api = WeatherApiService()

    override fun getCurrentWeather(lat: Double, lng: Double): Resource<WeatherData> {
        return try {
            val json = api.fetchWeatherJson(lat, lng) ?: return Resource.Error("No data")
            val today = json.getJSONArray("days").getJSONObject(0)
            val dto = WeatherDto(
                date = today.getString("datetime"),
                temperature = today.getDouble("temp"),
                tempMax = today.getDouble("tempmax"),
                tempMin = today.getDouble("tempmin"),
                conditions = today.getString("conditions")
            )
            Resource.Success(dto.toDomain())
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unknown error")
        }

    }

    override fun getFiveDayForecast(lat: Double, lng: Double): Resource<List<WeatherData>> {
        return try {


            val json = api.fetchWeatherJson(lat, lng) ?: return Resource.Error("No data")
            val daysArray = json.getJSONArray("days")
            val forecast = mutableListOf<WeatherData>()

            for (i in 0 until minOf(5, daysArray.length())) {
                val day = daysArray.getJSONObject(i)
                val dto = WeatherDto(
                    date = day.getString("datetime"),
                    temperature = day.getDouble("temp"),
                    tempMax = day.getDouble("tempmax"),
                    tempMin = day.getDouble("tempmin"),
                    conditions = day.getString("conditions")
                )
                forecast.add(dto.toDomain())
            }

            Resource.Success(forecast)

        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Unknown error")
        }

    }


}