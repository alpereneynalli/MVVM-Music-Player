package com.player.ui.audioPlayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.player.R
import com.player.data.model.OnlineSong
import com.player.ui.theme.buttonColor
import com.player.ui.theme.montserratFont
import com.player.ui.theme.selectedCategoryColor

@Composable
fun SongListItem(
    song: OnlineSong,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    audioPlayerViewModel: MediaPlayerViewModel?,
) {

    val songState = audioPlayerViewModel?.songState?.observeAsState()
    val songExpansionState = audioPlayerViewModel?.songExpansionState?.observeAsState()
    val currentPlayingSongId = audioPlayerViewModel?.currentPlayingSongId?.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .clickable {
                    audioPlayerViewModel?.handleSong(song)
                }
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.musiclogo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(color = buttonColor)
                        .padding(12.dp)
                        .clip(CircleShape),
                    alignment = Alignment.Center
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    Column {
                        Text(
                            text = song.songName,
                            color = if (currentPlayingSongId?.value == song.songID) selectedCategoryColor else Color.White,
                            fontFamily = montserratFont,
                            fontWeight = FontWeight.W500,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = song.musician,
                            color = Color.White,
                            fontFamily = montserratFont,
                            fontWeight = FontWeight.W300,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = song.duration,
                            color = Color.White,
                            fontFamily = montserratFont,
                            fontWeight = FontWeight.W300,
                            fontSize = 10.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = Color.White
                        )
                    }
                }
            }
        }
        if (currentPlayingSongId?.value == song.songID) {
            when (songExpansionState?.value) {
                SongExpansionState.Expanded -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        PlayerSlider(audioPlayerViewModel)
                        PlayerButtons(audioPlayerViewModel)
                    }
                }

                else -> Unit
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongListItemPreview() {
    // Create dummy data for the preview
    val song = OnlineSong(
        songID = 1,
        songName = "Song Name",
        musician = "Musician Name",
        duration = "3:30",
        category = "Banger",
        sourceURL = "https://www.example.com/song.mp3",
        fileName = "song.mp3",
    )

    // Display the SongListItem with the dummy data
    Box(modifier = Modifier.background(Color.Black)) {
        SongListItem(
            song = song,
            isFavorite = true,
            onFavoriteToggle = {},
            audioPlayerViewModel = null,
        )
    }

}

