package com.instabug.weather

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.instabug.weather.presentation.current_weather.CurrentWeatherScreen
import com.instabug.weather.presentation.navigation.NavGraph
import com.instabug.weather.presentation.weather_forecast.ForecastScreen
import com.instabug.weather.ui.theme.WeatherTheme
import java.util.Calendar

class MainActivity : ComponentActivity() {

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val isDaytime = currentHour in 6..18
    val backgroundDrawable = if (isDaytime) R.drawable.iv_day else R.drawable.iv_night



    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isLocationGranted) {
                setContent {
                    WeatherTheme {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Background image
                            Image(
                                painter = painterResource(id = R.drawable.iv_day), // Or dynamic image
                                contentDescription = "Background",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Navigation graph
                            NavGraph(navController = rememberNavController())
                        }
                    }
                }
            } else {
                setContent {
                    WeatherTheme {
                        Text("Location permission is required to fetch weather data.")
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                setContent {
                    WeatherTheme {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = backgroundDrawable),
                                contentDescription = "Background",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            NavGraph(navController = rememberNavController())
                        }
                    }
                }
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }
}
