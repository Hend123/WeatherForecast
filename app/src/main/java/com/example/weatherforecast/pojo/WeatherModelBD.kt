package com.example.weatherforecast.pojo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "current_weather_table")

data class WeatherModelBD(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    @ColumnInfo
    val dt: Int,
    val temp: Double,
    val pressure: Int,
    val humidity: Int,
    val clouds: Int,
    val wind_speed: Double,
    val icon: String,
    val desc: String,
    val city: String,
    val timeZone:String,
    val hourlyWeather: List<HourlyWeather>,
    val dailyWeather: List<DailyWeather>,
    val alertsWeather: List<AlertsWeather>
)
data class AlertsWeather(

     val sender_name: String,
    val event: String,
    val start: Int,
   val end: Int,
    val description: String
)

data class HourlyWeather(

    val dt: Int,
    val temp: Double,
    val icon: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dt)
        parcel.writeDouble(temp)
        parcel.writeString(icon)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HourlyWeather> {
        override fun createFromParcel(parcel: Parcel): HourlyWeather {
            return HourlyWeather(parcel)
        }

        override fun newArray(size: Int): Array<HourlyWeather?> {
            return arrayOfNulls(size)
        }
    }
}

data class DailyWeather(

    val dt: Int,
    val min: Double,
    val max: Double,
    val icon: String,
    val desc: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dt)
        parcel.writeDouble(min)
        parcel.writeDouble(max)
        parcel.writeString(icon)
        parcel.writeString(desc)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DailyWeather> {
        override fun createFromParcel(parcel: Parcel): DailyWeather {
            return DailyWeather(parcel)
        }

        override fun newArray(size: Int): Array<DailyWeather?> {
            return arrayOfNulls(size)
        }
    }
}