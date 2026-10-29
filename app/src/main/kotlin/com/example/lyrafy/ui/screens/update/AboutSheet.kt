package com.example.lyrafy.ui.screens.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.lyrafy.BuildConfig
import com.example.lyrafy.R
import com.example.lyrafy.ui.component.PopupSheetState
import com.example.lyrafy.ui.component.pressBounce
import com.example.lyrafy.ui.screens.donate.showDonateSheet
import com.example.lyrafy.ui.theme.InterFontFamily

private val SheetBg = Color(0xFF0A0B0E)
private val StoryCardSurface = Color(0xFF121316)
private val StoryCardBorder = Color(0xFF26282E)
private val InfoPillBorder = Color(0xFF2A2C32)
private val LyraGold = Color(0xFFE2B963)
private val SubtitleGray = Color(0xFFA0A4B0)
private val HandleGray = Color(0xFF9AA0A6)
private val LabelGray = Color(0xFF8E929B)
private val BodyTextGray = Color(0xFFD2D5DC)

private const val DevBannerFallbackUrl = "https://url.dinzid.my.id/OiHa2Ll"
private const val DevAvatarFallbackUrl = "https://url.dinzid.my.id/HOjKIXC"
private const val XProfileUrl = "https://x.com/dinzid04"
private const val GitHubRepoUrl = "https://github.com/dinzid04/Linefy"
private const val DeveloperEmailUrl = "mailto:gadingkencana05@gmail.com"

/** Opens the Developer & About LYRA sheet in the zoom-out popup host. */
fun PopupSheetState.showAboutSheet() {
    show(
        fullBleed = true,
        surface = SheetBg,
        buttonBar = {
            SheetPrimaryButton(
                text = "Support development",
                leadingIcon = R.drawable.favorite,
                onClick = { showDonateSheet() },
            )
        },
    ) {
        AboutSheetContent(
            onBack = ::dismiss,
        )
    }
}

