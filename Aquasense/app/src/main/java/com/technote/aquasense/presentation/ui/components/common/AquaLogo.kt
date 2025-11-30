package com.technote.aquasense.presentation.ui.components.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.technote.aquasense.R
import com.technote.aquasense.presentation.ui.theme.Dimens

@Composable
fun AquaLogo(
    modifier: Modifier = Modifier,
    size: Dp = Dimens.logoSize,
    contentDescription: String = "Logo Aquasense",
    negative: Boolean = false
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
    }
    if (negative) {
        Image(
            painter = painterResource(id = R.drawable.ic_action_name),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size)
        )
    } else {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(size)
                .padding(bottom = Dimens.containerPadding)
        )
    }

}