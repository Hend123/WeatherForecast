package com.example.weatherforecast.utils.view_model_factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.data.local.DatabaseHelper
import com.example.weatherforecast.data.remote.ApiHelper
import com.example.weatherforecast.presentation.view_model.FavoriteViewModel


//@Suppress("UNCHECKED_CAST")
//class FavoriteVMFactory(private val apiHelper: ApiHelper, private val dbHelper: DatabaseHelper
//) :
//    ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
//            return FavoriteViewModel( apiHelper, dbHelper) as T
//        }
//        throw IllegalArgumentException("Unknown class name")
//    }
//}