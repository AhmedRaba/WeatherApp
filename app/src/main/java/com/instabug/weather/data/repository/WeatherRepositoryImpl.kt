package com.instabug.weather.data.repository

import com.instabug.weather.data.model.WeatherDto
import com.instabug.weather.data.remote.WeatherApiService
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.repository.WeatherRepository

class WeatherRepositoryImpl: WeatherRepository {

    private val api=WeatherApiService()

    override fun getCurrentWeather(): WeatherData? {
        val json=api.fetchWeatherJson() ?: return null
        val today=json.getJSONArray("days").getJSONObject(0)
        val dto = WeatherDto(
            date = today.getString("datetime"),
            temperature = today.getDouble("temp"),
            tempMax = today.getDouble("tempmax"),
            tempMin = today.getDouble("tempmin")
        )
        return dto.toDomain()

    }

    override fun getFiveDayForecast(): List<WeatherData> {
        val json=api.fetchWeatherJson() ?: return emptyList()
        val daysArray = json.getJSONArray("days")
        val forecast= mutableListOf<WeatherData>()

        for (i in 0 until minOf(5,daysArray.length())){
            val day=daysArray.getJSONObject(i)
            val dto = WeatherDto(
                date = day.getString("datetime"),
                temperature = day.getDouble("temp"),
                tempMax = day.getDouble("tempmax"),
                tempMin = day.getDouble("tempmin")
            )
            forecast.add(dto.toDomain())

        }
        return forecast
    }


}