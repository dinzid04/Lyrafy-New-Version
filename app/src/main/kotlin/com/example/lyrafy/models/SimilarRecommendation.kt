// SimilarRecommendation.kt

package com.example.lyrafy.models

import com.music.innertube.models.YTItem
import com.example.lyrafy.db.entities.LocalItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
