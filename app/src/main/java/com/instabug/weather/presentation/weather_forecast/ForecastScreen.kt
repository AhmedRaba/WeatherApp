package com.instabug.weather.presentation.weather_forecast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.instabug.weather.utils.LocationProvider

@Composable
fun ForecastScreen(viewModel: ForecastViewModel = ForecastViewModel()) {
    val context = LocalContext.current
    val forecastData = viewModel.state
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
            viewModel.fetchForecast(lat, lng)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (forecastData.isEmpty()) {
            CircularProgressIndicator()
        } else {
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
                        Text("Max: ${day.tempMax}°", style = MaterialTheme.typography.bodyMedium)
                        Text("Min: ${day.tempMin}°", style = MaterialTheme.typography.bodyMedium)
                        Text(day.conditions, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}