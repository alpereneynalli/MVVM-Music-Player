package com.player.ui.audioPlayer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PlayerSlider(viewModel: MediaPlayerViewModel?) {
    val currentMinutes = viewModel?.currentMinutes?.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Slider(
            value = currentMinutes?.value!!.toFloat(),
            onValueChange = { newValue ->
                viewModel.pause()
                viewModel.seekTo(newValue.toInt())
            },
            onValueChangeFinished = {
                viewModel.play()
            },
            valueRange = 0f..(viewModel.duration.value ?: 0L).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = viewModel.formatMilliseconds(currentMinutes.value!!),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${viewModel.formatMilliseconds((viewModel.duration.value ?: 0L).toInt())} ",
                color = Color.White
            )
        }
    }
}