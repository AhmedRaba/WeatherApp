package com.instabug.weather.data.remote

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class WeatherApiService {

    fun fetchWeatherJson(lat: Double, lng: Double): JSONObject? {

        val baseUrl =
            "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/$lat,$lng?unitGroup=uk&key=M4FXYQ7R83FVN7QH4JKQZ2E66&contentType=json"
        return try {
            val url = URL(baseUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()
            JSONObject(response)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentHourIcon(lat: Double, lng: Double): String? {
        val jsonObject = fetchWeatherJson(lat, lng)
        val hoursArray = jsonObject?.getJSONArray("days")?.getJSONObject(0)?.getJSONArray("hours")

        if (hoursArray != null) {
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            for (i in 0 until hoursArray.length()) {
                val hourData = hoursArray.getJSONObject(i)
                val hour = hourData.getString("datetime").split(":")[0].toInt()
                if (hour == currentHour) {
                    return hourData.getString("icon")
                }
            }
        }
        return null
    }
}