package com.technote.aquasense.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.*

// String Extensions
fun String?.isValidEmail(): Boolean {
    return this?.let { android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches() } ?: false
}

fun String?.isNotNullOrBlank(): Boolean {
    return !this.isNullOrBlank()
}

// Date Extensions
fun Date.toFormattedString(pattern: String = "dd/MM/yyyy HH:mm"): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}

// Double Extensions
fun Double.toTemperatureString(): String = "${this}ºC"
fun Double.toVolumeString(): String = "${this.toInt()} Litros"
fun Double.toPpmString(): String = "${this.toInt()} PPM"

// Progress Calculation
fun Double.calculateProgress(min: Double, max: Double): Float {
    return when {
        this < min -> 0.1f
        this > max -> 1.0f
        else -> ((this - min) / (max - min)).toFloat() * 0.8f + 0.1f
    }
}

// Color Extensions
@Composable
fun getProgressColor(progress: Float): Color {
    return when {
        progress >= 0.8f -> Color(0xFF00DF3A) // Verde
        progress >= 0.5f -> Color(0xFFFFB020) // Amarelo
        else -> Color(0xFFFF5733) // Vermelho
    }
}

