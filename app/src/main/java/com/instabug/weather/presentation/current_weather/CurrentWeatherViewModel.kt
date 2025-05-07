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

    private val repository = WeatherRepositoryImpl()
    private val useCase = GetCurrentWeatherUseCase(repository)

    init {
        fetchWeather()
    }


    private fun fetchWeather() {
        Thread{
            val data = useCase.execute()
            state=data
        }.start()
    }

}