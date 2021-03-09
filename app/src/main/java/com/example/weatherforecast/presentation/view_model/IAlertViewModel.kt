package com.example.weatherforecast.presentation.view_model

import androidx.lifecycle.LiveData
import com.example.weatherforecast.pojo.AlertDB

interface IAlertViewModel {
    suspend fun addAlert(alertDB: AlertDB)
    fun getAlertFromDB(): LiveData<MutableList<AlertDB>>
    fun deleteAlertItem(alertDB: AlertDB)
}