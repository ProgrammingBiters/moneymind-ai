package com.moneymind.ai.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColors = lightColorScheme(
    primary = MoneyMindGreenDark,
    onPrimary = MoneyMindSurfaceLight,
    secondary = MoneyMindSlate,
    background = MoneyMindBackgroundLight,
    surface = MoneyMindSurfaceLight,
    error = MoneyMindRed
)

private val DarkColors = darkColorScheme(
    primary = MoneyMindGreen,
    onPrimary = MoneyMindNavy,
    secondary = MoneyMindSlate,
    background = MoneyMindBackgroundDark,
    surface = MoneyMindSurfaceDark,
    error = MoneyMindRed
)

/**
 * App-wide Material 3 theme. Uses dynamic color (Material You) on Android 12+
 * when available, falling back to the hand-tuned MoneyMind palette otherwise.
 */
@Composable
fun MoneyMindTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MoneyMindTypography,
        shapes = MoneyMindShapes,
        content = content
    )
}
