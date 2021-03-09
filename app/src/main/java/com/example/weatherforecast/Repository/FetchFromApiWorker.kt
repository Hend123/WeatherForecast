package com.example.weatherforecast.Repository

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.retrofitandcoroutine.data.remote.Constant
import com.example.retrofitandcoroutine.data.remote.RetrofitClient
import com.example.weatherforecast.R
import com.example.weatherforecast.data.remote.ApiHelperImpl
import com.example.weatherforecast.pojo.Alerts
import com.example.weatherforecast.pojo.WeatherModel
import com.example.weatherforecast.pojo.convertTime
import com.example.weatherforecast.presentation.views.alerts.AlertReceiver
import com.example.weatherforecast.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class FetchFromApiWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private var mApiHelper: ApiHelperImpl? = null
    private var weatherMutableLiveDataApi: MutableLiveData<WeatherModel> = MutableLiveData()
    private var lat: String? = null
    private var lon: String? = null
    private var lang: String? = null
    private val mCtx = context
    private var requestCodeList = ArrayList<Int>()
    private val gson = Gson()
    private lateinit var alarmManager: AlarmManager
    private lateinit var alerts: List<Alerts>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    override fun doWork(): Result {
        mApiHelper = ApiHelperImpl(RetrofitClient.getApiService())
        lat = inputData.getString("lat")
        lon = inputData.getString("lon")
        lang = inputData.getString("lang")
        alerts = ArrayList()
        init()
        fetchWeather()
        // val outPut = Data.Builder().putAll(weatherMutableLiveDataApi)
        return Result.success()
    }

    fun init() {
        alarmManager = mCtx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //alerts = ArrayList()
        sharedPreferences = mCtx.getSharedPreferences(
            "prefs",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()

    }

    @SuppressLint("SimpleDateFormat")
    fun setAlarm(alerts: List<Alerts>) {
        Log.v("vm",alerts.size.toString() + " hello" )
        val sdf = java.text.SimpleDateFormat("EEE, h:mm a")
        if (alerts.size > 0) {
            Log.v("wm", "2")
            for (alertItem in alerts) {
                Log.v("wm", "3")
                val now = System.currentTimeMillis()
                if (alertItem.start > now / 1000 ) {
                   Log.v("event",alertItem.event)
                    setNotification(
                        alertItem.start,
                        alertItem.event,
                        "From ${convertTime(alertItem.start)} to ${convertTime(alertItem.end)}"
                    )
                    Log.v("gg", "set alarm")
                }else if(alertItem.end > now / 1000){
                    Log.v("gg", "set alarm")
                    Log.v("time",((alertItem.start + alertItem.end)/2).toString())
                    setNotification(
                       alertItem.end,
                        alertItem.event,
                        "From ${convertTime(alertItem.start)} to ${convertTime(alertItem.end)}"
                    )
          } else {
                    Log.v("gg", "no set alarm")
                }

            }
            val requestCodeJson = gson.toJson(requestCodeList)
            editor.putString("requestsOfAlerts", requestCodeJson)
            editor.commit()
            editor.apply()
        }
    }

    fun fetchWeather(): MutableLiveData<WeatherModel> {


        GlobalScope.launch {
            Dispatchers.IO

            try {
                //"68.3963", "36.9419"
                val response = mApiHelper!!.getWeather(
                    lat!!, lon!!, lang!!, "minutely",
                    Constant.APP_ID
                )
                Log.v("latvm",lat!!)
                // Check if response was successful.
                withContext(Dispatchers.Main) {

                    weatherMutableLiveDataApi.value = response
                    response.alerts?.let {
                        setAlarm(it)
                    }
                    Log.v("wm", alerts.toString() + "hello")
                    setAlarm(ArrayList<Alerts>())


                }
            } catch (e: Exception) {
                Log.i("testError11", " " + e.message)
            }
        }
        return weatherMutableLiveDataApi

    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun setNotification(startTime: Int, event: String, description: String) {
        val intent = Intent(mCtx, AlertReceiver::class.java)
        intent.putExtra("event", event)
        intent.putExtra("desc", description)
        val r = Random()
        val i1 = r.nextInt(99)

        val pendingIntent = PendingIntent.getBroadcast(mCtx, i1, intent, 0)
        requestCodeList.add(i1)
        val alertTime: Long = startTime.toLong()
        Log.v("alertTime", alertTime.toString())
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alertTime, pendingIntent)
       // Toast.makeText(mCtx, R.string.set_alarm, Toast.LENGTH_LONG).show()
        mCtx.registerReceiver(AlertReceiver(), IntentFilter())
    }
}