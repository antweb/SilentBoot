package com.antweb.silentboot.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = red,
    primaryVariant = Color(0x00FF00),
)

private val LightColorPalette = lightColors(
    primary = red,
//    primaryVariant = darkRed,
    primaryVariant = Color(0x00FF00),

)

@Composable
fun SilentBootTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        content = content
    )
}
