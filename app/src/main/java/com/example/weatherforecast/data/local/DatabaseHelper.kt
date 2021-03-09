package com.example.weatherforecast.data.local

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModelBD

interface DatabaseHelper {

    suspend fun insertWeather(modelBD: WeatherModelBD)

    fun getWeather(): LiveData<WeatherModelBD>

    suspend fun deleteCurrentWeather(modelBD: WeatherModelBD)

    suspend fun insertFavoriteWeather(favoriteModelBD: FavoriteModelBD)

    fun getFavoriteWeather(): LiveData<MutableList<FavoriteModelBD>>

    suspend fun deleteFavoriteWeather(modelBD: FavoriteModelBD)

    suspend fun insertAlert(alertDB: AlertDB)

    fun getAlerts(): LiveData<MutableList<AlertDB>>

    suspend fun deleteAlert(alertDB: AlertDB)
}