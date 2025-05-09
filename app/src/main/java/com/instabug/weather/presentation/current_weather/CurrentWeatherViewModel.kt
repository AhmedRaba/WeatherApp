package com.instabug.weather.presentation.current_weather

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.instabug.weather.data.repository.WeatherRepositoryImpl
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.usecase.GetCurrentWeatherUseCase
import com.instabug.weather.utils.ConnectivityHelper
import com.instabug.weather.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CurrentWeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableStateFlow<Resource<WeatherData>>(Resource.Loading)
    val state = _state.asStateFlow()

    private val repository = WeatherRepositoryImpl()
    private val useCase = GetCurrentWeatherUseCase(repository)

    fun fetchWeather(lat: Double, lng: Double) {
        if (ConnectivityHelper.isConnectedToInternet(getApplication())) {
            _state.value = Resource.Loading
            Thread {
                try {
                    val result = useCase.execute(lat, lng)
                    _state.value = result
                } catch (e: Exception) {
                    _state.value = Resource.Error(e.message ?: "Unknown error")
                }
            }.start()
        } else {
            _state.value = Resource.Error("No internet connection")
        }
    }
}
