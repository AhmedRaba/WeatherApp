package com.instabug.weather.presentation.weather_forecast

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.instabug.weather.presentation.current_weather.getIconResId
import com.instabug.weather.utils.LocationProvider
import com.instabug.weather.utils.Resource
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastScreen(
    navController: NavController,
    viewModel: ForecastViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
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
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {

        TopAppBar(modifier = Modifier.align(Alignment.TopStart), title = {
            Text(
                text = "5-day forecast",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent // Transparent to blend with background
        )
        )

        when (state) {
            Resource.Loading -> {
                CircularProgressIndicator()
            }

            is Resource.Success -> {
                val forecastData = (state as Resource.Success<List<WeatherData>>).data
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(forecastData) { day ->
                            val iconResId = getIconResId(context, day.icon)

                            // Parse day of the week and date
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                            val date = dateFormat.parse(day.date)
                            val dayOfWeek = dayOfWeekFormat.format(date)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large,
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White.copy(
                                        alpha = 0.30f
                                    )
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {

                                    if (iconResId != 0) {
                                        Image(
                                            painter = painterResource(id = iconResId),
                                            contentDescription = day.conditions,
                                            modifier = Modifier
                                                .size(60.dp)
                                                .padding(end = 12.dp)
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "${day.temperature.toInt()}°",
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = getTemperatureColor(day.temperature.toInt())
                                        )

                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(SpanStyle(color = getTemperatureColor(day.tempMax.toInt()))) {
                                                    append("${day.tempMax.toInt()}°")
                                                }
                                                append("/")
                                                withStyle(SpanStyle(color = getTemperatureColor(day.tempMin.toInt()))) {
                                                    append("${day.tempMin.toInt()}°")
                                                }
                                            }, fontSize = 20.sp, fontWeight = FontWeight.Normal, color = Color.White
                                        )

                                        Text(
                                            text = day.conditions,
                                            color = Color.White,
                                            fontSize = 18.sp
                                        )
                                    }

                                    // Show Day of the Week and Date
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = dayOfWeek,  // Day of the week above the date
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = day.date, // Date below the day of the week
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            is Resource.Error -> {
                val errorMessage = (state as Resource.Error).message
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "❌ Failed to load forecast: $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
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
