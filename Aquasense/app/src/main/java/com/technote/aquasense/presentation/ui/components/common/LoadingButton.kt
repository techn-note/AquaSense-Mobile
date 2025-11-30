package com.technote.aquasense.presentation.ui.components.common

import android.R.attr.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.technote.aquasense.presentation.ui.theme.Dimens

@Composable
fun LoadingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    backgroundColor: Color = Color(0xFF007BFF),
    contentColor: Color = Color(0xFFFFFFFF),
    width: Dp? = null, // Torna width opcional
    height: Dp = Dimens.buttonHeight
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor,
            disabledContainerColor = Color(0xFFCCCCCC),
            disabledContentColor = Color(0xFF666666)
        ),
        modifier = modifier
            .let { if (width != null) it.width(width) else it }
            .height(height)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = contentColor,
                    modifier = Modifier.size(Dimens.iconSize),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}