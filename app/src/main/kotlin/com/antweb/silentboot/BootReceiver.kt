package com.antweb.silentboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.preference.PreferenceManager

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
