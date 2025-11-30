package com.technote.aquasense.presentation.ui.components.common

import android.icu.text.ListFormatter.Width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.technote.aquasense.presentation.ui.theme.Dimens

@Composable
fun AquaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    width: Dp = 125.dp,
    height: Dp = Dimens.buttonHeight
) {
    val colors = when (variant) {
        ButtonVariant.Primary -> ButtonDefaults.buttonColors(
            containerColor = Color(0xFF007BFF),
            contentColor = Color.White,
            disabledContentColor = Color(0xFFCCCCCC),
            disabledContainerColor = Color(0xFF666666)
        )
        ButtonVariant.Secondary -> ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF007BFF)
        )
    }

    when (variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                enabled = enabled && !isLoading,
                colors = colors,
                modifier = modifier
                    .width(width)
                    .height(height)
            ) {
                ButtonContent(text, isLoading)
            }
        }
        ButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled && !isLoading,
                colors = colors,
                modifier = modifier
                    .width(width)
                    .height(height)
            ) {
                ButtonContent(text, isLoading)
            }
        }
    }
}

@Composable
private fun ButtonContent(text: String, isLoading: Boolean) {
    if (isLoading) {
        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(Dimens.iconSize),
            strokeWidth = 2.dp
        )
    } else {
        Text(
            text = text,
            fontSize = Dimens.fontSizeLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class ButtonVariant {
    Primary,
    Secondary
}