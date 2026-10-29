// NetEaseLyricsProvider.kt

package com.example.lyrafy.lyrics

import android.content.Context
import com.music.directlyrics.NetEase
import com.example.lyrafy.constants.EnableNetEaseKey
import com.example.lyrafy.utils.dataStore
import com.example.lyrafy.utils.get

object NetEaseLyricsProvider : LyricsProvider {
    override val name = "NetEase"
    override fun isEnabled(context: Context): Boolean = context.dataStore[EnableNetEaseKey] ?: true

    override suspend fun getLyrics(
        id: String,
        title: String,
        artist: String,
        duration: Int,
        album: String?,
    ): Result<String> = NetEase.getLyrics(title, artist, duration)
}