@Composable
private fun AboutSheetContent(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val installed = remember { shortVersion(BuildConfig.VERSION_NAME) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState),
    ) {
        // 1. Top Section (Back button, "Developer" title, subtitle)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1B20))
                    .border(1.dp, Color(0xFF2A2C34), CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onBack,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Developer",
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.6).sp,
                    color = Color.White,
                ),
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Di balik LYRA, ada satu orang yang percaya bahwa musik bisa membuat hidup lebih bermakna.",
                style = TextStyle(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = SubtitleGray,
                ),
            )
        }

        // 2. Banner & Overlapping Circular Avatar
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = DevBannerFallbackUrl,
                placeholder = painterResource(R.drawable.ic_lyra_logo),
                error = painterResource(R.drawable.ic_lyra_logo),
                contentDescription = "Developer Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFF14151A)),
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 20.dp)
                    .offset(y = 42.dp)
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1B20))
                    .border(2.dp, Color.White, CircleShape),
            ) {
                AsyncImage(
                    model = DevAvatarFallbackUrl,
                    placeholder = painterResource(R.drawable.ic_lyra_logo),
                    error = painterResource(R.drawable.ic_lyra_logo),
                    contentDescription = "Dinz Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

            // Space for the 42.dp avatar overhang + 12.dp spacing
            Spacer(Modifier.height(54.dp))

            // 3. Identity & Bio + Social Icons + Info Pills + Story Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    text = "Dinz",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                    ),
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    text = "@GadingKecana",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = HandleGray,
                    ),
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "\"Not every inspiration needs to be known. Some are simply meant to live on through what we create.\"",
                    style = TextStyle(
                        fontFamily = InterFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp,
                        lineHeight = 19.sp,
                        color = Color.White,
                    ),
                )

                Spacer(Modifier.height(14.dp))

                // Social Icons Row (X, GitHub, Mail)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // X Icon
                    Icon(
                        imageVector = XLogoIcon,
                        contentDescription = "X (@dinzid04)",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { context.openUrl(XProfileUrl) },
                            ),
                    )

                    // GitHub Icon
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = "GitHub (dinzid04/Linefy)",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { context.openUrl(GitHubRepoUrl) },
                            ),
                    )

                    // Mail Icon
                    Icon(
                        imageVector = MailOutlineIcon,
                        contentDescription = "Email (gadingkencana05@gmail.com)",
                        tint = Color.White,
                        modifier = Modifier
                            .size(21.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { context.openUrl(DeveloperEmailUrl) },
                            ),
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Info Pills (SMAN 1 Pabuaran & Indonesia)
                DeveloperInfoPill(
                    iconContent = {
                        Icon(
                            imageVector = GraduationCapIcon,
                            contentDescription = null,
                            tint = Color(0xFFB0B4C0),
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    text = "SMAN 1 Pabuaran",
                )

                Spacer(Modifier.height(8.dp))

                DeveloperInfoPill(
                    iconContent = {
                        Icon(
                            painter = painterResource(R.drawable.location_on),
                            contentDescription = null,
                            tint = Color(0xFFB0B4C0),
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    text = "Indonesia",
                )

                Spacer(Modifier.height(16.dp))

                // Story Card 1: ABOUT LYRA MUSIC
                StoryCard {
                    Text(
                        text = "ABOUT LYRA MUSIC",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.2.sp,
                            color = LabelGray,
                        ),
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_lyra_logo),
                            contentDescription = "LYRA Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(64.dp),
                        )

                        Spacer(Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "LYRA",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 28.sp,
                                    letterSpacing = 0.5.sp,
                                    color = Color.White,
                                ),
                            )
                            Text(
                                text = "Music for a brighter you",
                                style = TextStyle(
                                    fontFamily = InterFontFamily,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = LyraGold,
                                ),
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "LYRA bukan sekadar nama sebuah aplikasi musik. Di baliknya, ada cerita sederhana tentang cahaya, kekaguman, dan seseorang yang menginspirasi tanpa pernah harus menjadi pusat perhatian.",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = BodyTextGray,
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Story Card 2: L — Luminous
                StoryCard {
                    Text(
                        text = "L — Luminous",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LyraGold,
                        ),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Melambangkan cahaya, keindahan, dan harapan yang membuat sesuatu terasa lebih berarti.",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = BodyTextGray,
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Story Card 3: Y • RA (D)
                StoryCard {
                    Text(
                        text = "Y • RA (D)",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = LyraGold,
                        ),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Sebuah rangkaian inisial yang menyimpan cerita pribadi. Tidak semua maknanya perlu dijelaskan kepada dunia.",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = BodyTextGray,
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Story Card 4: Closing Reflection
                StoryCard {
                    Text(
                        text = "Bagi saya, LYRA adalah cara untuk menyimpan sebuah cerita dalam sesuatu yang nyata. Seperti musik, ada hal-hal yang tidak selalu bisa dijelaskan dengan kata-kata, tetapi tetap memiliki arti.",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = BodyTextGray,
                        ),
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Story Card 5: Hubungi Developer
                StoryCard {
                    Text(
                        text = "Hubungi Developer",
                        style = TextStyle(
                            fontFamily = InterFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White,
                        ),
                    )

                    Spacer(Modifier.height(12.dp))

                    ContactInnerBox(
                        iconContent = {
                            Icon(
                                painter = painterResource(R.drawable.github),
                                contentDescription = null,
                                tint = LyraGold,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                        label = "github.com/dinzid04/Linefy",
                        onClick = { context.openUrl(GitHubRepoUrl) },
                    )

                    Spacer(Modifier.height(8.dp))

                    ContactInnerBox(
                        iconContent = {
                            Icon(
                                imageVector = MailOutlineIcon,
                                contentDescription = null,
                                tint = LyraGold,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                        label = "gadingkencana05@gmail.com",
                        onClick = { context.openUrl(DeveloperEmailUrl) },
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Version Badge at bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(StoryCardSurface)
                            .border(1.dp, StoryCardBorder, RoundedCornerShape(50))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "Lyra | $installed",
                            style = TextStyle(
                                fontFamily = InterFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = SubtitleGray,
                            ),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
}

@Composable
private fun DeveloperInfoPill(
    iconContent: @Composable () -> Unit,
    text: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(StoryCardSurface)
            .border(1.dp, InfoPillBorder, RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        iconContent()
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color(0xFFD0D4DC),
            ),
        )
    }
}

@Composable
private fun StoryCard(
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(StoryCardSurface)
            .border(1.dp, StoryCardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
private fun ContactInnerBox(
    iconContent: @Composable () -> Unit,
    label: String,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .pressBounce(interaction, pressedScale = 0.97f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1C22))
            .border(1.dp, Color(0xFF2C2F38), RoundedCornerShape(12.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        iconContent()
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            style = TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
                color = Color.White,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
internal fun SheetPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    background: Color = Color.White,
    contentColor: Color = Color.Black,
    leadingIcon: Int? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .pressBounce(interaction, pressedScale = 0.96f)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) background else background.copy(alpha = 0.4f))
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            )
            .padding(horizontal = 18.dp),
    ) {
        Spacer(Modifier.weight(1f))
        if (leadingIcon != null) {
            Icon(
                painter = painterResource(leadingIcon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = TextStyle(
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = (-0.3).sp,
                color = contentColor,
            ),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.weight(1f))
    }
}

internal fun shortVersion(raw: String): String =
    raw.trim().removePrefix("v").removePrefix("V").substringBefore('-')

internal fun Context.openUrl(url: String) {
    runCatching {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

private val XLogoIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "XLogo",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.White),
            pathFillType = PathFillType.NonZero,
        ) {
            moveTo(18.244f, 2.25f)
            horizontalLineToRelative(3.308f)
            lineToRelative(-7.227f, 8.26f)
            lineToRelative(8.502f, 11.24f)
            horizontalLineTo(16.17f)
            lineToRelative(-5.214f, -6.817f)
            lineTo(4.99f, 21.75f)
            horizontalLineTo(1.68f)
            lineToRelative(7.73f, -8.835f)
            lineTo(1.254f, 2.25f)
            horizontalLineTo(8.08f)
            lineToRelative(4.713f, 6.231f)
            close()
            moveTo(17.083f, 19.77f)
            horizontalLineToRelative(1.833f)
            lineTo(7.084f, 4.126f)
            horizontalLineTo(5.117f)
            close()
        }
    }.build()
}

private val MailOutlineIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "MailOutline",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(4f, 4f)
            horizontalLineTo(20f)
            curveTo(21.1f, 4f, 22f, 4.9f, 22f, 6f)
            verticalLineTo(18f)
            curveTo(22f, 19.1f, 21.1f, 20f, 20f, 20f)
            horizontalLineTo(4f)
            curveTo(2.9f, 20f, 2f, 19.1f, 2f, 18f)
            verticalLineTo(6f)
            curveTo(2f, 4.9f, 2.9f, 4f, 4f, 4f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(22f, 6f)
            lineTo(12f, 13f)
            lineTo(2f, 6f)
        }
    }.build()
}

private val GraduationCapIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "GraduationCap",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(22f, 10f)
            lineTo(12f, 5f)
            lineTo(2f, 10f)
            lineTo(12f, 15f)
            lineTo(22f, 10f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.White),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(6f, 12f)
            verticalLineTo(17f)
            curveTo(6f, 18.66f, 8.69f, 20f, 12f, 20f)
            curveTo(15.31f, 20f, 18f, 18.66f, 18f, 17f)
            verticalLineTo(12f)
        }
    }.build()
}
