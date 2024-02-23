package com.player.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val gradientBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF2E2E2E), Color(0xFF1F1F1F)) // Define your gradient colors
)
val selectedCategoryColor = Color(0xFF69F0AE)
val buttonColor = Color(0x4BFFFFFF)

data class GradientCombination(val colors: List<Color>)

val gradientCombinations = listOf(
    GradientCombination(listOf(Color(0xFFEED800), Color(0xFFFF2DC4))),
    GradientCombination(listOf(Color(0xFF00A5D9), Color(0xFF2DFFB3))),
    GradientCombination(listOf(Color(0xFF2DFFB3), Color(0xFFEED800))),
    GradientCombination(listOf(Color(0xFFEED800), Color(0xFFFF2DC4))),
    GradientCombination(listOf(Color(0xFFFF2DC4), Color(0xFF935FCD), Color(0xFF00A5D9)))
)