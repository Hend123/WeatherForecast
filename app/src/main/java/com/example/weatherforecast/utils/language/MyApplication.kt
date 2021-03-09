package com.example.weatherforecast.utils.language

import android.app.Application
import android.util.Log
import androidx.preference.PreferenceManager
import java.util.*

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        updateResources()
    }

    private fun updateResources():Boolean {

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val language = sharedPreferences.getString("lang", "en")

        BaseActivity.dLocale = Locale(language!!)
        return true
    }
}