package com.instabug.weather.domain.usecase

import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.repository.WeatherRepository

class GetCurrentWeatherUseCase(private val repository: WeatherRepository) {
    fun execute(lat: Double, lng: Double): WeatherData? = repository.getCurrentWeather(lat,lng)
}