package com.example.weatherforecast.presentation.views.home.location.gps

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.weatherforecast.presentation.views.home.location.gps.LocationLiveDat

class LocationViewModel (application: Application) : AndroidViewModel(application) {
    private val locationData =
        LocationLiveDat(
            application
        )
    fun getLocationData() = locationData
}