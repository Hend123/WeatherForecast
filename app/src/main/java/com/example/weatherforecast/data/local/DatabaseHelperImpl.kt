package com.example.weatherforecast.data.local

import androidx.lifecycle.LiveData
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModelBD

class DatabaseHelperImpl (private val weatherDatabase: WeatherDatabase) : DatabaseHelper{
    override suspend fun insertWeather(modelBD: WeatherModelBD)
            = weatherDatabase.weatherDao().insertWeather(modelBD)

    override  fun getWeather(): LiveData<WeatherModelBD>
    = weatherDatabase.weatherDao().getWeather()

    override suspend fun deleteCurrentWeather(modelBD: WeatherModelBD)
    = weatherDatabase.weatherDao().deleteCurrentWeather(modelBD)

    override suspend fun insertFavoriteWeather(favoriteModelBD: FavoriteModelBD)
    = weatherDatabase.favoriteDao().insertFavoriteWeather(favoriteModelBD)

    override  fun getFavoriteWeather(): LiveData<MutableList<FavoriteModelBD>>
    = weatherDatabase.favoriteDao().getFavoriteWeather()

    override suspend fun deleteFavoriteWeather(modelBD: FavoriteModelBD)
    = weatherDatabase.favoriteDao().deleteFavoriteWeather(modelBD)

    override suspend fun insertAlert(alertDB: AlertDB)
    = weatherDatabase.alertDao().insertAlert(alertDB)

    override fun getAlerts(): LiveData<MutableList<AlertDB>>
    = weatherDatabase.alertDao().getAlerts()

    override suspend fun deleteAlert(alertDB: AlertDB)
    = weatherDatabase.alertDao().deleteAlert(alertDB)

}