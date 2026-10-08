package com.example.medicareapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/*
 * Schermata Paziente utilizza principalmente il tema chiaro.
 * Non vengono usati colori dinamici di Android -> si evita che app cambi
 * colore in base a dispositivo dell'utente.
 * Obbiettivo è quello di far mantenere all'app la propria identità visiva.
 */

private val LightColorScheme = lightColorScheme(
    primary = MediCareBlue,
    onPrimary = MediCareSurface,

    secondary = MediCareBlueDark,
    onSecondary = MediCareSurface,

    tertiary = MediCareBlueLight,
    onTertiary = MediCareText,

    background = MediCareBackground,
    onBackground = MediCareText,

    surface = MediCareSurface,
    onSurface = MediCareText,

    surfaceVariant = MediCareBlueLight,
    onSurfaceVariant = MediCareTextSecondary,

    outline = MediCareBorder,

    error = MediCareError,
    onError = MediCareSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = MediCareBlue,
    secondary = MediCareBlueLight,
    tertiary = MediCareBlueLight
)

@Composable
fun MedicareAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}