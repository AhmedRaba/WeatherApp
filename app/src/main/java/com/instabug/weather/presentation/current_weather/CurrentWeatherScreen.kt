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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.instabug.weather.utils.LocationProvider

@Composable
fun CurrentWeatherScreen(viewModel: CurrentWeatherViewModel = CurrentWeatherViewModel()) {
    val context = LocalContext.current
    val data = viewModel.state

    // Fetch the user's location when the Composable is first launched
    LaunchedEffect(Unit) {
        val location = LocationProvider.getLastKnownLocation(context)
        if (location == null) {
            println("⚠️ No location found")
        } else {
            val lat = location.latitude
            val lng = location.longitude
            viewModel.fetchWeather(lat, lng)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (data == null) {
            CircularProgressIndicator()
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Date: ${data.date}", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Current Temp: ${data.temperature}°", style = MaterialTheme.typography.bodyLarge)
                Text("Max: ${data.tempMax}°", style = MaterialTheme.typography.bodyMedium)
                Text("Min: ${data.tempMin}°", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
