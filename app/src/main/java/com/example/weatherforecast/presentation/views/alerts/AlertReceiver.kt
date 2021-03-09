package com.example.weatherforecast.presentation.views.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.provider.Settings
import androidx.core.app.NotificationCompat


class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val notificationHelper =
            NotificationHelper(context,intent)
        val nb: NotificationCompat.Builder =
            notificationHelper.channelNotification
        notificationHelper.manager?.notify(1, nb.build())

    }
}