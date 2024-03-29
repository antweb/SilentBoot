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
    private lateinit var binding: MainActivityBinding
    private var isEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        isEnabled = sharedPrefs.getBoolean(PreferenceKey.ENABLED.key, false)

        binding.activateSwitch.setOnCheckedChangeListener { _, value ->
            if (value != isEnabled) {
                updateEnabledState(value)
            }
        }

        binding.openHelpButton.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }

        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()

        setLabels()

        updateEnabledState(isEnabled && checkPermissions())
    }

    private fun setLabels() {
        if (isEnabled) {
            binding.activateSwitch.isChecked = true
            binding.statusImage.setImageDrawable(applicationContext.getDrawable(R.drawable.status_image_enabled))
            binding.enabledStatusText.text = getString(R.string.summaryEnabled)
        } else {
            binding.activateSwitch.isChecked = false
            binding.statusImage.setImageDrawable(applicationContext.getDrawable(R.drawable.status_image_disabled))
            binding.enabledStatusText.text = getString(R.string.summaryDisabled)
        }
    }

    private fun updateEnabledState(newValue: Boolean) {
        if (newValue && !checkPermissions()) {
            setLabels()
            showPermissionDialog()
            return
        }

        if (newValue != isEnabled) {
            isEnabled = newValue
            setLabels()

            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            val editor = sharedPrefs.edit()
            editor.putBoolean(PreferenceKey.ENABLED.key, isEnabled)
            editor.apply()
        }

        if (isEnabled) {
            startService()
        } else {
            stopService()
        }
    }

    private fun startService() {
        val intent = Intent(applicationContext, ShutdownReceiverService::class.java)
        startForegroundService(intent)
    }

    private fun stopService() {
        val intent = Intent(applicationContext, ShutdownReceiverService::class.java)
        stopService(intent)
    }

    private fun checkPermissions(): Boolean {
        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.permissionDialogText))
        builder.setPositiveButton(getString(R.string.permissionDialogEnable)) { _, _ ->
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivityForResult(intent, 0)
        }
        builder.setNegativeButton(getString(R.string.permissionDialogCancel)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        builder.show()
    }
}
