// ArtistListRecommendation.kt

package com.example.lyrafy.models

import com.music.innertube.models.YTItem

data class ArtistGroup(
    val artistName: String,
    val artistId: String?,
    val artistThumbnailUrl: String?,
    val items: List<YTItem>,
)
