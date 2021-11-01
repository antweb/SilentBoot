package com.antweb.silentboot

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.provider.Settings

class MainActivity : PreferenceActivity(), OnSharedPreferenceChangeListener {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null && extras.getBoolean("notify")) {
            startNotification()
            finish()
        }
        addPreferencesFromResource(R.xml.preferences)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isEnabled = sharedPrefs.getBoolean("enabled", false)
        if (isEnabled) {
            checkPermissions()
        }
        setAdvancedEnabled(isEnabled)
        setSummaries()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == PREF_KEY_ENABLED) {
            val prefEnabled = findPreference(PREF_KEY_ENABLED) as CheckBoxPreference
            val enabled = prefEnabled.isChecked
            if (enabled) {
                checkPermissions()
                prefEnabled.setSummary(R.string.summaryEnabled)
                setAdvancedEnabled(true)
                if (sharedPreferences.getBoolean("compatibility", false)) startNotification()
            } else {
                prefEnabled.setSummary(R.string.summaryDisabled)
                setAdvancedEnabled(false)
                stopNotification()
            }
        } else if (key == PREF_KEY_EXTENDED) {
            val prefExtended = findPreference(PREF_KEY_EXTENDED) as CheckBoxPreference
            val extended = prefExtended.isChecked
            if (extended) {
                prefExtended.setSummary(R.string.summaryExtendedEnabled)
            } else {
                prefExtended.setSummary(R.string.summaryExtendedDisabled)
            }
        } else if (key == PREF_KEY_COMPAT) {
            val prefCompat = findPreference(PREF_KEY_COMPAT) as CheckBoxPreference
            val compat = prefCompat.isChecked
            if (compat) {
                prefCompat.setSummary(R.string.summaryCompatEnabled)
                startNotification()
            } else {
                prefCompat.setSummary(R.string.summaryCompatDisabled)
                stopNotification()
            }
        } else if (key == PREF_KEY_AIRPLANETOGGLE) {
            val prefAirplaneToggle = findPreference(PREF_KEY_AIRPLANETOGGLE) as CheckBoxPreference
            val airplaneToggle = prefAirplaneToggle.isChecked
            if (airplaneToggle) {
                prefAirplaneToggle.setSummary(R.string.summaryAirplaneToggleEnabled)
            } else {
                prefAirplaneToggle.setSummary(R.string.summaryAirplaneToggleDisabled)
            }
        }
    }

    /**
     * Disable or enable advanced settings based on enabled state
     *
     * @param enabled Enabled state
     */
    protected fun setAdvancedEnabled(enabled: Boolean) {
        val prefExtended = findPreference(PREF_KEY_EXTENDED)
        val prefCompat = findPreference(PREF_KEY_COMPAT)
        val prefAirplaneToggle = findPreference(PREF_KEY_AIRPLANETOGGLE)
        if (enabled) {
            prefExtended.isEnabled = true
            prefCompat.isEnabled = true
            prefAirplaneToggle.isEnabled = true
        } else {
            prefExtended.isEnabled = false
            prefCompat.isEnabled = false
            prefAirplaneToggle.isEnabled = false
        }
    }

    protected fun setSummaries() {
        val prefEnabled = findPreference(PREF_KEY_ENABLED)
        val prefExtended = findPreference(PREF_KEY_EXTENDED)
        val prefCompat = findPreference(PREF_KEY_COMPAT)
        val prefAirplaneToggle = findPreference(PREF_KEY_AIRPLANETOGGLE)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (sharedPrefs.getBoolean(
                "enabled",
                false
            )
        ) prefEnabled.setSummary(R.string.summaryEnabled) else prefEnabled.setSummary(R.string.summaryDisabled)
        if (sharedPrefs.getBoolean(
                "extended",
                false
            )
        ) prefExtended.setSummary(R.string.summaryExtendedEnabled) else prefExtended.setSummary(R.string.summaryExtendedDisabled)
        if (sharedPrefs.getBoolean(
                "compatibility",
                false
            )
        ) prefCompat.setSummary(R.string.summaryCompatEnabled) else prefCompat.setSummary(R.string.summaryCompatDisabled)
        if (sharedPrefs.getBoolean(
                "airplanetoggle",
                false
            )
        ) prefAirplaneToggle.setSummary(R.string.summaryAirplaneToggleEnabled) else prefAirplaneToggle.setSummary(
            R.string.summaryAirplaneToggleDisabled
        )
    }

    /**
     * Start the notification
     */
    protected fun startNotification() {
        val builder = Notification.Builder(this@MainActivity)
        builder.setContentTitle(getString(R.string.notificationTitle))
        builder.setContentText(getString(R.string.notificationEnabled))
        builder.setSmallIcon(R.drawable.status_gingerbread)
        builder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon))
        builder.setAutoCancel(false)
        builder.setOngoing(true)
        builder.build()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * Stop the notification
     */
    protected fun stopNotification() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_ID)
    }

    /**
     * Check permissions
     */
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val nm = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (!nm.isNotificationPolicyAccessGranted) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.permissionDialogText))
            builder.setPositiveButton(getString(R.string.permissionDialogEnable)) { dialogInterface, i ->
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivityForResult(intent, 0)
            }
            builder.setNegativeButton(getString(R.string.permissionDialogCancel)) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.show()
        }
    }

    companion object {
        const val PREF_KEY_ENABLED = "enabled"
        const val PREF_KEY_EXTENDED = "extended"
        const val PREF_KEY_COMPAT = "compatibility"
        const val PREF_KEY_AIRPLANETOGGLE = "airplanetoggle"
        const val NOTIFICATION_ID = 1
    }
}
