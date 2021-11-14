package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.core.content.ContextCompat.startForegroundService
import androidx.preference.PreferenceManager

class BootReceiver : BroadcastReceiver() {
    private val RESTORE_DELAY = 5000

    override fun onReceive(context: Context, intent: Intent) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        if (settings.getBoolean("enabled", false)) {
            try {
                Thread.sleep(RESTORE_DELAY.toLong())
            } catch (e: InterruptedException) {
            }
            val mode = settings.getInt("last_ringer_mode", -1)
            val audiomanager = context
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager

            if (mode != -1) audiomanager.ringerMode = mode

            val intent = Intent(context.applicationContext, ShutdownReceiverService::class.java)
            startForegroundService(context.applicationContext, intent)
        }
    }
}
