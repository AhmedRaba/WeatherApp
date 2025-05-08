package com.instabug.weather.domain.usecase

import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.repository.WeatherRepository
import com.instabug.weather.utils.Resource

class GetCurrentWeatherUseCase(private val repository: WeatherRepository) {
    fun execute(lat: Double, lng: Double): Resource<WeatherData> {
        return repository.getCurrentWeather(lat, lng)
    }
}