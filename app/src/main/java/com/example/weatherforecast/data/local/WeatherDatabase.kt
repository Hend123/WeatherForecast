package com.example.weatherforecast.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.data.local.dao.AlertDao
import com.example.weatherforecast.data.local.dao.FavoriteDao
import com.example.weatherforecast.data.local.dao.WeatherDao
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModelBD


@Database(
    entities = arrayOf(WeatherModelBD::class, FavoriteModelBD::class, AlertDB::class),
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun alertDao(): AlertDao

    companion object {
        private var instance: WeatherDatabase? = null

        @Synchronized
        fun getInstance(context: Context): WeatherDatabase {
            instance
                ?: synchronized(this) {
                    instance = Room.databaseBuilder(
                        context,
                        WeatherDatabase::class.java, "weather_database"
                    ).build()
                }



            return instance!!
        }
    }
}