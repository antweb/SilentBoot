package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.preference.PreferenceManager

class ShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isEnabled = sharedPrefs.getBoolean(PreferenceKey.ENABLED.key, true)

        if (!isEnabled) {
            return
        }

        val audioManager = context
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val editor = sharedPrefs.edit()

        editor.putInt(PreferenceKey.LAST_RINGER_MODE.key, audioManager.ringerMode)
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

        // Stick to commit to be on the safe side?
        editor.commit()
    }
}
