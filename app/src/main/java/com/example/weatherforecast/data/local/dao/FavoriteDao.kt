package com.example.weatherforecast.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModelBD

@Dao
interface FavoriteDao {
    /**
     * Dao of  favorite
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insertFavoriteWeather(favoriteModelBD: FavoriteModelBD)

    @Query("select * from favorite_table")
     fun getFavoriteWeather(): LiveData<MutableList<FavoriteModelBD>>


    @Delete
   suspend fun deleteFavoriteWeather(modelBD: FavoriteModelBD)

}