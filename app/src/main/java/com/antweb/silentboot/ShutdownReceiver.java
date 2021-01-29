package com.antweb.silentboot;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;

/**
 * Handles mute on shutdown.
 *
 * @author Anton Weber (ant@antweb.me)
 */
public class ShutdownReceiver extends BroadcastReceiver {

    /**
     * onReceive method
     *
     * @param context: application context
     * @param intent: sources Intent object
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Intent.ACTION_SHUTDOWN.equals(intent.getAction()))
            return;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (settings.getBoolean("enabled", true)) {
            AudioManager audiomanager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);

            SharedPreferences.Editor editor = settings.edit();

            // Extended workaround
            if (settings.getBoolean("extended", false)) {
                editor.putInt("last_sys_vol", audiomanager.getStreamVolume(AudioManager.STREAM_SYSTEM));
                editor.putInt("last_notification_vol", audiomanager.getStreamVolume(AudioManager.STREAM_NOTIFICATION));

                audiomanager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
                audiomanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
            }

            // Classic workaround
            editor.putInt("last_ringer_mode", audiomanager.getRingerMode());
            audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            // Airplane mode toggle
            if (settings.getBoolean("airplanetoggle", false)) {
                // Save current state
                int lastAirplaneMode = Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0);
                editor.putInt("last_airplane_mode", lastAirplaneMode);

                // Deactivate airplane mode if necessary
                if (lastAirplaneMode != 0) {
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, 0);
                }
            }

            // Android O+ compatibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                editor.putInt("last_dnd", notificationManager.getCurrentInterruptionFilter());
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            }

            editor.apply();
        }
    }
}