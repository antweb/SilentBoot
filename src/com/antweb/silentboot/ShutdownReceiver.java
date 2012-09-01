package com.antweb.silentboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.provider.Settings;

/**
 * Handles mute on shutdown.
 * 
 * @author Anton Weber (ant@antweb.me)
 */
public class ShutdownReceiver extends BroadcastReceiver {

    /**
     * Preference file name
     */
    public static final String PREFS_NAME = "silentbootpref";

    /**
     * onReceive method
     *
     * @param context
     * @intent intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.d("SilentBoot","ShutdownReceiver received " +
        // intent.toString());
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("enabled", true)) {
            AudioManager audiomanager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            int lastmode = audiomanager.getRingerMode();

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("lastmode", lastmode);
            editor.commit();
            // Log.d("SilentBoot","Mode " + lastmode + " saved");

            // Log.d("SilentBoot","Trying to set mode " +
            // AudioManager.RINGER_MODE_SILENT);
            audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            // Log.d("SilentBoot","Mode is " +
            // audiomanager.getRingerMode());

            if (settings.getBoolean("airplanetoggle", false)) {
                // Save current state
                int lastairplanemode = Settings.System.getInt(
                        context.getContentResolver(),
                        Settings.System.AIRPLANE_MODE_ON, 0);
                editor.putInt("lastairplanemode", lastairplanemode);

                // Deactivate airplane mode if necessary
                if (lastairplanemode != 0) {
                    // Log.d("SilentBoot","Trying to set airplane mode to 0");
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, 0);
                    // Log.d("SilentBoot","Airplane mode is "
                    // +
                    // Settings.System.getInt(context.getContentResolver(),
                    // Settings.System.AIRPLANE_MODE_ON,
                    // 0));
                }
            }
        } else {
            // Log.d("SilentBoot","Silent Boot disabled");
        }
    }
}
