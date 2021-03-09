package com.example.weatherforecast.presentation.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherforecast.Repository.Repo
import com.example.weatherforecast.data.local.DatabaseHelper
import com.example.weatherforecast.data.remote.ApiHelper
import com.example.weatherforecast.pojo.WeatherModel
import com.example.weatherforecast.pojo.WeatherModelBD
import com.example.weatherforecast.utils.Resource

class WeatherViewModel() : ViewModel(), IWeatherViewModel {
    private var repo: Repo
    private var mLat: String? = null
    private var mLon: String? = null
    private var mLanguage: String? = null
    private var mApiHelper: ApiHelper? = null
    private var mDbHelper: DatabaseHelper? = null
    public fun setData(
        lat: String, lon: String, language: String,
        apiHelper: ApiHelper, dbHelper: DatabaseHelper
    ) {
        mLat = lat
        mLon = lon
        mLanguage = language
        mApiHelper = apiHelper
        mDbHelper = dbHelper
        repo.setData(mLat!!,mLon!!,mLanguage!!,mApiHelper!!,mDbHelper!!)
        weatherMLVMApi = repo.fetchWeather()


    }

    var weatherMLVMApi: MutableLiveData<Resource<WeatherModel>>
    var weatherMLVMRoom: LiveData<WeatherModelBD>

    init {
        repo = Repo()
        weatherMLVMApi = MutableLiveData()
        weatherMLVMRoom = MutableLiveData()
    }


    override fun fetchWeather(): MutableLiveData<Resource<WeatherModel>> {
        return weatherMLVMApi
    }

    override suspend fun addweather(weatherModelBD: WeatherModelBD) {
        Log.v("mm", "4")
        repo.addweatherInDB(weatherModelBD)
    }

    override fun getweatherFromDB(): LiveData<WeatherModelBD> {

        return repo.getweatherFromDB()
    }
}