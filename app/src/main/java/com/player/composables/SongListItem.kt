package com.player.composables

import android.util.Log
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.player.R
import com.player.audioPlayer.MediaPlayerViewModel
import com.player.audioPlayer.PlayerButtons
import com.player.audioPlayer.PlayerSlider
import com.player.roomdb.OnlineSong
import com.player.ui.theme.gradientCombinations
import com.player.ui.theme.montserratFont
import com.player.viewmodel.AddMusicViewModel
import kotlin.random.Random

@Composable
fun SongListItem(
    song: OnlineSong,
    isFavorite: Boolean,
    isDownloaded: Boolean,
    onFavoriteToggle: () -> Unit,
    expandedItemId: Int,
    onItemClicked: (Int) -> Unit,
    onDownloadClicked: () -> Unit,
    audioPlayerViewModel: MediaPlayerViewModel,
    addMusicViewModel: AddMusicViewModel
) {
    val isExpanded = expandedItemId == song.songID
    val randomIndex = remember { Random.nextInt(0, gradientCombinations.size) }
    val selectedCombination = gradientCombinations[randomIndex]
    val downloadProgress by addMusicViewModel.downloadProgress.observeAsState(0)
    val context = LocalContext.current

    val currentPlayingSongId = remember { mutableStateOf<Int?>(null) }

    Log.d("SongListItem", "Download Progress: $downloadProgress")

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
                    onItemClicked(if (isExpanded) -1 else song.songID)
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
                        .background(
                            Brush.linearGradient(
                                selectedCombination.colors,
                                start = Offset(50f, 0f),
                                end = Offset(50f, 100f)
                            )
                        )
                        .padding(12.dp)
                        .clip(CircleShape),
                    alignment = Alignment.Center
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                Column {
                    Text(
                        text = song.songName,
                        color = Color.White,
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
                    if (isDownloaded) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Downloaded",
                            tint = Color.White
                        )
                    } else {
                        if (downloadProgress in 1..99) {
                            CircularProgressIndicator(
                                progress = downloadProgress.toFloat() / 100f,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            IconButton(onClick = onDownloadClicked) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))

            LaunchedEffect(song.songID, isDownloaded) {
                if (currentPlayingSongId.value != song.songID) {
                    if (isDownloaded) {
                        audioPlayerViewModel.triggerMediaScan(context, song.fileName)
                        val localUri = audioPlayerViewModel.getDownloadedFileUri(context, song.fileName)
                        if (localUri != null) {
                            audioPlayerViewModel.initExoPlayer(localUri)
                        }
                    } else {
                        audioPlayerViewModel.loadFileFromFirebase(
                            callback = { uri ->
                                audioPlayerViewModel.initExoPlayer(uri)
                            },
                            errorCallback = { errorMessage ->
                                Log.d("EXO", errorMessage)
                            },
                            song.fileName
                        )
                    }
                    currentPlayingSongId.value = song.songID
                }
            }

            Column {
                PlayerSlider(audioPlayerViewModel)
                PlayerButtons(audioPlayerViewModel)
            }
        }
    }
}