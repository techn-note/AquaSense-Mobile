package com.technote.aquasense.presentation.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // Primary colors
    primary = PrimaryBlue,
    onPrimary = TextLight,
    primaryContainer = PrimaryBlueVariant,
    onPrimaryContainer = TextLight,

    // Secondary colors
    secondary = SecondaryGreen,
    onSecondary = TextDark,
    secondaryContainer = SecondaryGreenVariant,
    onSecondaryContainer = TextLight,

    // Tertiary colors
    tertiary = WarningYellow,
    onTertiary = TextDark,
    tertiaryContainer = WarningYellow,
    onTertiaryContainer = TextDark,

    // Error colors
    error = ErrorRed,
    onError = TextLight,
    errorContainer = ErrorRed,
    onErrorContainer = TextLight,

    // Background colors
    background = BackgroundLight,
    onBackground = TextDark,

    // Surface colors
    surface = BackgroundWhite,
    onSurface = TextDark,
    surfaceVariant = SurfaceGray,
    onSurfaceVariant = TextGray,

    // Outline and other colors
    outline = BorderGray,
    outlineVariant = DividerColor,
    scrim = TextDark,
    inverseSurface = TextDark,
    inverseOnSurface = TextLight,
    inversePrimary = PrimaryBlueVariant,
    surfaceTint = PrimaryBlue
)

private val DarkColorScheme = darkColorScheme(
    // Primary colors - MESMAS CORES DO MODO CLARO
    primary = PrimaryBlue,
    onPrimary = TextLight,
    primaryContainer = PrimaryBlueVariant,
    onPrimaryContainer = TextLight,

    // Secondary colors - MESMAS CORES DO MODO CLARO
    secondary = SecondaryGreen,
    onSecondary = TextDark,
    secondaryContainer = SecondaryGreenVariant,
    onSecondaryContainer = TextLight,

    // Tertiary colors - MESMAS CORES DO MODO CLARO
    tertiary = WarningYellow,
    onTertiary = TextDark,
    tertiaryContainer = WarningYellow,
    onTertiaryContainer = TextDark,

    // Error colors - MESMAS CORES DO MODO CLARO
    error = ErrorRed,
    onError = TextLight,
    errorContainer = ErrorRed,
    onErrorContainer = TextLight,

    // Background colors (apenas estes ficam escuros)
    background = Color(0xFF121212), // Fundo escuro padrão do Material
    onBackground = TextLight,

    // Surface colors (apenas estes ficam escuros)
    surface = Color(0xFF1E1E1E), // Surface escuro padrão do Material
    onSurface = TextLight,
    surfaceVariant = Color(0xFF2C2C2C), // Um pouco mais claro que surface
    onSurfaceVariant = Color(0xFFB0B0B0), // Cinza claro para texto secundário

    // Outline and other colors - MESMAS CORES DO MODO CLARO
    outline = BorderGray,
    outlineVariant = DividerColor,
    scrim = TextDark,
    inverseSurface = BackgroundWhite,
    inverseOnSurface = TextDark,
    inversePrimary = PrimaryBlue,
    surfaceTint = PrimaryBlue
)

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */


@Composable
fun AquasenseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Para manter as cores do Aquasense
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}