package com.example.weatherforecast.Repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.retrofitandcoroutine.data.remote.Constant
import com.example.weatherforecast.data.local.DatabaseHelper
import com.example.weatherforecast.data.remote.ApiHelper
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModel
import com.example.weatherforecast.pojo.WeatherModelBD
import com.example.weatherforecast.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Repo() : IRepo {
    private var weatherMutableLiveDataApi: MutableLiveData<Resource<WeatherModel>>
    private var weatherMutableLiveDataBD: LiveData<Resource<WeatherModelBD>>
//constructor( apiHelper: ApiHelper,  dbHelper: DatabaseHelper):this("","","",apiHelper,dbHelper){
//    this.apiHelper = apiHelper
//    this.dbHelper = dbHelper
//}
    init {

        this.weatherMutableLiveDataApi = MutableLiveData()
        this.weatherMutableLiveDataBD = MutableLiveData()
    }

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
    }
    public fun setDataFromDB(apiHelper: ApiHelper, dbHelper: DatabaseHelper){
        mApiHelper = apiHelper
        mDbHelper = dbHelper
    }

    override fun fetchWeather(): MutableLiveData<Resource<WeatherModel>> {
        Log.v("repo1",mLat!!)

        GlobalScope.launch {
            Dispatchers.IO
            weatherMutableLiveDataApi.postValue(Resource.loading(null))
            try {
                val response = mApiHelper!!.getWeather(
                    mLat!!, mLon!!, mLanguage!!, "minutely",
                    Constant.APP_ID
                )
                Log.v("repo2",mLat!!)
                // Check if response was successful.
                withContext(Dispatchers.Main) {

                        Log.i("test1", " " + weatherMutableLiveDataApi.value)
                        weatherMutableLiveDataApi.postValue(Resource.success(response))
                        Log.i("test2", " " + weatherMutableLiveDataApi.value)

                }
            } catch (e: Exception) {
                Log.i("testError", " " + e.message)
                weatherMutableLiveDataApi.postValue(Resource.error(e.toString(), null))
            }
        }
        Log.i("test3", " " + weatherMutableLiveDataApi.value)
        return weatherMutableLiveDataApi

    }

    override suspend fun addweatherInDB(weatherModelBD: WeatherModelBD) {
        Log.v("mm","1")
        Log.v("llllllllll",weatherModelBD.toString())
        GlobalScope.launch(Dispatchers.IO) {
            Log.v("mm","2")
            mDbHelper!!.insertWeather(weatherModelBD)
            withContext(Dispatchers.Main){
                Log.v("llllllllll",weatherModelBD.toString())
            }
            Log.v("mm","3")
        }


    }

    override  fun getweatherFromDB(): LiveData<WeatherModelBD> {

        return mDbHelper!!.getWeather()
    }

    override  fun addFavoriteDB(favoriteModelBD: FavoriteModelBD) {
        GlobalScope.launch(Dispatchers.IO) {
            mDbHelper!!.insertFavoriteWeather(favoriteModelBD)
        }
    }

    override   fun getFavoriteFromDB(): LiveData<MutableList<FavoriteModelBD>> {

        return mDbHelper!!.getFavoriteWeather()
    }
    override fun deleteFavItem(favoriteModelBD: FavoriteModelBD){
        GlobalScope.launch {
            Dispatchers.IO
            mDbHelper!!.deleteFavoriteWeather(favoriteModelBD)
        }

    }

    override fun addAlert(alertDB: AlertDB) {
        GlobalScope.launch {
            Dispatchers.IO
            mDbHelper!!.insertAlert(alertDB)
        }
    }

    override fun getAlertFromDB(): LiveData<MutableList<AlertDB>> {
        return mDbHelper!!.getAlerts()
    }

    override fun deleteAlertItem(alertDB: AlertDB) {
        GlobalScope.launch {
            Dispatchers.IO
            mDbHelper!!.deleteAlert(alertDB)
        }
    }
}
