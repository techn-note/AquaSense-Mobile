package com.technote.aquasense.presentation.ui.components.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.technote.aquasense.presentation.ui.theme.Dimens

@Composable
fun AuthActionRow(
    primaryText: String,
    secondaryText: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    primaryEnabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onSecondaryClick,
            enabled = !isLoading
        ) {
            Text(
                text = secondaryText,
                color = Color(0xFF007BFF),
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Button(
            onClick = onPrimaryClick,
            enabled = primaryEnabled && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF007BFF),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFCCCCCC)
            ),
            shape = RoundedCornerShape(Dimens.borderRadiusMedium),
            modifier = Modifier.widthIn(min = 100.dp),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 16.dp)
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.spaceExtraLarge),
                        strokeWidth = 2.dp,
                        color = Color.White,
                    )
                }
            } else {
                Text(
                    text = primaryText,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}