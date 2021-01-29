package com.antweb.silentboot;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

public class ShutdownReceiverService extends Service {
    static final String DEFAULT_NOTIFICATION_CHANNEL_ID = "PHONE_SHUTDOWN_RECEIVER_SERVICE";
    static final int ONGOING_NOTIFICATION_ID = 1;

    BroadcastReceiver shutdownReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, notificationIntent, 0);
            Notification notification =
                    new Notification.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.status_gingerbread)
                            .setBadgeIconType(Notification.BADGE_ICON_NONE)
                            .setContentIntent(pendingIntent)
                            .setVisibility(Notification.VISIBILITY_SECRET)
                            .build();

            startForeground(ONGOING_NOTIFICATION_ID, notification);

            shutdownReceiver = new ShutdownReceiver();
            registerReceiver(shutdownReceiver, new IntentFilter(Intent.ACTION_SHUTDOWN));
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            if(nm.getNotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID) != null)
                return;
            CharSequence name = getString(R.string.shutdown_notification_channel_name);
            String description = getString(R.string.shutdown_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(false);
            channel.enableVibration(false);
            channel.setShowBadge(false);
            channel.setBypassDnd(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            channel.setSound(null, null);
            nm.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(shutdownReceiver != null)
            unregisterReceiver(shutdownReceiver);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.cancel(ONGOING_NOTIFICATION_ID);
        }
    }
}
