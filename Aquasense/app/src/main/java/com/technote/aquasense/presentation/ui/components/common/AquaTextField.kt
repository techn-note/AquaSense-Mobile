package com.technote.aquasense.presentation.ui.components.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.technote.aquasense.presentation.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AquaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isEnable: Boolean = true,
    enabled: Boolean = true, // Alias para isEnable
    error: String? = null,
    singleLine: Boolean = true
) {
    val isEnabled = isEnable && enabled
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        //Label (apenas se label não for vazio)
        if (label.isNotEmpty()) {
            Text(
                text = label,
                fontSize = Dimens.fontSizeLarge,
                fontWeight = FontWeight.Medium,
                color = if (error != null) Color.Red else Color(0xFF343434),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Dimens.spaceExtraSmall),
                textAlign = TextAlign.Start
            )
        }

        // TextField
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth(),
            enabled = isEnabled,
            singleLine = singleLine,
            visualTransformation = if (isPassword)
            PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error != null) Color.Red else Color(0xFF007BFF),
                unfocusedBorderColor = if (error != null) Color.Red else Color(0xFF343434),
                focusedLabelColor = if (error != null) Color.Red else Color(0xFF007BFF),
                cursorColor = Color(0xFF007BFF)
            ),
            isError = error != null,
            shape = RoundedCornerShape(Dimens.borderRadiusMedium)
        )

        // Error Message
        if (error != null) {
            Text(
                text = error,
                color = Color.Red,
                fontSize = Dimens.fontSizeSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Dimens.spaceLarge, top = Dimens.spaceExtraSmall)
            )
        }

    }
}

