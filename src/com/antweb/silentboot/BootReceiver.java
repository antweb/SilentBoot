package com.antweb.silentboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.provider.Settings;

/**
 * Restores previous state at boot-up.
 * 
 * @author Anton Weber (ant@antweb.me)
 */
public class BootReceiver extends BroadcastReceiver {

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
            int mode = settings.getInt("lastmode",
                    AudioManager.RINGER_MODE_SILENT);
            AudioManager audiomanager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            audiomanager.setRingerMode(mode);

            // Airplane toggle
            if (settings.getBoolean("airplanetoggle", false)) {
                // Load previous state
                int lastairplanemode = settings.getInt("lastairplanemode", -1);

                // Re-enable previous state if necessary
                if (lastairplanemode != -1 && lastairplanemode != 0) {
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, lastairplanemode);

                    Intent changeintent = new Intent(
                            Intent.ACTION_AIRPLANE_MODE_CHANGED);
                    changeintent.putExtra("state", lastairplanemode);
                    context.sendBroadcast(intent);
                }
            }

            // Start notification
            if (settings.getBoolean("compatibility", false)) {
                Intent intent_notify = new Intent(context, MainActivity.class);
                intent_notify.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_notify.putExtra("notify", true);
                context.startActivity(intent_notify);
            }
        }
    }
}
