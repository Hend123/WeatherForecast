package com.example.weatherforecast.data.remote

import com.example.retrofitandcoroutine.data.remote.ApiService
import com.example.weatherforecast.pojo.WeatherModel
import retrofit2.Call

class ApiHelperImpl(private val apiService: ApiService): ApiHelper  {
    override suspend fun getWeather(
        lat: String,
        lon: String,
        lang: String,
        exclude: String,
        appId: String
    ): WeatherModel {
       return apiService.getWeather(lat, lon, lang, exclude, appId)
    }
}