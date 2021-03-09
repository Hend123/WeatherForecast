package com.example.weatherforecast.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforecast.pojo.WeatherModelBD

@Dao
interface WeatherDao {
    /**
     * Dao of  weather
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)

   suspend fun insertWeather(modelBD: WeatherModelBD)

    @Query("select * from current_weather_table where id = 0")
    fun getWeather(): LiveData<WeatherModelBD>


    @Delete
   suspend fun deleteCurrentWeather(modelBD: WeatherModelBD)

}