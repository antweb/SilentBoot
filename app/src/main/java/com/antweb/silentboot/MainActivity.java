package com.antweb.silentboot;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MainActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    static final String PREF_KEY_ENABLED = "enabled";
    static final String PREF_KEY_EXTENDED = "extended";
    static final String PREF_KEY_COMPAT = "compatibility";
    static final String PREF_KEY_AIRPLANETOGGLE = "airplanetoggle";

    public static final int NOTIFICATION_ID = 1;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getBoolean("notify")) {
            startNotification();
            finish();
        }

        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isEnabled = sharedPrefs.getBoolean("enabled", false);

        if (isEnabled) {
            checkPermissions();
        }

        setAdvancedEnabled(isEnabled);
        setSummaries();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_KEY_ENABLED)) {
            CheckBoxPreference prefEnabled = (CheckBoxPreference) findPreference(PREF_KEY_ENABLED);
            boolean enabled = prefEnabled.isChecked();

            if (enabled) {
                checkPermissions();

                prefEnabled.setSummary(R.string.summaryEnabled);
                setAdvancedEnabled(true);
                if (sharedPreferences.getBoolean("compatibility", false))
                    startNotification();
            } else {
                prefEnabled.setSummary(R.string.summaryDisabled);
                setAdvancedEnabled(false);
                stopNotification();
            }
        } else if (key.equals(PREF_KEY_EXTENDED)) {
            CheckBoxPreference prefExtended = (CheckBoxPreference) findPreference(PREF_KEY_EXTENDED);
            boolean extended = prefExtended.isChecked();

            if (extended) {
                prefExtended.setSummary(R.string.summaryExtendedEnabled);
            } else {
                prefExtended.setSummary(R.string.summaryExtendedDisabled);
            }
        } else if (key.equals(PREF_KEY_COMPAT)) {
            CheckBoxPreference prefCompat = (CheckBoxPreference) findPreference(PREF_KEY_COMPAT);
            boolean compat = prefCompat.isChecked();

            if (compat) {
                prefCompat.setSummary(R.string.summaryCompatEnabled);
                startNotification();
            } else {
                prefCompat.setSummary(R.string.summaryCompatDisabled);
                stopNotification();
            }
        } else if (key.equals(PREF_KEY_AIRPLANETOGGLE)) {
            CheckBoxPreference prefAirplaneToggle = (CheckBoxPreference) findPreference(PREF_KEY_AIRPLANETOGGLE);
            boolean airplaneToggle = prefAirplaneToggle.isChecked();

            if (airplaneToggle) {
                prefAirplaneToggle.setSummary(R.string.summaryAirplaneToggleEnabled);
            } else {
                prefAirplaneToggle.setSummary(R.string.summaryAirplaneToggleDisabled);
            }
        }
    }

    /**
     * Disable or enable advanced settings based on enabled state
     *
     * @param enabled Enabled state
     */
    @SuppressWarnings("deprecation")
    protected void setAdvancedEnabled(boolean enabled) {
        Preference prefExtended = findPreference(PREF_KEY_EXTENDED);
        Preference prefCompat = findPreference(PREF_KEY_COMPAT);
        Preference prefAirplaneToggle = findPreference(PREF_KEY_AIRPLANETOGGLE);

        if (enabled) {
            prefExtended.setEnabled(true);
            prefCompat.setEnabled(true);
            prefAirplaneToggle.setEnabled(true);
        } else {
            prefExtended.setEnabled(false);
            prefCompat.setEnabled(false);
            prefAirplaneToggle.setEnabled(false);
        }
    }

    @SuppressWarnings("deprecation")
    protected void setSummaries() {
        Preference prefEnabled = findPreference(PREF_KEY_ENABLED);
        Preference prefExtended = findPreference(PREF_KEY_EXTENDED);
        Preference prefCompat = findPreference(PREF_KEY_COMPAT);
        Preference prefAirplaneToggle = findPreference(PREF_KEY_AIRPLANETOGGLE);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPrefs.getBoolean("enabled", false))
            prefEnabled.setSummary(R.string.summaryEnabled);
        else
            prefEnabled.setSummary(R.string.summaryDisabled);

        if (sharedPrefs.getBoolean("extended", false))
            prefExtended.setSummary(R.string.summaryExtendedEnabled);
        else
            prefExtended.setSummary(R.string.summaryExtendedDisabled);

        if (sharedPrefs.getBoolean("compatibility", false))
            prefCompat.setSummary(R.string.summaryCompatEnabled);
        else
            prefCompat.setSummary(R.string.summaryCompatDisabled);

        if (sharedPrefs.getBoolean("airplanetoggle", false))
            prefAirplaneToggle.setSummary(R.string.summaryAirplaneToggleEnabled);
        else
            prefAirplaneToggle.setSummary(R.string.summaryAirplaneToggleDisabled);
    }

    /**
     * Start the notification
     */
    @SuppressWarnings("deprecation")
    protected void startNotification() {
        Notification.Builder builder = new Notification.Builder(MainActivity.this);

        builder.setContentTitle(getString(R.string.notificationTitle));
        builder.setContentText(getString(R.string.notificationEnabled));
        builder.setSmallIcon(R.drawable.status_gingerbread);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.build();

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.build());
    }

    /**
     * Stop the notification
     */
    protected void stopNotification() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }

    /**
     * Check permissions
     */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nm.isNotificationPolicyAccessGranted()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.permissionDialogText));

            builder.setPositiveButton(getString(R.string.permissionDialogEnable), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });

            builder.setNegativeButton(getString(R.string.permissionDialogCancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            builder.show();
        }
    }
}
