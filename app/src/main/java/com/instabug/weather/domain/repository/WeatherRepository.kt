package com.instabug.weather.domain.repository

import com.instabug.weather.domain.model.WeatherData

interface WeatherRepository {

    fun getCurrentWeather(lat: Double, lng: Double): WeatherData?

    fun getFiveDayForecast(lat: Double, lng: Double): List<WeatherData>

}