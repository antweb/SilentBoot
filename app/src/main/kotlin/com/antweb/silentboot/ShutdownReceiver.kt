package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.preference.PreferenceManager

class ShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        if (settings.getBoolean(PreferenceKey.ENABLED.key, true)) {
            val audiomanager = context
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val editor = settings.edit()

            editor.putInt(PreferenceKey.LAST_RINGER_MODE.key, audiomanager.ringerMode)
            audiomanager.ringerMode = AudioManager.RINGER_MODE_SILENT

            // Stick to commit to be on the safe side?
            editor.commit()
        }
    }
}
