package com.instabug.weather.domain.repository

import com.instabug.weather.domain.model.WeatherData

interface WeatherRepository {

    fun getCurrentWeather(): WeatherData?

    fun getFiveDayForecast(): List<WeatherData>

}