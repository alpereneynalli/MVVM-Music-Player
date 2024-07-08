package com.player.ui.audioPlayer

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlayerButtons(
    viewModel: MediaPlayerViewModel?,
    playerButtonSize: Dp = 48.dp,
    sideButtonSize: Dp = 36.dp
) {
    val songState = viewModel?.songState?.observeAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            imageVector = Icons.Filled.Replay10,
            contentDescription = "Reply 10 Sec Icon",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(sideButtonSize)
                .clickable {
                    viewModel?.backward(10)
                }
        )
        when (songState?.value) {
            SongState.Loading -> CircularProgressIndicator()
            else -> Image(
                imageVector = if(songState?.value == SongState.Playing) Icons.Filled.PauseCircleFilled
                else Icons.Filled.PlayCircleFilled,
                contentDescription = "Play / Pause Icon",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .size(playerButtonSize)
                    .semantics { role = Role.Button }
                    .clickable {
                        viewModel?.handlePlayPause()
                    }
            )
        }

        Image(
            imageVector = Icons.Filled.Forward10,
            contentDescription = "Forward Icon",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(sideButtonSize)
                .clickable {
                    viewModel?.forward(10)
                }
        )
    }
}