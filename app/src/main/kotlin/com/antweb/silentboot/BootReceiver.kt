package com.antweb.silentboot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.content.ContextCompat.startForegroundService
import androidx.preference.PreferenceManager

class BootReceiver : BroadcastReceiver() {
    private val restoreDelay = 5000

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isEnabled = sharedPrefs.getBoolean(PreferenceKey.ENABLED.key, false)

        if (!isEnabled) {
            return
        }

        // Background audio hardening introduced in Android 17 prevents us from using the old mechanism
        // Can't have nice things...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CINNAMON_BUN) {
            handleNew(context)
        } else {
            handleOld(context)
        }

        val serviceIntent = Intent(context.applicationContext, ShutdownReceiverService::class.java)
        startForegroundService(context.applicationContext, serviceIntent)
    }

    private fun handleNew(context: Context) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, RestoreActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID_ACTIONS,
            context.getString(R.string.actionsChannelName),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = context.getString(R.string.actionsChannelDescription)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID_ACTIONS).apply {
            setContentTitle(context.getString(R.string.notificationTitleRestore))
            setContentText(context.getString(R.string.notificationTextRestore))
            setSmallIcon(R.drawable.notification_icon)
            setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
            setContentIntent(pendingIntent)
        }.build()

        notificationManager.notify(NOTIFICATION_ID_RESTORE, notification)
    }

    private fun handleOld(context: Context) {
        try {
            Thread.sleep(restoreDelay.toLong())
        } catch (_: InterruptedException) {
        }

        restoreSavedSettings(context)
    }
}
