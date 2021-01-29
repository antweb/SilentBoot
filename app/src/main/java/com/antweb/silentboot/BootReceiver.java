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
 * Restores previous state at boot-up.
 *
 * @author Anton Weber (ant@antweb.me)
 */
public class BootReceiver extends BroadcastReceiver {

    private final int RESTORE_DELAY = 5000;

    /**
     * onReceive method
     *
     * @param context: application context
     * @param intent: sources Intent object
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) &&
                !Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction()) &&
                !Intent.ACTION_MY_PACKAGE_UNSUSPENDED.equals(intent.getAction()))
            return;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        if (settings.getBoolean("enabled", false)) {
            try {
                Thread.sleep(RESTORE_DELAY);
            } catch (InterruptedException e) {
                // do nothing
            }

            int mode = settings.getInt("last_ringer_mode", -1);
            AudioManager audiomanager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);

            // Classic workaround
            if (mode != -1)
                audiomanager.setRingerMode(mode);

            int lastSysVol = settings.getInt("last_sys_vol", -1);
            int lastNotificationVol = settings.getInt("last_notification_vol", -1);

            // Extended workaround
            if (settings.getBoolean("extended", false) && lastSysVol != -1 && lastNotificationVol != -1) {
                audiomanager.setStreamVolume(AudioManager.STREAM_SYSTEM, lastSysVol, 0);
                audiomanager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, lastNotificationVol, 0);
            }

            // Airplane toggle
            if (settings.getBoolean("airplanetoggle", false)) {
                // Load previous state
                int lastAirplaneMode = settings.getInt("last_airplane_mode", -1);

                // Re-enable previous state if necessary
                if (lastAirplaneMode != -1 && lastAirplaneMode != 0) {
                    Settings.System.putInt(context.getContentResolver(),
                            Settings.System.AIRPLANE_MODE_ON, lastAirplaneMode);

                    Intent changeintent = new Intent(
                            Intent.ACTION_AIRPLANE_MODE_CHANGED);
                    changeintent.putExtra("state", lastAirplaneMode);
                    context.sendBroadcast(intent);
                }
            }

            // Android O+ compatibility
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                int lastDnd = settings.getInt("last_dnd", NotificationManager.INTERRUPTION_FILTER_ALL);
                notificationManager.setInterruptionFilter(lastDnd);

                context.startForegroundService(new Intent(context, ShutdownReceiverService.class));
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
