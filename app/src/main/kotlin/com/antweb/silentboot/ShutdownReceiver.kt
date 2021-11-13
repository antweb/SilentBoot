package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.preference.PreferenceManager

class ShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        if (settings.getBoolean("enabled", true)) {
            val audiomanager = context
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val editor = settings.edit()

            editor.putInt("last_ringer_mode", audiomanager.ringerMode)
            audiomanager.ringerMode = AudioManager.RINGER_MODE_SILENT

            editor.commit()
        }
    }
}
