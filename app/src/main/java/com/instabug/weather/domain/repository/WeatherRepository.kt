package com.instabug.weather.domain.repository

import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.utils.Resource

interface WeatherRepository {

    fun getCurrentWeather(lat: Double, lng: Double): Resource<WeatherData>

    fun getFiveDayForecast(lat: Double, lng: Double): Resource<List<WeatherData>>

}