package com.instabug.weather.domain.usecase

import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.repository.WeatherRepository
import com.instabug.weather.utils.Resource

class GetFiveDayForecastUseCase(private val repository: WeatherRepository) {

    fun execute(lat: Double, lng: Double): Resource<List<WeatherData>>{

        return repository.getFiveDayForecast(lat, lng)
    }

}