package com.example.weatherforecast.pojo

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity(tableName = "favorite_table")

    data class FavoriteModelBD(
    @PrimaryKey(autoGenerate = true)
   // @PrimaryKey val id: Int,
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
    val hourlyWeather: List<HourlyWeather>,
    val dailyWeather: List<DailyWeather>
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(HourlyWeather)!!,
        parcel.createTypedArrayList(DailyWeather)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(dt)
        parcel.writeDouble(temp)
        parcel.writeInt(pressure)
        parcel.writeInt(humidity)
        parcel.writeInt(clouds)
        parcel.writeDouble(wind_speed)
        parcel.writeString(icon)
        parcel.writeString(desc)
        parcel.writeString(city)
        parcel.writeTypedList(hourlyWeather)
        parcel.writeTypedList(dailyWeather)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoriteModelBD> {
        override fun createFromParcel(parcel: Parcel): FavoriteModelBD {
            return FavoriteModelBD(parcel)
        }

        override fun newArray(size: Int): Array<FavoriteModelBD?> {
            return arrayOfNulls(size)
        }
    }
}