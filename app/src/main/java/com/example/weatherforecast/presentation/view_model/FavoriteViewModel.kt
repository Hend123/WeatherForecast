package com.example.weatherforecast.presentation.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherforecast.Repository.Repo
import com.example.weatherforecast.data.local.DatabaseHelper
import com.example.weatherforecast.data.remote.ApiHelper
import com.example.weatherforecast.pojo.FavoriteModelBD
import com.example.weatherforecast.pojo.WeatherModelBD

class FavoriteViewModel()
    : ViewModel(), IFavoriteViewModel {
    private var repo: Repo
    private var mApiHelper: ApiHelper? = null
    private var mDbHelper: DatabaseHelper? = null
    var favoriteMLVMRoom: LiveData<List<WeatherModelBD>>

    init {
        repo = Repo()
        favoriteMLVMRoom = MutableLiveData()
    }
    public fun setDataFromDB(apiHelper: ApiHelper, dbHelper: DatabaseHelper){
        mApiHelper = apiHelper
        mDbHelper = dbHelper
        repo.setDataFromDB(mApiHelper!!,mDbHelper!!)
    }

    override suspend fun addFavoriteDB(favoriteModelBD: FavoriteModelBD) {
        repo.addFavoriteDB(favoriteModelBD)
    }

    override  fun getFavoriteFromDB(): LiveData<MutableList<FavoriteModelBD>> {

        return repo.getFavoriteFromDB()
    }
    override fun deleteFavItem(favoriteModelBD: FavoriteModelBD){
        repo.deleteFavItem(favoriteModelBD)
    }
}