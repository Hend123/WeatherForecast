package com.example.weatherforecast.utils.language

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import java.util.*
import android.content.res.Configuration


//open class BaseActivity: AppCompatActivity() {
//
//    @SuppressLint("RestrictedApi")
//    override fun attachBaseContext(newBase: Context) {
//// get chosen language from shread preference
//        val localeToSwitchTo = Locale("ar")
//        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, localeToSwitchTo)
//        super.attachBaseContext(localeUpdatedContext)
//    }
//
//}
open class BaseActivity : AppCompatActivity() {

    companion object {
    var dLocale: Locale? = null
    }

    init {
        updateConfig(this)
    }

    fun updateConfig(wrapper: android.view.ContextThemeWrapper) {
        if(dLocale==Locale("") ) // Do nothing if dLocale is null
            return

        Locale.setDefault(dLocale!!)
        val configuration = Configuration()
        configuration.setLocale(dLocale)
        wrapper.applyOverrideConfiguration(configuration)
    }

}