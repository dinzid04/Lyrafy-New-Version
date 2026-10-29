// DonateSheet.kt

package com.example.lyrafy.ui.screens.donate

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.example.lyrafy.R
import com.example.lyrafy.ui.component.PopupSheetState
import com.example.lyrafy.ui.player.menu.MenuRowSurface
import com.example.lyrafy.ui.theme.InterFontFamily

private const val DevAvatarFallbackUrl = "https://url.dinzid.my.id/HOjKIXC"
private val LyraGold = Color(0xFFE2B963)

/** Shows Donate as a plain PopupSheet - see docs/popup-sheet.skill.md before copying this file. */
fun PopupSheetState.showDonateSheet() {
    show(buttonBar = { DonateDoneButton(onClick = ::dismiss) }) {
        DonateSheetContent()
    }
}

@Composable
private fun DonateDoneButton(onClick: () -> Unit) {
    val borderBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.22f)
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(MenuRowSurface)
            .border(1.dp, borderBrush, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Done",
            fontFamily = InterFontFamily,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun ColumnScope.DonateSheetContent() {
    var showQrisFullscreen by remember { mutableStateOf(false) }

    val glassBorderBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.42f),
                LyraGold.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.28f)
            ),
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Developer Profile Picture (Dinz)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1B20))
                    .border(2.dp, LyraGold.copy(alpha = 0.85f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = DevAvatarFallbackUrl,
                    placeholder = painterResource(R.drawable.ic_lyra_logo),
                    error = painterResource(R.drawable.ic_lyra_logo),
                    contentDescription = "Dinz profile picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(LyraGold.copy(alpha = 0.16f))
                        .border(1.dp, LyraGold.copy(alpha = 0.45f), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LYRA DEVELOPER",
                        fontFamily = InterFontFamily,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = LyraGold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Dinz / Gading Kencana",
                    fontFamily = InterFontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "@GadingKecana",
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    color = Color(0xFF9AA0A6)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Dukung Pengembangan LYRA",
            fontFamily = InterFontFamily,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.5).sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Terima kasih telah menggunakan LYRA! Setiap dukungan yang kamu berikan sangat berarti untuk menjaga aplikasi ini tetap hidup, bebas iklan, dan terus berkembang menjadi lebih baik.",
            fontFamily = InterFontFamily,
            fontSize = 13.5.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFD0D4DC)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Direct QRIS Card inside a Liquid Glass container
        val qrisCardShape = RoundedCornerShape(24.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(qrisCardShape)
                .background(Color(0xFF14151A).copy(alpha = 0.82f))
                .border(1.dp, glassBorderBrush, qrisCardShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { showQrisFullscreen = true }
                )
                .drawWithCache {
                    val sheen = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.38f
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(sheen)
                    }
                }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Golden Accent Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(LyraGold.copy(alpha = 0.18f))
                    .border(1.dp, LyraGold.copy(alpha = 0.5f), RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "QRIS • All E-Wallet & Mobile Banking",
                    fontFamily = InterFontFamily,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = LyraGold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Dinz / Gading Kencana",
                fontFamily = InterFontFamily,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // High-contrast QRIS preview frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.qris_dinz,
                    placeholder = painterResource(R.drawable.ic_lyra_logo),
                    error = painterResource(R.drawable.ic_lyra_logo),
                    contentDescription = "QRIS Dinz / Gading Kencana",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tap to enlarge hint pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.10f))
                    .border(1.dp, Color.White.copy(alpha = 0.22f), RoundedCornerShape(50))
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.fullscreen),
                    contentDescription = null,
                    tint = LyraGold,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ketuk untuk memperbesar QRIS",
                    fontFamily = InterFontFamily,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showQrisFullscreen) {
        QrisLiquidGlassFullscreenOverlay(
            onDismiss = { showQrisFullscreen = false }
        )
    }
}

@Composable
fun QrisLiquidGlassFullscreenOverlay(
    onDismiss: () -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val cardBorderBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.55f),
                LyraGold.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.35f)
            ),
            start = Offset.Zero,
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xEB08090C))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                )
                .padding(horizontal = 20.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            val frameShape = RoundedCornerShape(28.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(frameShape)
                    .background(Color(0xFF14151A).copy(alpha = 0.90f))
                    .border(1.2.dp, cardBorderBrush, frameShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {}
                    )
                    .drawWithCache {
                        val topSheen = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.16f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.35f
                        )
                        onDrawWithContent {
                            drawContent()
                            drawRect(topSheen)
                        }
                    }
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(LyraGold.copy(alpha = 0.18f))
                        .border(1.dp, LyraGold.copy(alpha = 0.55f), RoundedCornerShape(50))
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "QRIS • All E-Wallet & Mobile Banking",
                        fontFamily = InterFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LyraGold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Dinz / Gading Kencana",
                    fontFamily = InterFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Cubit untuk memperbesar (Pinch to zoom)",
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFFA0A4B0),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(1f, 3.5f)
                                scale = newScale
                                if (newScale > 1f) {
                                    offsetX = (offsetX + pan.x).coerceIn(-400f, 400f)
                                    offsetY = (offsetY + pan.y).coerceIn(-400f, 400f)
                                } else {
                                    offsetX = 0f
                                    offsetY = 0f
                                }
                            }
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = R.drawable.qris_dinz,
                        placeholder = painterResource(R.drawable.ic_lyra_logo),
                        error = painterResource(R.drawable.ic_lyra_logo),
                        contentDescription = "QRIS Fullscreen",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offsetX
                                translationY = offsetY
                            }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Close / Done Liquid Glass Pill Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, cardBorderBrush, RoundedCornerShape(50))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onDismiss
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tutup / Selesai",
                        fontFamily = InterFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
