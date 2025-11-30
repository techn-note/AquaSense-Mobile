package com.technote.aquasense.presentation.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.technote.aquasense.presentation.ui.theme.Dimens

@Composable
fun AquaTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF333333),
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        fontSize = Dimens.fontSizeTitle,
        fontWeight = FontWeight.Bold,
        color = color,
        textAlign = textAlign,
        modifier = modifier.padding(bottom = Dimens.spaceExtraLarge)
    )
}