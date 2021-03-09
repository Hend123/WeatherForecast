package com.example.weatherforecast.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherforecast.pojo.AlertDB
@Dao
interface AlertDao {
    /**
     * Dao of  alert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insertAlert(alertDB: AlertDB)

    @Query("select * from alert_table")
    fun getAlerts(): LiveData<MutableList<AlertDB>>


    @Delete
    suspend fun deleteAlert(alertDB: AlertDB)
}