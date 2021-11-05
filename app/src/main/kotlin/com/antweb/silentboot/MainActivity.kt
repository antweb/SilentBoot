package com.antweb.silentboot

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Build
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.provider.Settings

class MainActivity : PreferenceActivity(), OnSharedPreferenceChangeListener {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isEnabled = sharedPrefs.getBoolean("enabled", false)
        if (isEnabled) {
            checkPermissions()
        }

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
            } else {
                prefEnabled.setSummary(R.string.summaryDisabled)
            }
        }
    }

    protected fun setSummaries() {
        val prefEnabled = findPreference(PREF_KEY_ENABLED)
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (sharedPrefs.getBoolean(
                "enabled",
                false
            )
        ) {
            prefEnabled.setSummary(R.string.summaryEnabled)
        } else {
            prefEnabled.setSummary(R.string.summaryDisabled)
        }
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
    }
}
