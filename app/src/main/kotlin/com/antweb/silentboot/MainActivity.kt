package com.antweb.silentboot

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.antweb.silentboot.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val PREF_KEY_ENABLED = "enabled"
    }

    private lateinit var binding: MainActivityBinding
    private var isEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        isEnabled = sharedPrefs.getBoolean(PREF_KEY_ENABLED, false)

        setLabels()
        binding.activateToggleButton.setOnClickListener {
            toggleEnabled()
        }

        binding.openHelpButton.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }

        if (isEnabled) {
            checkPermissions()
        }
    }

    private fun setLabels() {
        if (isEnabled) {
            binding.activateToggleButton.text = getString(R.string.buttonDisable)
            binding.enabledStatusText.text = getString(R.string.summaryEnabled)
        } else {
            binding.activateToggleButton.text = getString(R.string.buttonEnable)
            binding.enabledStatusText.text = getString(R.string.summaryDisabled)
        }
    }

    private fun toggleEnabled() {
        isEnabled = !isEnabled

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPrefs.edit()
        editor.putBoolean(PREF_KEY_ENABLED, isEnabled)
        editor.apply()

        setLabels()

        if (isEnabled) {
            checkPermissions()

            val intent = Intent(applicationContext, ShutdownReceiverService::class.java)
            startForegroundService(intent)
        } else {
            val intent = Intent(applicationContext, ShutdownReceiverService::class.java)
            stopService(intent)
        }
    }

    private fun checkPermissions() {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.permissionDialogText))
            builder.setPositiveButton(getString(R.string.permissionDialogEnable)) { _, _ ->
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                startActivityForResult(intent, 0)
            }
            builder.setNegativeButton(getString(R.string.permissionDialogCancel)) { dialogInterface, i -> dialogInterface.dismiss() }
            builder.show()
        }
    }
}
