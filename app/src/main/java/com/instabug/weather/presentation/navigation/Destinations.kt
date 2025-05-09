package com.instabug.weather.presentation.navigation


// Define screen routes here
sealed class Screen(val route: String) {
    object CurrentWeather : Screen("current_weather")
    object Forecast : Screen("forecast_screen")
}
