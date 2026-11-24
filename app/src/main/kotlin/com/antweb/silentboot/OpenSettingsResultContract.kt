package com.antweb.silentboot

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract

class OpenSettingsResultContract : ActivityResultContract<Unit, Unit>() {
    override fun createIntent(context: Context, input: Unit) =
        Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

    override fun parseResult(resultCode: Int, intent: Intent?) {
        // No result. We just use this as a trigger to refresh our permission value
    }
}
