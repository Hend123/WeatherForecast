package com.example.weatherforecast.data.local

import androidx.room.TypeConverter
import com.example.weatherforecast.pojo.AlertsWeather
import com.example.weatherforecast.pojo.DailyWeather
import com.example.weatherforecast.pojo.HourlyWeather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters {
    @TypeConverter
    fun fromListHourlyToString(listHourly: List<HourlyWeather>): String {
        return Gson().toJson(listHourly)
    }

    @TypeConverter

    fun fromStringToListHourly(stringHourly: String): List<HourlyWeather> {

        val listType: Type = object : TypeToken<List<HourlyWeather>>() {}.type
        return Gson().fromJson(stringHourly, listType)
    }

    @TypeConverter
    fun fromListDailyToString(listDaily: List<DailyWeather>): String {
        return Gson().toJson(listDaily)
    }

    @TypeConverter
    fun fromStringToListDaily(stringDaily: String): List<DailyWeather> {
        val listType: Type = object : TypeToken<List<DailyWeather>>() {}.type
        return Gson().fromJson(stringDaily, listType)
    }
    @TypeConverter
    fun fromListAlertToString(listAlert: List<AlertsWeather>): String {
        return Gson().toJson(listAlert)
    }

    @TypeConverter
    fun fromStringToListAlert(stringAlert: String): List<AlertsWeather> {
        val listType: Type = object : TypeToken<List<AlertsWeather>>() {}.type
        return Gson().fromJson(stringAlert, listType)
    }
}