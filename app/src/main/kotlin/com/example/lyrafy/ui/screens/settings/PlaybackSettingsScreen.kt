package com.example.lyrafy.ui.screens.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lyrafy.R
import com.example.lyrafy.constants.PersistentQueueKey
import com.example.lyrafy.constants.SkipSilenceKey
import com.example.lyrafy.constants.StopPlaybackOnTaskRemovedKey
import com.example.lyrafy.constants.SwipeToChangeSongKey
import com.example.lyrafy.ui.component.AppSwitch
import com.example.lyrafy.ui.component.SettingsGroup
import com.example.lyrafy.ui.component.SettingsGroupStyle
import com.example.lyrafy.ui.component.SettingsItem
import com.example.lyrafy.ui.component.SubSettingsScaffold
import com.example.lyrafy.ui.component.SubSettingsSearchBar
import com.example.lyrafy.utils.rememberPreference

@Composable
fun PlaybackSettingsScreen(navController: NavController) {
    var query by remember { mutableStateOf("") }

    val (stopPlaybackOnTaskRemoved, onStopPlaybackOnTaskRemovedChange) = rememberPreference(
        StopPlaybackOnTaskRemovedKey,
        defaultValue = false
    )
    val (persistentQueue, onPersistentQueueChange) = rememberPreference(
        PersistentQueueKey,
        defaultValue = true
    )
    val (skipSilence, onSkipSilenceChange) = rememberPreference(
        SkipSilenceKey,
        defaultValue = false
    )
    val (swipeToChangeSong, onSwipeToChangeSongChange) = rememberPreference(
        SwipeToChangeSongKey,
        defaultValue = false
    )

    val keepPlaying = !stopPlaybackOnTaskRemoved

    SubSettingsScaffold(
        title = "Playback",
        onBack = { navController.navigateUp() },
        searchBar = { SubSettingsSearchBar(query = query, onQueryChange = { query = it }) },
    ) {
        val items = listOf(
            SettingsItem(
                title = { Text("Keep playing even when closed") },
                highlightKey = "Keep playing even when closed",
                descriptionText = "Keep playing after the app is cleared",
                detail = "Continue playback in background when app is cleared from recents",
                icon = painterResource(R.drawable.clear_all),
                iconShape = CircleShape,
                onClick = { onStopPlaybackOnTaskRemovedChange(!stopPlaybackOnTaskRemoved) },
                trailingContent = {
                    AppSwitch(
                        checked = keepPlaying,
                        onCheckedChange = { onStopPlaybackOnTaskRemovedChange(!it) },
                    )
                },
            ),
            SettingsItem(
                title = { Text("Save Player's last state") },
                highlightKey = "Save Player's last state",
                descriptionText = "Off clears played tracks on close",
                detail = "disabling this will removing previously played tracks when closing the app",
                icon = painterResource(R.drawable.cached),
                iconShape = CircleShape,
                onClick = { onPersistentQueueChange(!persistentQueue) },
                trailingContent = {
                    AppSwitch(
                        checked = persistentQueue,
                        onCheckedChange = onPersistentQueueChange,
                    )
                },
            ),
            SettingsItem(
                title = { Text("Skip Silence") },
                highlightKey = "Skip Silence",
                descriptionText = "Jump over the quiet gaps in songs",
                icon = painterResource(R.drawable.skip_next),
                iconShape = CircleShape,
                onClick = { onSkipSilenceChange(!skipSilence) },
                trailingContent = {
                    AppSwitch(
                        checked = skipSilence,
                        onCheckedChange = onSkipSilenceChange,
                    )
                },
            ),
            SettingsItem(
                title = { Text("Geser untuk Ganti Lagu") },
                highlightKey = "Geser untuk Ganti Lagu",
                descriptionText = "Aktifkan untuk mengganti lagu dengan menggeser ke kanan atau kiri di layar penuh pemutar musik",
                icon = painterResource(R.drawable.skip_next),
                iconShape = CircleShape,
                onClick = { onSwipeToChangeSongChange(!swipeToChangeSong) },
                trailingContent = {
                    AppSwitch(
                        checked = swipeToChangeSong,
                        onCheckedChange = onSwipeToChangeSongChange,
                    )
                },
            ),
        ).filter {
            query.isBlank() || it.highlightKey?.contains(query, ignoreCase = true) == true ||
                it.descriptionText?.contains(query, ignoreCase = true) == true
        }

        if (items.isNotEmpty()) {
            SettingsGroup(
                style = SettingsGroupStyle.Grouped,
                items = items,
            )
        }

        Spacer(Modifier.height(140.dp))
    }
}
