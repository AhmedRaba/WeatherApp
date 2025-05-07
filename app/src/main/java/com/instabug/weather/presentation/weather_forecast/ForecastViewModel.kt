package com.instabug.weather.presentation.weather_forecast

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.instabug.weather.data.repository.WeatherRepositoryImpl
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.usecase.GetFiveDayForecastUseCase

class ForecastViewModel:ViewModel() {

    var state by mutableStateOf<List<WeatherData>>(emptyList())

    private val repository = WeatherRepositoryImpl()
    private val useCase = GetFiveDayForecastUseCase(repository)

    init {
        fetchForecast()
    }


    private fun fetchForecast() {
        Thread{
            val result = useCase.execute()
            state=result
        }.start()
    }




}