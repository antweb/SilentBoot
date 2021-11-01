package com.antweb.silentboot

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem

class HelpActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.help_activity)
        if (Build.VERSION.SDK_INT > 11) {
            enableUp()
        }
    }

    @TargetApi(11)
    protected fun enableUp() {
        val actionBar = actionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
