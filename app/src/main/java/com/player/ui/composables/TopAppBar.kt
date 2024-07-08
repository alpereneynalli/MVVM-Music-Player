package com.player.ui.composables

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.player.ui.theme.montserratFont


@Composable
fun TopAppBar(text: String, onBackClicked: () -> Unit) {
    androidx.compose.material.TopAppBar(
        title = {
            Text(
                text = text,
                color = Color.White,
                fontFamily = montserratFont,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}