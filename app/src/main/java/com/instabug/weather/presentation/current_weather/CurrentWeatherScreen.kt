package com.instabug.weather.presentation.current_weather

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.instabug.weather.domain.model.WeatherData
import com.instabug.weather.presentation.component.getTemperatureColor
import com.instabug.weather.presentation.navigation.Screen
import com.instabug.weather.utils.LocationProvider
import com.instabug.weather.utils.Resource

@Composable
fun CurrentWeatherScreen(
    navController: NavController,
    viewModel: CurrentWeatherViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val cityName = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        fetchWeatherData(context, viewModel, cityName)
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        when (state) {
            Resource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            is Resource.Success -> {
                val data = (state as Resource.Success<WeatherData>).data
                val iconResId = getIconResId(context, data.icon)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        cityName.value?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (iconResId != 0) {
                            Image(
                                painter = painterResource(id = iconResId),
                                contentDescription = data.conditions,
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(bottom = 16.dp)
                            )
                        } else {
                            Text(
                                text = "⚠️ Icon not found for '${data.icon}'",
                                color = MaterialTheme.colorScheme.error,
                            )
                        }

                        Text(
                            data.conditions,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 26.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Normal
                        )

                        Text(
                            "${data.temperature.toInt()}°",
                            style = MaterialTheme.typography.displayLarge,
                            fontSize = 80.sp,
                            color = getTemperatureColor(data.temperature.toInt()),
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(color = getTemperatureColor(data.tempMax.toInt()))) {
                                    append("${data.tempMax.toInt()}°")
                                }
                                append("/")
                                withStyle(style = SpanStyle(color = getTemperatureColor(data.tempMin.toInt()))) {
                                    append("${data.tempMin.toInt()}°")
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 30.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Button(modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp)
                        .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        onClick = { navController.navigate(Screen.Forecast.route) }) {
                        Text(text = "5-Day Forecast")
                    }


                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh Weather",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 16.dp)
                            .size(30.dp) // Adjust size as needed
                            .clickable { fetchWeatherData(context, viewModel, cityName) }
                    )
                }
            }

            is Resource.Error -> {
                val error = (state as Resource.Error).message
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "❌ Failed to load weather: $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        val location = LocationProvider.getLastKnownLocation(context)
                        if (location != null) {
                            viewModel.fetchWeather(location.latitude, location.longitude)
                        } else {
                            Log.w("CurrentWeatherScreen", "⚠️ No location found")
                        }
                    }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}


private fun fetchWeatherData(
    context: Context,
    viewModel: CurrentWeatherViewModel,
    cityName: MutableState<String?>,
) {
    val location = LocationProvider.getLastKnownLocation(context)
    if (location == null) {
        Log.w("CurrentWeatherScreen", "⚠️ No location found")
    } else {
        val lat = location.latitude
        val lng = location.longitude
        LocationProvider.getCityNameFromLocation(context, location) { city ->
            cityName.value = city
        }
        viewModel.fetchWeather(lat, lng)
    }
}

fun getIconResId(context: Context, iconName: String): Int {
    val normalizedName = iconName.replace("-", "_").lowercase()
    return context.resources.getIdentifier(normalizedName, "drawable", context.packageName)
}

