package com.antweb.silentboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
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
     * @param context
     * @intent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (settings.getBoolean("enabled", true)) {
            AudioManager audiomanager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            int lastmode = audiomanager.getRingerMode();

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("lastmode", lastmode);
            editor.commit();

            audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            if (settings.getBoolean("airplanetoggle", false)) {
                // Save current state
                int lastairplanemode = Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0);
                editor.putInt("lastairplanemode", lastairplanemode);

                // Deactivate airplane mode if necessary
                if (lastairplanemode != 0) {
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, 0);
                }
            }
        }
    }
}
