package com.instabug.weather.domain.usecase

import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.repository.WeatherRepository

class GetFiveDayForecastUseCase(private val repository: WeatherRepository)  {

    fun execute(): List<WeatherData> = repository.getFiveDayForecast()

}