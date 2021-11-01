package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.preference.PreferenceManager
import android.provider.Settings

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

            // Classic workaround
            if (mode != -1) audiomanager.ringerMode = mode
            val lastSysVol = settings.getInt("last_sys_vol", -1)
            val lastNotificationVol = settings.getInt("last_notification_vol", -1)

            // Extended workaround
            if (settings.getBoolean(
                    "extended",
                    false
                ) && lastSysVol != -1 && lastNotificationVol != -1
            ) {
                audiomanager.setStreamVolume(AudioManager.STREAM_SYSTEM, lastSysVol, 0)
                audiomanager.setStreamVolume(
                    AudioManager.STREAM_NOTIFICATION,
                    lastNotificationVol,
                    0
                )
            }

            // Airplane toggle
            if (settings.getBoolean("airplanetoggle", false)) {
                // Load previous state
                val lastAirplaneMode = settings.getInt("last_airplane_mode", -1)

                // Re-enable previous state if necessary
                if (lastAirplaneMode != -1 && lastAirplaneMode != 0) {
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.AIRPLANE_MODE_ON, lastAirplaneMode
                    )
                    val changeintent = Intent(
                        Intent.ACTION_AIRPLANE_MODE_CHANGED
                    )
                    changeintent.putExtra("state", lastAirplaneMode)
                    context.sendBroadcast(intent)
                }
            }

            // Start notification
            if (settings.getBoolean("compatibility", false)) {
                val intent_notify = Intent(context, MainActivity::class.java)
                intent_notify.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent_notify.putExtra("notify", true)
                context.startActivity(intent_notify)
            }
        }
    }
}
