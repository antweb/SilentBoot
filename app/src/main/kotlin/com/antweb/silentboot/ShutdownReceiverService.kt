package com.antweb.silentboot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.IBinder

class ShutdownReceiverService : Service() {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "SERVICE_CHANNEL"
        private const val NOTIFICATION_ID = 1
    }

    private var shutdownReceiver: ShutdownReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val name = getString(R.string.serviceChannelName)
        val descriptionText = getString(R.string.serviceChannelDescription)
        val importance = NotificationManager.IMPORTANCE_MIN

        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID).apply {
            setContentText(getString(R.string.notificationEnabled))
            setSmallIcon(R.drawable.status_gingerbread)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon))
        }.build()

        startForeground(NOTIFICATION_ID, notification)

        return START_STICKY
    }

    override fun onCreate() {
        shutdownReceiver = ShutdownReceiver()
        registerReceiver(shutdownReceiver, IntentFilter(Intent.ACTION_SHUTDOWN))

        super.onCreate()
    }

    override fun onDestroy() {
        if (shutdownReceiver != null) {
            unregisterReceiver(shutdownReceiver)
            shutdownReceiver = null
        }

        super.onDestroy()
    }
}
