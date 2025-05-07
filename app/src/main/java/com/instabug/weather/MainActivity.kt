package com.instabug.weather

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import android.Manifest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.core.content.ContextCompat
import com.instabug.weather.presentation.current_weather.CurrentWeatherScreen
import com.instabug.weather.presentation.weather_forecast.ForecastScreen
import com.instabug.weather.ui.theme.WeatherTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Check if either permission is granted
            val isLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (isLocationGranted) {
                // Either permission is granted, proceed with getting the weather
                setContent {
                    WeatherTheme {
                        ForecastScreen()
                    }
                }
            } else {
                // Handle the case where neither permission is granted
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

        // Check if either permission is already granted
        when {
            // If either permission is already granted, proceed with fetching weather
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                setContent {
                    WeatherTheme {
                        ForecastScreen()
                    }
                }
            }
            // If neither permission is granted, request both permissions
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