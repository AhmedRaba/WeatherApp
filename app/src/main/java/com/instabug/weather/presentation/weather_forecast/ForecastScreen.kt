package com.instabug.weather.presentation.weather_forecast

import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.ui.theme.WeatherTheme
import com.instabug.weather.utils.LocationProvider
import com.instabug.weather.utils.Resource

@Composable
fun ForecastScreen(viewModel: ForecastViewModel = ForecastViewModel(context = LocalContext.current)) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    Log.d("ForecastScreen", "Current state: $state")
    val cityName = remember { mutableStateOf<String?>(null) }
    var lastLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }


    LaunchedEffect(Unit) {
        val location = LocationProvider.getLastKnownLocation(context)
        if (location != null) {
            lastLatLng = location.latitude to location.longitude
            LocationProvider.getCityNameFromLocation(context, location) { city ->
                cityName.value = city
            }
            viewModel.fetchForecast(location.latitude, location.longitude)
        } else {
            Log.e("ForecastScreen", "No Location Found")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            Resource.Loading -> {
                Log.d("ForecastScreen", "Showing Loading Indicator")

                CircularProgressIndicator()
            }

            is Resource.Success -> {
                Log.d("ForecastScreen", "Showing Success Indicator")

                val forecastData = (state as Resource.Success<List<WeatherData>>).data
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        cityName.value?.let {
                            Text(
                                text = "Weather Forecast for $it",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    items(forecastData) { day ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text("Date: ${day.date}", style = MaterialTheme.typography.titleLarge)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Temp: ${day.temperature}°",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                "Max: ${day.tempMax}°",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Min: ${day.tempMin}°",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(day.conditions, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

            }

            is Resource.Error -> {
                Log.d("ForecastScreen", "Showing Error Indicator")
                val errorMessage = (state as Resource.Error).message
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "❌ Failed to load forecast: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        Log.d("ForecastScreen", "Showing Error Indicator")
                        val location = LocationProvider.getLastKnownLocation(context)
                        if (location != null) {
                            viewModel.fetchForecast(location.latitude, location.longitude)
                        }
                    }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun Preview(){
    MaterialTheme {
    ForecastScreen()
    }
}