package com.antweb.silentboot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SilentBootTheme {
                var showHelp by remember { mutableStateOf(false) }

                // Ideally we'd want to use something like Navigation 3 here.
                // But to keep the memory profile as small as possible, we'll instead create a
                // rudimentary navigation with BackHandler + AnimatedContent that doesn't need extra
                // dependencies.
                BackHandler(enabled = showHelp) {
                    showHelp = false
                }

                AnimatedContent(
                    targetState = showHelp,
                    label = "content",
                    transitionSpec = {
                        if (showHelp) {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                        }.using(
                            SizeTransform(clip = false),
                        )
                    },
                ) { state ->
                    if (!state) {
                        MainScreen(
                            onHelpNavigate = { showHelp = true },
                            onToggle = { enabled -> updateEnabledState(enabled) },
                        )
                    } else {
                        HelpScreen(
                            onBack = { showHelp = false },
                        )
                    }
                }
            }
        }
    }

    private fun updateEnabledState(isEnabled: Boolean) {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPrefs.edit {
            putBoolean(PreferenceKey.ENABLED.key, isEnabled)
            apply()
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
}
