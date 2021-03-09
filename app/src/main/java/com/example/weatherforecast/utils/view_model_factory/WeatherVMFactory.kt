package com.example.weatherforecast.utils.view_model_factory

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.data.local.DatabaseHelper
import com.example.weatherforecast.data.remote.ApiHelper
import com.example.weatherforecast.presentation.view_model.FavoriteViewModel
import com.example.weatherforecast.presentation.view_model.WeatherViewModel

//@Suppress("UNCHECKED_CAST")
//class WeatherVMFactory(private val lat: String, private val lon: String, private val language:String,
//                       private val apiHelper: ApiHelper, private val dbHelper: DatabaseHelper
//) :
//    ViewModelProvider.Factory {
//
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        Log.v("vmf1",lat)
//        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
//            Log.v("vmf2",lat)
//            return WeatherViewModel(lat, lon, language, apiHelper, dbHelper) as T
//        }
//        throw IllegalArgumentException("Unknown class name")
//    }
//}