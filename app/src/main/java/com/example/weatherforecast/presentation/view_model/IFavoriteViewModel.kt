package com.example.weatherforecast.presentation.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModelBD

interface IFavoriteViewModel {
    suspend fun addFavoriteDB(favoriteModelBD: FavoriteModelBD)
    fun getFavoriteFromDB(): LiveData<MutableList<FavoriteModelBD>>
     fun deleteFavItem(favoriteModelBD: FavoriteModelBD)
}