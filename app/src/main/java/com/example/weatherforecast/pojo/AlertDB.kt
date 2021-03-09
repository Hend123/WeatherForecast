package com.example.weatherforecast.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "alert_table")
data class AlertDB(
   // @PrimaryKey(autoGenerate = true)

    @ColumnInfo
    val requestCode:Int,
    val event: String,
    val start: String,
    val description: String,
    val status:Boolean
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}