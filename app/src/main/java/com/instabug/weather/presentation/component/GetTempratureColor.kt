package com.instabug.weather.presentation.component

import androidx.compose.ui.graphics.Color

fun getTemperatureColor(temp: Int): Color {
    return when {
        temp <= 0 -> Color(0xFF005A9C) // Darker Blue for freezing
        temp in 1..10 -> Color(0xFF007ACC) // Medium Blue
        temp in 11..20 -> Color(0xFF3399FF) // Brighter Blue with more contrast
        temp in 21..30 -> Color(0xFFFFA500) // Orange
        else -> Color(0xFFFF4500) // OrangeRed
    }
}
