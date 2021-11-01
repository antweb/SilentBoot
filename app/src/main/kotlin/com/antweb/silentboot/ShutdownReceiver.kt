package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.media.AudioManager
import android.provider.Settings


class ShutdownReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val settings = PreferenceManager.getDefaultSharedPreferences(context)
        if (settings.getBoolean("enabled", true)) {
            val audiomanager = context
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val editor = settings.edit()

            // Extended workaround
            if (settings.getBoolean("extended", false)) {
                editor.putInt(
                    "last_sys_vol",
                    audiomanager.getStreamVolume(AudioManager.STREAM_SYSTEM)
                )
                editor.putInt(
                    "last_notification_vol",
                    audiomanager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
                )
                audiomanager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0)
                audiomanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0)
            }

            // Classic workaround
            editor.putInt("last_ringer_mode", audiomanager.ringerMode)
            audiomanager.ringerMode = AudioManager.RINGER_MODE_SILENT

            // Airplane mode toggle
            if (settings.getBoolean("airplanetoggle", false)) {
                // Save current state
                val lastAirplaneMode = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.AIRPLANE_MODE_ON, 0
                )
                editor.putInt("last_airplane_mode", lastAirplaneMode)

                // Deactivate airplane mode if necessary
                if (lastAirplaneMode != 0) {
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.AIRPLANE_MODE_ON, 0
                    )
                }
            }
            editor.commit()
        }
    }
}