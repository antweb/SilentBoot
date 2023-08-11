//package com.antweb.silentboot
//
//import android.app.NotificationManager
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.media.AudioManager
//import androidx.core.content.ContextCompat.startForegroundService
//import androidx.preference.PreferenceManager
//
//class BootReceiver : BroadcastReceiver() {
//    private val restoreDelay = 5000
//
//    override fun onReceive(context: Context, intent: Intent) {
//        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
//        val isEnabled = sharedPrefs.getBoolean(PreferenceKey.ENABLED.key, false)
//
//        if (!isEnabled) {
//            return
//        }
//
//        try {
//            Thread.sleep(restoreDelay.toLong())
//        } catch (e: InterruptedException) {
//        }
//
//        val lastRingerMode = sharedPrefs.getInt(PreferenceKey.LAST_RINGER_MODE.key, -1)
//        val lastDnd = sharedPrefs.getInt(PreferenceKey.LAST_DND_MODE.key, -1)
//
//        val audioManager = context
//            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        val notificationManager = context
//            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        if (!notificationManager.isNotificationPolicyAccessGranted) {
//            // Should only happen if the permission is revoked manually
//            return
//        }
//
//        if (lastRingerMode != -1) {
//            audioManager.ringerMode = lastRingerMode
//        }
//        if (lastDnd != -1) {
//            notificationManager.setInterruptionFilter(lastDnd)
//        }
//
//        val serviceIntent = Intent(context.applicationContext, ShutdownReceiverService::class.java)
//        startForegroundService(context.applicationContext, serviceIntent)
//    }
//}
