package com.instabug.weather.presentation.current_weather

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.instabug.weather.data.repository.WeatherRepositoryImpl
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.usecase.GetCurrentWeatherUseCase
import com.instabug.weather.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrentWeatherViewModel : ViewModel() {


    private val _state = MutableStateFlow<Resource<WeatherData>>(Resource.Loading)
    val state= _state.asStateFlow()


    private val repository = WeatherRepositoryImpl()
    private val useCase = GetCurrentWeatherUseCase(repository)



    fun fetchWeather(lat: Double, lng: Double) {
        _state.value = Resource.Loading
        Thread {
            Log.d("CurrentWeatherVM", "Fetching weather for: $lat, $lng")
            try {
                val result = useCase.execute(lat, lng)
            Log.d("CurrentWeatherVM", "Weather fetched: $result")
            _state.value = result
            } catch (e: Exception) {
            Log.e("CurrentWeatherVM", "Error fetching weather: ${e.message}", e)
                _state.value = Resource.Error(e.message ?: "Unknown error")
            }
        }.start()
    }
}