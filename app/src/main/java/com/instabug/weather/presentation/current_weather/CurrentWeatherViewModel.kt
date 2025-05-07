package com.instabug.weather.presentation.current_weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.instabug.weather.data.repository.WeatherRepositoryImpl
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.usecase.GetCurrentWeatherUseCase

class CurrentWeatherViewModel : ViewModel() {

    var state by mutableStateOf<WeatherData?>(null)
    private set

    private val repository = WeatherRepositoryImpl()
    private val useCase = GetCurrentWeatherUseCase(repository)



    fun fetchWeather(lat: Double, lng: Double) {
        Thread {
            println("📡 Fetching weather for: $lat, $lng")
            val result = useCase.execute(lat, lng)
            println("✅ Weather fetched: $result")
            state = result
        }.start()
    }

}