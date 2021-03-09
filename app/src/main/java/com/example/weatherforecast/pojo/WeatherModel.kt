package com.example.weatherforecast.pojo

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.utils.GlideApp
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


data class WeatherModel(

    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("timezone_offset") val timezone_offset: Int,
    @SerializedName("current") val current: Current,
    @SerializedName("hourly") val hourly: List<Hourly>,
    @SerializedName("daily") val daily: List<Daily>,
    @SerializedName("alerts") val alerts: List<Alerts>
)

data class LatLon(val lat: String, val lon: String)

data class Current(

    @SerializedName("dt") val dt: Int,
    @SerializedName("sunrise") val sunrise: Int,
    @SerializedName("sunset") val sunset: Int,
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feels_like: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("dew_point") val dew_point: Double,
    @SerializedName("uvi") val uvi: Double,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind_speed") val wind_speed: Double,
    @SerializedName("wind_deg") val wind_deg: Int,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("rain") val rain: Rain
)

data class Rain(

    @SerializedName("1h") val h1: Double
)

data class Weather(

    @SerializedName("id") val id: Int,
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon") val icon: String
)


data class Hourly(

    @SerializedName("dt") val dt: Int,
    @SerializedName("temp") val temp: Double,
    @SerializedName("feels_like") val feels_like: Double,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("dew_point") val dew_point: Double,
    @SerializedName("uvi") val uvi: Double,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("visibility") val visibility: Int,
    @SerializedName("wind_speed") val wind_speed: Double,
    @SerializedName("wind_deg") val wind_deg: Int,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("pop") val pop: Double,
    @SerializedName("rain") val rain: Rain
)

data class Feels_like(

    @SerializedName("day") val day: Double,
    @SerializedName("night") val night: Double,
    @SerializedName("eve") val eve: Double,
    @SerializedName("morn") val morn: Double
)

data class Temp(

    @SerializedName("day") val day: Double,
    @SerializedName("min") val min: Double,
    @SerializedName("max") val max: Double,
    @SerializedName("night") val night: Double,
    @SerializedName("eve") val eve: Double,
    @SerializedName("morn") val morn: Double
)

data class Daily(

    @SerializedName("dt") val dt: Int,
    @SerializedName("sunrise") val sunrise: Int,
    @SerializedName("sunset") val sunset: Int,
    @SerializedName("temp") val temp: Temp,
    @SerializedName("feels_like") val feels_like: Feels_like,
    @SerializedName("pressure") val pressure: Int,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("dew_point") val dew_point: Double,
    @SerializedName("wind_speed") val wind_speed: Double,
    @SerializedName("wind_deg") val wind_deg: Int,
    @SerializedName("weather") val weather: List<Weather>,
    @SerializedName("clouds") val clouds: Int,
    @SerializedName("pop") val pop: Double,
    @SerializedName("rain") val rain: Double,
    @SerializedName("uvi") val uvi: Double
)

data class Alerts(

    @SerializedName("sender_name") val sender_name: String,
    @SerializedName("event") val event: String,
    @SerializedName("start") val start: Int,
    @SerializedName("end") val end: Int,
    @SerializedName("description") val description: String
)

@BindingAdapter("loadImage")
fun loadImage(view: ImageView, imageUrl: String?) {
    imageUrl?.let {
        Picasso.get()
            .load("http://openweathermap.org/img/w/${imageUrl}.png").placeholder(R.drawable.ic_ph).into(view)


    }

}

/**
 * convert unix time to date
 */
@SuppressLint("SimpleDateFormat")
@BindingAdapter("displayDate")
fun unixTimeStampToDate(textView: TextView, untixTimeStamp: Int): String {
    val dateFormat = SimpleDateFormat("EEE,dd/MM")
    val date = Date()
    date.time = untixTimeStamp.toLong() * 1000
    textView.text = dateFormat.format(date)
    return dateFormat.format(date)
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("displayDate_")
fun unixTimeStampToDate_(textView: TextView, untixTimeStamp: Int): String {
    val dateFormat = SimpleDateFormat("EEE, dd/MM/yyyy hh:mm a")
    val date = Date()
    date.time = untixTimeStamp.toLong() * 1000
    textView.text = dateFormat.format(date)
    return dateFormat.format(date)
}
fun convertTime(untixTimeStamp: Int): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm")
    val date = Date()
    date.time = untixTimeStamp.toLong() * 1000
    return dateFormat.format(date)
}

/**
 * convert unix time to time
 */
@SuppressLint("SimpleDateFormat")
@BindingAdapter("displayTime")
fun unixTimeStampToTime(textView: TextView, untixTimeStamp: Int): String {
    val timeFormat = SimpleDateFormat("HH:mm a")
    val date = Date()
    date.time = untixTimeStamp.toLong() * 1000
    textView.text = timeFormat.format(date)
    return timeFormat.format(date)
}

/**
 * get city by lat and lon
 */
@BindingAdapter("city")
fun getCity(textView: TextView, latLon: String?) {
    latLon?.let {
        val latLonSplit = it.split("+")
//        GlobalScope.launch {
//            Dispatchers.IO
        try {
            val addresses: List<Address> =
                Geocoder(textView.context, Locale.getDefault()).getFromLocation(
                    latLonSplit[0].toDouble(), latLonSplit[0].toDouble(), 1
                )
            //  withContext(Dispatchers.Main) {
            if (addresses.size > 0) {
                val city = addresses.get(0).locality
                if (city == null) {
                    textView.setText(addresses.get(0).countryName)
                } else {
                    textView.setText(city)
                }
            } else {
                textView.setText("Unknown place")
            }
        }catch (e:IOException) {

            textView.setText("Unknown place")
        }

          //  }
        //}


    }


}


@BindingAdapter("tempUnits")
fun tempUnits(textView: TextView, temp: Double) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(textView.context)
    val unit = prefs.getString("temp_units", "kelvin")
    if (unit.equals("kelvin")) {
        textView.text = String.format(temp.toString() + "K")
    } else if (unit.equals("celsius")) {
        textView.text = String.format((((temp - 273.15).toFloat()).toString()) + "°C")

    } else {
        textView.text = String.format(((temp * 9 / 5 - 459.67).toFloat()).toString() + "°F")
    }

}

@BindingAdapter("windSpeedUnits")
fun windSpeedUnits(textView: TextView, windSpeed: Double) {
    Log.v("ws", windSpeed.toString() + "m/s")
    val prefs = PreferenceManager.getDefaultSharedPreferences(textView.context)
    val unit = prefs.getString("wind_speed", "metre_sec")
    if (unit.equals("metre_sec")) {
        Log.v("ws", windSpeed.toString() + "m/s")
        textView.text = windSpeed.toFloat().toString() + " m/s"
        Log.v("ws", windSpeed.toString() + "m/s")
    } else if (unit.equals("miles_hour")) {
        textView.text = String.format(((windSpeed * 2.23694).toFloat()).toString() + "mph")

    }

}

@BindingAdapter("visibilityOfProgBar")
fun visibility(progressBar: ProgressBar, isVisible: Boolean) {
    if (isVisible) {
        progressBar.visibility = View.VISIBLE
    } else {
        progressBar.visibility = View.GONE

    }
}


