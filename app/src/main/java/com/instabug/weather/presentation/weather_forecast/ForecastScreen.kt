package com.instabug.weather.presentation.weather_forecast

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instabug.weather.R
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.presentation.current_weather.getIconResId
import com.instabug.weather.utils.LocationProvider
import com.instabug.weather.utils.Resource
import java.util.Calendar

@Composable
fun ForecastScreen(viewModel: ForecastViewModel = ForecastViewModel(context = LocalContext.current)) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    Log.d("ForecastScreen", "Current state: $state")
    val cityName = remember { mutableStateOf<String?>(null) }
    var lastLatLng by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val isDaytime = currentHour in 6..18
    val backgroundDrawable = if (isDaytime) R.drawable.iv_day else R.drawable.iv_night

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
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = backgroundDrawable),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        when (state) {
            Resource.Loading -> {
                Log.d("ForecastScreen", "Showing Loading Indicator")
                CircularProgressIndicator()
            }

            is Resource.Success -> {
                Log.d("ForecastScreen", "Showing Success Indicator")

                val forecastData = (state as Resource.Success<List<WeatherData>>).data
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 30.dp)
                ) {

                    items(forecastData) { day ->
                        val iconResId = getIconResId(context, day.icon)

                        Card(
                            modifier = Modifier.width(200.dp).padding(end = 16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {

                                if (iconResId != 0) {
                                    Image(
                                        painter = painterResource(id = iconResId),
                                        contentDescription = day.conditions,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .padding(bottom = 8.dp)
                                    )
                                } else {
                                    Text(
                                        text = "⚠️ Icon not found for '${day.icon}'",
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                }

                                cityName.value?.let {
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.White,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 30.sp
                                    )
                                }

                                Text(
                                    "${day.temperature.toInt()}°",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontSize = 70.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Normal
                                )

                                Text(
                                    "${day.tempMax.toInt()}°/${day.tempMin.toInt()}°",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 30.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Normal
                                )

                                Spacer(Modifier.height(8.dp))

                                Text(
                                    day.conditions,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 26.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Normal
                                )
                            }
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
private fun Preview() {
    MaterialTheme {
        ForecastScreen()
    }
}