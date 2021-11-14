package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.core.content.ContextCompat.startForegroundService
import androidx.preference.PreferenceManager

class BootReceiver : BroadcastReceiver() {
    private val restoreDelay = 5000

    override fun onReceive(context: Context, intent: Intent) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isEnabled = sharedPrefs.getBoolean(PreferenceKey.ENABLED.key, false)

        if (!isEnabled) {
            return
        }

        try {
            Thread.sleep(restoreDelay.toLong())
        } catch (e: InterruptedException) {
        }

        val mode = sharedPrefs.getInt(PreferenceKey.LAST_RINGER_MODE.key, -1)
        val audioManager = context
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (mode != -1) {
            audioManager.ringerMode = mode
        }

        val serviceIntent = Intent(context.applicationContext, ShutdownReceiverService::class.java)
        startForegroundService(context.applicationContext, serviceIntent)
    }
}
