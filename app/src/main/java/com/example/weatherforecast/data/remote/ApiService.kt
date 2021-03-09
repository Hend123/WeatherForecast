package com.example.retrofitandcoroutine.data.remote




import com.example.weatherforecast.pojo.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET("onecall")
   suspend  fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") lang:String,
        @Query("exclude") exclude: String,
        @Query("APPID") appId: String
    ): WeatherModel

}