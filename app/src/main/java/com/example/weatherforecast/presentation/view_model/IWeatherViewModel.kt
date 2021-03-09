package com.example.weatherforecast.presentation.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.pojo.WeatherModel
import com.example.weatherforecast.pojo.WeatherModelBD
import com.example.weatherforecast.utils.Resource

interface IWeatherViewModel {
    fun fetchWeather(): MutableLiveData<Resource<WeatherModel>>

    suspend fun addweather(weatherModelBD: WeatherModelBD)

    fun getweatherFromDB(): LiveData<WeatherModelBD>
}