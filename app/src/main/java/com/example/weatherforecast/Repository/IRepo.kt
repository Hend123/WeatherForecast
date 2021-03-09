package com.example.weatherforecast.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModel
import com.example.weatherforecast.pojo.WeatherModelBD
import com.example.weatherforecast.utils.Resource

interface IRepo {
    fun fetchWeather(): MutableLiveData<Resource<WeatherModel>>

    suspend fun addweatherInDB(weatherModelBD: WeatherModelBD)
    fun getweatherFromDB(): LiveData<WeatherModelBD>

    fun addFavoriteDB(favoriteModelBD: FavoriteModelBD)
    fun getFavoriteFromDB(): LiveData<MutableList<FavoriteModelBD>>
    fun deleteFavItem(favoriteModelBD: FavoriteModelBD)

    fun addAlert(alertDB: AlertDB)
    fun getAlertFromDB(): LiveData<MutableList<AlertDB>>
    fun deleteAlertItem(alertDB: AlertDB)
}