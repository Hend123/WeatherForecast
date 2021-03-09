package com.example.weatherforecast.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherforecast.Repository.Repo
import com.example.weatherforecast.data.local.DatabaseHelper
import com.example.weatherforecast.data.remote.ApiHelper
import com.example.weatherforecast.pojo.AlertDB
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModelBD

class AlertViewModel ()
    : ViewModel(), IAlertViewModel {
    private var repo: Repo
    private var mApiHelper: ApiHelper? = null
    private var mDbHelper: DatabaseHelper? = null
    var alertMLVMRoom: LiveData<List<AlertDB>>

    init {
        repo = Repo()
        alertMLVMRoom = MutableLiveData()
    }
    public fun setDataFromDB(apiHelper: ApiHelper, dbHelper: DatabaseHelper){
        mApiHelper = apiHelper
        mDbHelper = dbHelper
        repo.setDataFromDB(mApiHelper!!,mDbHelper!!)
    }

    override suspend fun addAlert(alertDB: AlertDB) {
        repo.addAlert(alertDB)
    }

    override fun getAlertFromDB(): LiveData<MutableList<AlertDB>> {

        return repo.getAlertFromDB()
    }
    override fun deleteAlertItem(alertDB: AlertDB){
        repo.deleteAlertItem(alertDB)
    }
}