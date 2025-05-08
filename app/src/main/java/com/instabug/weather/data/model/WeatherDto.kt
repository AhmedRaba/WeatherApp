package com.instabug.weather.data.model

import com.instabug.weather.domain.model.WeatherData

data class WeatherDto(
    val date: String,
    val temperature: Double,
    val tempMax: Double,
    val tempMin: Double,
    val conditions: String,
    val icon:String
) {
    fun toDomain(): WeatherData {
        return WeatherData(
            date = date,
            temperature = temperature,
            tempMax = tempMax,
            tempMin = tempMin,
            conditions = conditions,
            icon=icon
        )
    }
}
