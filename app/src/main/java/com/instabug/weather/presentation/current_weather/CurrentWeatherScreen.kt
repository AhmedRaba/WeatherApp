package com.instabug.weather.presentation.current_weather

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.instabug.weather.R
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.utils.LocationProvider
import com.instabug.weather.utils.Resource
import java.time.LocalDateTime
import java.util.Calendar

@Composable
fun CurrentWeatherScreen(viewModel: CurrentWeatherViewModel = CurrentWeatherViewModel()) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val cityName = remember { mutableStateOf<String?>(null) }

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val isDaytime = currentHour in 6..18
    val backgroundDrawable = if (isDaytime) R.drawable.iv_day else R.drawable.iv_night

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
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = backgroundDrawable),
            null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        when (state) {

            Resource.Loading -> CircularProgressIndicator()

            is Resource.Success -> {
                val data = (state as Resource.Success<WeatherData>).data
                val iconResId = getIconResId(context, data.icon)

                println(data.icon)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {


                        if (iconResId != 0) {
                            Image(
                                painter = painterResource(id = iconResId),
                                contentDescription = data.conditions,
                                modifier = Modifier
                                    .size(140.dp)
                                    .padding(bottom = 8.dp)
                            )
                        } else {
                            Text(
                                text = "⚠️ Icon not found for '${data.icon}'",
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
                            "${data.temperature.toInt()}°",
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 70.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Normal
                        )

                        Text(
                            "${data.tempMax.toInt()}°/${data.tempMin.toInt()}°",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 30.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Normal
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            data.conditions,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 26.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Normal
                        )
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


fun getIconResId(context: Context, iconName: String): Int {
    val normalizedName = iconName.replace("-", "_").lowercase()
    return context.resources.getIdentifier(normalizedName, "drawable", context.packageName)
}

