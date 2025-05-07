package com.instabug.weather.data.remote

import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class WeatherApiService {

    private val baseUrl =
        "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/30.4163%2C31.5698?unitGroup=uk&key=M4FXYQ7R83FVN7QH4JKQZ2E66&contentType=json"

    fun fetchWeatherJson(): JSONObject? {
        return try {
            val url = URL(baseUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val reader=BufferedReader(InputStreamReader(connection.inputStream))
            val response=reader.readText()
            reader.close()

            JSONObject(response)
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}