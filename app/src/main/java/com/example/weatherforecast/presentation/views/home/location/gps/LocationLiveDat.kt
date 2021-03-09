package com.example.weatherforecast.presentation.views.home.location.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import com.example.weatherforecast.pojo.LatLon
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class LocationLiveDat(var context: Context) : LiveData<LatLon>() {
    private var fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    /**
     * use setLocationData for
     * map location data(Longitude and Latitude) to LatLon,
     * which in turn is set as the value of LocationLiveData.
     * value is a property inherited from LiveData.
     */
    private fun setLocationData(location: Location) {
        value = LatLon(
            location.longitude.toString(),
            location.latitude.toString()
        )
    }

    /**
     * To receive location update with FusedLocationProviderClient,
     * we need to create LocationRequest, where we specify the interval and accuracy of the location update
     */
    companion object {

        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    /**
     * and LocationCallback which will be invoked when we have location update from FusedLocationProviderClient.
     */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            for (location in locationResult.locations) {
                setLocationData(location)
            }
        }
    }

    /**
     * We create method startLocationUpdates to call fusedLocationClient.requestLocationUpdates by passing
     * locationRequestobject and locationCallback object.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    //called when the lifecycle owner(LocationActivity) is either paused, stopped or destroyed.
    override fun onInactive() {
        super.onInactive()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    //called when the lifecycle owner(LocationActivity) is either started or resumed

    override fun onActive() {
        super.onActive()
     //   getLastLocation()
        startLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
  fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.also {
                    setLocationData(it)
                }
            }
    }


}
