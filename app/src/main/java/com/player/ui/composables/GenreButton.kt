package com.player.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.player.data.model.categoryPictureMap
import com.player.ui.theme.montserratFont
import com.player.ui.theme.selectedCategoryColor
import com.player.ui.screens.AddMusicViewModel

@Composable
fun SquareButtonWithImage(
    text: String,
    categoryName: String,
    selected: Boolean,
    onClick: () -> Unit,
    onSelected: () -> Unit,
    viewModel: AddMusicViewModel
) {

    val borderColor = if (selected) selectedCategoryColor else Color.Transparent
    val borderWidth = if (selected) 3.dp else 0.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                onSelected() // Notify the parent about the selection
                viewModel.setSelectedGenreName(text)
                onClick()
            }
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Green) // Background with rounded corners
        ) {
            Image(
                painter = painterResource(categoryPictureMap[categoryName]!!),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = text,
                color = Color.White,
                fontFamily = montserratFont,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}



