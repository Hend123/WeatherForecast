package com.example.weatherforecast.utils

import android.content.Context
import android.net.ConnectivityManager


object CheckInternet {

    fun hasNetworkAvailable(context: Context): Boolean {
        var isConnected = false
        val service = Context.CONNECTIVITY_SERVICE
        val manager = context.getSystemService(service) as ConnectivityManager?
        val networkInfo = manager?.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            val wifiConnected = networkInfo.type == ConnectivityManager.TYPE_WIFI
            val mobileConnected = networkInfo.type == ConnectivityManager.TYPE_MOBILE
            if (wifiConnected || mobileConnected) {
                isConnected = true
            }
        } else {
            isConnected = false
        }
        return isConnected
    }

}


//fun hasInternetConnected(context: Context): Boolean {
//    if (hasNetworkAvailable(context)) {
//        try {
//            val connection = URL(Constants.REACHABILITY_SERVER).openConnection() as HttpURLConnection
//            connection.setRequestProperty("User-Agent", "ConnectionTest")
//            connection.setRequestProperty("Connection", "close")
//            connection.connectTimeout = 1000 // configurable
//            connection.connect()
//            Logger.d(classTag, "hasInternetConnected: ${(connection.responseCode == 200)}")
//            return (connection.responseCode == 200)
//        } catch (e: IOException) {
//            Logger.e(classTag, "Error checking internet connection", e)
//        }
//    } else {
//        Log.w(classTag, "No network available!")
//    }
//    Log.d(classTag, "hasInternetConnected: false")
//    return false
//
//}