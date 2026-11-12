package com.antweb.silentboot

import android.app.NotificationManager
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Context.NOTIFICATION_SERVICE
import android.media.AudioManager
import android.util.Log
import androidx.preference.PreferenceManager

private const val TAG = "Restore"

fun restoreSavedSettings(context: Context) {
    val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)

    val lastRingerMode = sharedPrefs.getInt(PreferenceKey.LAST_RINGER_MODE.key, -1)
    val lastDnd = sharedPrefs.getInt(PreferenceKey.LAST_DND_MODE.key, -1)

    val audioManager = context.getSystemService(AUDIO_SERVICE) as AudioManager
    val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    Log.i(TAG, "Restoring. lastRingerMode: $lastRingerMode - lastDnd: $lastDnd - isVolumeFixed: ${audioManager.isVolumeFixed}")

    if (!notificationManager.isNotificationPolicyAccessGranted) {
        // Should only happen if the permission is revoked manually
        Log.e(TAG, "Notification Policy permission not granted. Aborting.")
        return
    }

    if (lastDnd != -1) {
        notificationManager.setInterruptionFilter(lastDnd)
    }

    if (lastRingerMode != -1) {
        audioManager.ringerMode = lastRingerMode
    }

    Log.i(TAG, "Restore complete. ringerMode: ${audioManager.ringerMode} - currentInterruptionFilter: ${notificationManager.currentInterruptionFilter}")
}
