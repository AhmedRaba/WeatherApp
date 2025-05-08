package com.instabug.weather.presentation.current_weather

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.utils.LocationProvider
import com.instabug.weather.utils.Resource

@Composable
fun CurrentWeatherScreen(viewModel: CurrentWeatherViewModel = CurrentWeatherViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val cityName = remember { mutableStateOf<String?>(null) }


    // Fetch the user's location when the Composable is first launched
    LaunchedEffect(Unit) {
        val location = LocationProvider.getLastKnownLocation(context)
        if (location == null) {
            println("⚠️ No location found")
        } else {
            val lat = location.latitude
            val lng = location.longitude
            LocationProvider.getCityNameFromLocation(context, location) { city ->
                cityName.value = city
            }
            viewModel.fetchWeather(lat, lng)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {

            Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> {
                val data = (state as Resource.Success<WeatherData>).data

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        cityName.value?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Current Temp: ${data.temperature}°",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Max: ${data.tempMax}°", style = MaterialTheme.typography.bodyMedium)
                        Text("Min: ${data.tempMin}°", style = MaterialTheme.typography.bodyMedium)


                    }
                }

            }

            is Resource.Error -> {
                val error = (state as Resource.Error).message
                Text(
                    text = "❌ Failed to load weather: $error",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
