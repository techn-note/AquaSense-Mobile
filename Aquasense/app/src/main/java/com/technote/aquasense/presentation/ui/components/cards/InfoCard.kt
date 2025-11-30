package com.technote.aquasense.presentation.ui.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoCard(
    title: String,
    items: List<InfoItem>,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = title,
            color = Color(0xFF1976D2),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items.forEach { item ->
                    InfoItemView(
                        item = item,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItemView(
    item: InfoItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.label,
            color = Color(0xFF757575),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = item.value,
            color = Color(0xFF1976D2),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        if (item.unit.isNotEmpty()) {
            Text(
                text = item.unit,
                color = Color(0xFF1976D2),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

        // Linha divisória (exceto no último item)
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .padding(start = 16.dp)
        ) {
            if (item.showDivider) {
                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp),
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

data class InfoItem(
    val label: String,
    val value: String,
    val unit: String = "",
    val showDivider: Boolean = false
)