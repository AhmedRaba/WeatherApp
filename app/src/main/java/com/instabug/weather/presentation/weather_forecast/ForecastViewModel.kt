package com.instabug.weather.presentation.weather_forecast

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.instabug.weather.data.repository.WeatherRepositoryImpl
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.usecase.GetFiveDayForecastUseCase
import com.instabug.weather.utils.ConnectivityHelper
import com.instabug.weather.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class ForecastViewModel(application: Application) : AndroidViewModel(application) {
    private val _state = MutableStateFlow<Resource<List<WeatherData>>>(Resource.Loading)
    val state = _state.asStateFlow()

    private val repository = WeatherRepositoryImpl(getApplication())
    private val useCase = GetFiveDayForecastUseCase(repository)

    fun fetchForecast(lat: Double, lng: Double) {
        _state.value = Resource.Loading

        Thread {
            try {
                // Let the useCase handle both fresh/cached data
                val result = useCase.execute(lat, lng)
                Handler(Looper.getMainLooper()).post {
                    _state.value = result
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    _state.value = Resource.Error(e.message ?: "Unknown error")
                }
            }
        }.start()
    }
}