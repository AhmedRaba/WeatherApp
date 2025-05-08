package com.instabug.weather.presentation.weather_forecast

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import com.instabug.weather.data.repository.WeatherRepositoryImpl
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.domain.usecase.GetFiveDayForecastUseCase
import com.instabug.weather.utils.ConnectivityHelper
import com.instabug.weather.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class ForecastViewModel(private val context: Context) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<WeatherData>>>(Resource.Loading)
    val state = _state.asStateFlow()

    private val repository = WeatherRepositoryImpl()
    private val useCase = GetFiveDayForecastUseCase(repository)


    fun fetchForecast(lat: Double, lng: Double) {
        if (ConnectivityHelper.isConnectedToInternet(context)) {
            _state.value = Resource.Loading
            Thread {
                Log.d("ForecastViewModel", "Fetching weather for: $lat, $lng")
                try {
                    val result = useCase.execute(lat, lng)
                    Log.d("ForecastViewModel", "Weather fetched: $result")

                    Handler(Looper.getMainLooper()).post {
                        _state.value = result
                    }
                } catch (e: Exception) {
                    Log.e("ForecastViewModel", "Error fetching weather: ${e.message}", e)

                    Handler(Looper.getMainLooper()).post {
                        _state.value = Resource.Error(e.message ?: "Unknown error")
                    }
                }
            }.start()
        } else {
            _state.value = Resource.Error("No internet connection")
        }

    }

}