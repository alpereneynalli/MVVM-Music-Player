package com.player.data.model

import com.player.R

data class Category(val category: String, val category_id: Int) {
}

val categoryPictureMap = mapOf<String, Int>(
    "COUNTRY" to R.drawable.country,
    "LO-FI" to R.drawable.lofi,
    "DANCE" to R.drawable.dance,
    "BLUES" to R.drawable.blues,
    "ACOUSTIC" to R.drawable.acoustic,
    "ROCK" to R.drawable.rock,
    "FOLK" to R.drawable.folk,
    "HARD\nROCK" to R.drawable.hardrock,
    "ELECTRONIC" to R.drawable.electronic,
    "JAZZ" to R.drawable.jazz,
    "SOUND\nTRACKS" to R.drawable.sound_tracks,
    "SYMPHONY" to R.drawable.symphony,
    "MEDITATION" to R.drawable.meditation,
    "EASY\nLISTENING" to R.drawable.easy_listening,
    "PLAYFUL" to R.drawable.playful,
    "ROMANTIC" to R.drawable.romantic
)