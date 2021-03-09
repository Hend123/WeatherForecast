package com.example.weatherforecast.presentation.views.alerts


import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.R

class NotificationHelper(base: Context?,intent: Intent) : ContextWrapper(base) {
    private var mManager: NotificationManager? = null
    private lateinit var intent: Intent
    private var witSound:Boolean = intent.getBooleanExtra("sound",true)
    private var desc:String? = null

    @TargetApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
        if(intent.getStringExtra("event") == null){
             desc = "There is Dangerous"
        }else{
            desc = intent.getStringExtra("event")
        }
        channel.enableLights(true)
       channel.setLightColor(Color.RED)
       channel.enableVibration(true)
//        channel.setS
        manager!!.createNotificationChannel(channel)
    }

    val manager: NotificationManager?
        get() {
            if (mManager == null) {
                mManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }
            return mManager
        }


    val channelNotification: NotificationCompat.Builder

        get() = NotificationCompat.Builder(
            applicationContext,
            channelID
        ).setContentTitle("Alert")
            .setContentText(desc+intent.getStringExtra("desc"))
            .setSmallIcon(R.drawable.ic_bell)

    companion object {
        const val channelID = "channelID"
        const val channelName = "Channel Name"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.intent = intent
            createChannel()
        }
    }
}