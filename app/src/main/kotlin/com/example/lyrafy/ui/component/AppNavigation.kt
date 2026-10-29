// AppNavigation.kt

package com.example.lyrafy.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.lyrafy.R
import com.example.lyrafy.constants.LiquidGlassNavbarKey
import com.example.lyrafy.ui.screens.Screens
import com.example.lyrafy.utils.rememberPreference
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import com.example.lyrafy.LocalGlassState

@Immutable
private data class NavItemState(
    val isSelected: Boolean,
    val iconRes: Int
)

/**
 * Navigates to a top-level tab exactly the way the navigation bar does.
 *
 * Anything that jumps to a tab from inside another tab MUST use this rather than a plain
 * `navigate(route)`, and the reason is subtle enough to be worth spelling out.
 *
 * A plain `navigate("settings")` from the profile menu pushes Settings on top of the *Home* tab's
 * stack, because that is where you were standing. Tapping Home in the bar then runs
 * `popUpTo(startDestination) { saveState = true }`, which sweeps that pushed Settings entry into
 * the state saved *for Home* — and `restoreState = true` on the very same call immediately puts it
 * back. The result is that tapping Home lands you on Settings, which is precisely the bug this
 * fixes, and it looks like the bar is ignoring you when in fact it is faithfully restoring a stack
 * that Settings should never have been part of.
 *
 * Going through here instead means a tab is always entered *as* a tab, so it never becomes a child
 * of whichever tab happened to be on screen when you left.
 */
fun NavController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * a tap on a tab. another tab: switch to it, back on the page you left it on. the tab you're in:
 * from one of its pages, back to the tab's own page (as every tab bar does); already there, the
 * page scrolls to its top.
 */
fun NavController.onTabTapped(screen: Screens, isSelectedTab: Boolean, scrollToTop: () -> Unit) {
    if (!isSelectedTab) {
        navigateToTab(screen.route)
        return
    }
    if (currentDestination?.route == screen.route) {
        currentBackStackEntry?.savedStateHandle?.set("scrollToTop", true)
        scrollToTop()
        return
    }
    if (!popBackStack(screen.route, inclusive = false)) navigateToTab(screen.route)
}

@Stable
private fun isRouteSelected(currentRoute: String?, screenRoute: String, navigationItems: List<Screens>): Boolean {
    if (currentRoute == null) return false
    if (currentRoute == screenRoute) return true
    return navigationItems.any { it.route == screenRoute } &&
           currentRoute.startsWith("$screenRoute/")
}

private val RouteOwners: List<Pair<String, String>> = listOf(
    "new_release" to "explore",
    "search/" to "search_input",
    "genre/" to "search_input",
    "youtube_browse/" to "search_input",
    "browse/" to "search_input",
    "library/" to "library",
    "cipher_settings" to "settings",
    "playback_diagnostics" to "settings",
    "lyrafy_settings" to "settings",
    "general_settings" to "settings",
    "appearance_settings" to "settings",
    "playback_settings" to "settings",
    "experimental_settings" to "settings",
    "storage_settings" to "settings",
    "import_sync" to "settings",
    "import_providers" to "settings",
    "import_tunemymusic" to "settings",
    "import_progress" to "settings",
    "youtube_sync" to "settings",
    "youtube_login" to "settings",
    "account_import/" to "settings",
    "equalizer" to "settings",
)

private fun owningTabRoute(currentRoute: String?, navigationItems: List<Screens>): String? {
    if (currentRoute == null) return null
    navigationItems.firstOrNull { isRouteSelected(currentRoute, it.route, navigationItems) }
        ?.let { return it.route }
    RouteOwners.firstOrNull { (prefix, _) ->
        currentRoute == prefix || currentRoute.startsWith(prefix)
    }?.let { return it.second }

    // Catch-all so RouteOwners does not have to stay exhaustive: every settings sub-screen is
    // owned by the settings tab without anyone having to list it. Only a fallback now; the back
    // stack decides the tab whenever it has a tab page in it (see owningTab).
    if (currentRoute.endsWith("_settings") || currentRoute.startsWith("settings")) return "settings"
    return null
}

/**
 * the tab the page on screen belongs to: the nearest tab page under it in the back stack.
 *
 * the bar used to remember the last tab it could name from the current route alone, and keep it
 * for routes no tab claims (albums, playlists, artists). that went wrong two ways: switching back
 * to a tab restores the page you left it on, so coming back to the Library's album kept Home lit;
 * and the remembered value was dropped whenever the bar left the screen (the player's full view,
 * the recap), so it came back as Home. the back stack always knows: a tab is entered with
 * [navigateToTab], so every page sits above the tab page it was opened from.
 */
fun owningTab(backStack: List<NavBackStackEntry>, navigationItems: List<Screens>): String {
    val tabRoutes = navigationItems.mapTo(HashSet()) { it.route }
    backStack.asReversed().firstOrNull { it.destination.route in tabRoutes }
        ?.destination?.route
        ?.let { return it }
    // a stack without a tab page under it (a deep link): go by the route, else the first tab
    return owningTabRoute(backStack.lastOrNull()?.destination?.route, navigationItems)
        ?: navigationItems.firstOrNull()?.route.orEmpty()
}

/**
 * which tab each page belongs to, kept by entry so a page that has already left the stack (the
 * one a tab switch saved away) can still be placed. the tab slide reads this: an album open in
 * the Library slides like the Library, not like a page no tab owns.
 */
class TabOwnership(private val navigationItems: List<Screens>) {
    private val tabRoutes = navigationItems.map { it.route }
    private val owners = HashMap<String, String>()

    fun update(backStack: List<NavBackStackEntry>) {
        var owner: String? = null
        backStack.forEach { entry ->
            val route = entry.destination.route
            if (route != null && route in tabRoutes) owner = route
            owner?.let { owners[entry.id] = it }
        }
        // saved tab stacks keep their entries' ids, so only trim when it has clearly grown stale
        if (owners.size > 400) {
            val live = backStack.mapTo(HashSet()) { it.id }
            owners.keys.retainAll(live)
        }
    }

    /** the tab's position in the bar, or -1 when nothing places it */
    fun indexOf(entry: NavBackStackEntry, backStack: List<NavBackStackEntry>): Int {
        update(backStack)
        val owner = owners[entry.id] ?: entry.destination.route?.takeIf { it in tabRoutes }
            ?: owningTabRoute(entry.destination.route, navigationItems)
        return tabRoutes.indexOf(owner)
    }
}

@Composable
fun AppNavigationRail(
    navigationItems: List<Screens>,
    currentRoute: String?,
    /** from [owningTab]: the tab the page on screen belongs to */
    selectedTabRoute: String,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    /** a second quick tap on Search: straight into typing */
    onSearchDoubleTap: (() -> Unit)? = null
) {
    val containerColor = if (pureBlack) Color.Black else MaterialTheme.colorScheme.surfaceContainer
    val viewConfiguration = LocalViewConfiguration.current

    NavigationRail(
        modifier = modifier,
        containerColor = containerColor
    ) {
        Spacer(modifier = Modifier.weight(1f))

        navigationItems.forEach { screen ->
            val isSelected = screen.route == selectedTabRoute
            val iconRes = remember(isSelected, screen) {
                if (isSelected) screen.iconIdActive else screen.iconIdInactive
            }

            val lastTap = remember { longArrayOf(0L) }

            NavigationRailItem(
                selected = isSelected,
                onClick = {
                    val doubleTap = onSearchDoubleTap.takeIf { screen == Screens.Search }
                    tabTapped(lastTap, viewConfiguration.doubleTapTimeoutMillis, doubleTap) {
                        onItemClick(screen, isSelected)
                    }
                },
                icon = {
                    val isLyraLogo = screen == Screens.Settings || iconRes == R.drawable.ic_lyra_logo
                    Icon(
                        painter = if (isLyraLogo) painterResource(R.drawable.ic_lyra_logo) else coil3.compose.rememberAsyncImagePainter(model = iconRes),
                        contentDescription = stringResource(screen.titleId),
                        tint = if (isLyraLogo) Color.Unspecified else Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

/** The bar itself, a stadium the mini player's 64dp matches. */
private val NavBarHeight = 64.dp
private val SlimNavBarHeight = 56.dp
private val NavBarShape = RoundedCornerShape(50)
private val LiquidGlassNavBarShape = RoundedCornerShape(32.dp)
private val LiquidGlassCapsuleShape = RoundedCornerShape(50)

/** The lit tab's highlight sits this far in from the bar's top, bottom and both ends. */
private val NavHighlightInset = 8.dp

/** Room between a highlight and where its neighbour's would be. */
private val NavHighlightGap = 4.dp

/** How bright an unlit tab's icon is. */
private const val UnlitIconAlpha = 0.7f

/**
 * The highlight's front edge springs to the new tab faster than its back edge, so it stretches
 * toward the tab and settles into it instead of sliding over as a rigid block.
 */
private val NavHighlightLead = spring<Float>(dampingRatio = 0.82f, stiffness = 700f)
private val NavHighlightTrail = spring<Float>(dampingRatio = 0.82f, stiffness = 320f)

@Composable
fun AppNavigationBar(
    navigationItems: List<Screens>,
    currentRoute: String?,
    /** from [owningTab]: the tab the page on screen belongs to */
    selectedTabRoute: String,
    onItemClick: (Screens, Boolean) -> Unit,
    modifier: Modifier = Modifier,
    pureBlack: Boolean = false,
    slimNav: Boolean = false,
    /** a second quick tap on Search: straight into typing */
    onSearchDoubleTap: (() -> Unit)? = null
) {
    val glassState = LocalGlassState.current ?: remember { GlassState() }
    val accentWash = rememberChromeAccentWash()
    val (liquidGlassNavbar) = rememberPreference(LiquidGlassNavbarKey, defaultValue = true)
    val isDark = isSystemInDarkTheme() || pureBlack
    val primaryColor = MaterialTheme.colorScheme.primary
    val selectedIndex = navigationItems.indexOfFirst { it.route == selectedTabRoute }
    val barHeight = if (slimNav) SlimNavBarHeight else NavBarHeight

    if (liquidGlassNavbar) {
        val dockTabs = remember { listOf(Screens.Home, Screens.Explore, Screens.Library) }
        val activeDockIndex = dockTabs.indexOfFirst { it.route == selectedTabRoute }
        val isSearchSelected = selectedTabRoute == Screens.Search.route

        val frostedSurfaceColor = when {
            pureBlack -> Color(0xFF0A0B0E).copy(alpha = 0.78f)
            isDark -> Color(0xFF14151A).copy(alpha = 0.72f)
            else -> Color(0xFF14151A).copy(alpha = 0.72f)
        }
        val specularBorderBrush = remember {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0.10f),
                    Color.White.copy(alpha = 0.05f),
                    Color.White.copy(alpha = 0.28f)
                ),
                start = Offset.Zero,
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }
        val searchActiveBorderBrush = remember {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.72f),
                    Color.White.copy(alpha = 0.22f),
                    Color.White.copy(alpha = 0.42f)
                ),
                start = Offset.Zero,
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }

        val haptic = LocalHapticFeedback.current
        var dockWidthPx by remember { mutableIntStateOf(0) }
        var dragIndex by remember { mutableStateOf<Float?>(null) }
        var lastHapticSlot by remember { mutableIntStateOf(-1) }

        val targetSlot = dragIndex ?: activeDockIndex.coerceAtLeast(0).toFloat()
        val animatedSlot by animateFloatAsState(
            targetValue = targetSlot,
            animationSpec = spring(dampingRatio = 0.78f, stiffness = 380f),
            label = "liquid_glass_dock_slot"
        )
        val pillAlpha by animateFloatAsState(
            targetValue = if (activeDockIndex >= 0 || dragIndex != null) 1f else 0f,
            animationSpec = tween(durationMillis = 200),
            label = "liquid_glass_pill_alpha"
        )

        Box(modifier = modifier) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left/Main Capsule Dock (Home, Explore, Library)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(barHeight)
                        .onSizeChanged { dockWidthPx = it.width }
                        .press3D(maxTilt = 3.5f, pressedScale = 0.985f)
                        .dropShadow(LiquidGlassCapsuleShape, ChromeShadow)
                        .clip(LiquidGlassCapsuleShape)
                        .border(
                            width = 1.dp,
                            brush = specularBorderBrush,
                            shape = LiquidGlassCapsuleShape
                        )
                        .pointerInput(dockTabs) {
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    if (dockWidthPx > 0) {
                                        val slotW = dockWidthPx.toFloat() / dockTabs.size
                                        val initial = ((offset.x / slotW) - 0.5f)
                                            .coerceIn(0f, (dockTabs.size - 1).toFloat())
                                        dragIndex = initial
                                        lastHapticSlot = initial.roundToInt()
                                    }
                                },
                                onHorizontalDrag = { change, _ ->
                                    change.consume()
                                    if (dockWidthPx > 0) {
                                        val slotW = dockWidthPx.toFloat() / dockTabs.size
                                        val current = ((change.position.x / slotW) - 0.5f)
                                            .coerceIn(0f, (dockTabs.size - 1).toFloat())
                                        dragIndex = current
                                        val nearest = current.roundToInt().coerceIn(0, dockTabs.size - 1)
                                        if (nearest != lastHapticSlot) {
                                            lastHapticSlot = nearest
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            val targetScreen = dockTabs[nearest]
                                            if (targetScreen.route != selectedTabRoute) {
                                                onItemClick(targetScreen, false)
                                            }
                                        }
                                    }
                                },
                                onDragEnd = {
                                    val finalFraction = dragIndex
                                    dragIndex = null
                                    lastHapticSlot = -1
                                    if (finalFraction != null) {
                                        val landedIndex = finalFraction.roundToInt().coerceIn(0, dockTabs.size - 1)
                                        val targetScreen = dockTabs[landedIndex]
                                        if (targetScreen.route != selectedTabRoute) {
                                            onItemClick(targetScreen, false)
                                        }
                                    }
                                },
                                onDragCancel = {
                                    dragIndex = null
                                    lastHapticSlot = -1
                                }
                            )
                        }
                        .drawWithCache {
                            val sheenBrush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.14f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = size.height * 0.45f
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(brush = sheenBrush)
                            }
                        }
                ) {
                    GlassPillBackground(
                        state = glassState,
                        blurRadius = { GlassChromeBlurRadius },
                        tint = frostedSurfaceColor,
                        foundationColor = if (pureBlack) Color.Black else GlassChromeColor,
                        accentWash = accentWash,
                        tileMode = android.graphics.Shader.TileMode.CLAMP,
                        bleed = true,
                        modifier = Modifier.fillMaxSize(),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 6.dp)
                            .slidingLiquidGlassLens(
                                animatedSlot = { animatedSlot },
                                pillAlpha = { pillAlpha },
                                tabCount = dockTabs.size
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dockTabs.forEach { screen ->
                            DetachedDockTab(
                                screen = screen,
                                isSelected = screen.route == selectedTabRoute,
                                onItemClick = onItemClick,
                            )
                        }
                    }
                }

                // Right Detached Circular Search Button (64.dp CircleShape)
                DetachedSearchCircleButton(
                    isSelected = isSearchSelected,
                    size = barHeight,
                    glassState = glassState,
                    frostedSurfaceColor = frostedSurfaceColor,
                    pureBlack = pureBlack,
                    accentWash = accentWash,
                    borderBrush = if (isSearchSelected) searchActiveBorderBrush else specularBorderBrush,
                    onClick = { onItemClick(Screens.Search, isSearchSelected) },
                    onDoubleTap = onSearchDoubleTap
                )
            }
        }
    } else {
        val classicItems = remember(navigationItems) {
            navigationItems.filter { it != Screens.Explore }
        }
        val classicSelectedIndex = classicItems.indexOfFirst { it.route == selectedTabRoute }
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .align(Alignment.TopCenter)
                    .height(barHeight)
                    .press3D(maxTilt = 4f, pressedScale = 0.985f)
                    .dropShadow(NavBarShape, ChromeShadow)
                    .clip(NavBarShape)
                    .drawWithContent {
                        drawContent()
                        drawChromeOutline(Offset.Zero, size, cornerRadius = size.height / 2f)
                    }
            ) {
                FrostedChromeGlass(
                    state = glassState,
                    pureBlack = pureBlack,
                    accentWash = accentWash,
                    modifier = Modifier.fillMaxSize(),
                )

                // Half the gap is the row's padding and half is inside each slot, so the highlight sits
                // the same distance from the ends as from the top and bottom.
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = NavHighlightInset - NavHighlightGap / 2)
                        .navHighlight(classicSelectedIndex, classicItems.size),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    classicItems.forEach { screen ->
                        // lit = the tab this page belongs to. what a tap on it does (scroll to the top
                        // on the tab's own page, back to that page from deeper in) is the caller's call
                        NavTab(
                            screen = screen,
                            isSelected = screen.route == selectedTabRoute,
                            liquidGlass = false,
                            primaryColor = primaryColor,
                            onItemClick = onItemClick,
                            onDoubleTap = onSearchDoubleTap.takeIf { screen == Screens.Search },
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive sliding Liquid Glass pill lens indicator inside the 3-tab capsule dock.
 * Draws a translucent glass fill, inner radial/vertical light refraction, and a 1.2.dp
 * directional specular border that glows brightest on the active edge.
 */
@Composable
private fun Modifier.slidingLiquidGlassLens(
    animatedSlot: () -> Float,
    pillAlpha: () -> Float,
    tabCount: Int,
): Modifier = drawWithCache {
    val verticalInset = 5.dp.toPx()
    val horizontalGap = 3.dp.toPx()
    val pillHeight = (size.height - verticalInset * 2f).coerceAtLeast(1f)
    val borderStrokeWidth = 1.2.dp.toPx()
    val fillColor = Color.White.copy(alpha = 0.12f)
    val topRefractionBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.20f),
            Color.White.copy(alpha = 0.04f),
            Color.Transparent
        ),
        startY = verticalInset,
        endY = verticalInset + pillHeight * 0.65f
    )

    onDrawBehind {
        val alpha = pillAlpha()
        if (alpha <= 0.001f || tabCount <= 0) return@onDrawBehind

        val slotWidth = size.width / tabCount
        val slot = animatedSlot().coerceIn(0f, (tabCount - 1).toFloat())
        val left = slot * slotWidth + horizontalGap
        val pillWidth = (slotWidth - horizontalGap * 2f).coerceAtLeast(1f)
        val topLeft = Offset(left, verticalInset)
        val pillSize = Size(pillWidth, pillHeight)
        val corner = CornerRadius(pillHeight / 2f)

        // Base translucent glass fill
        drawRoundRect(
            color = fillColor,
            topLeft = topLeft,
            size = pillSize,
            cornerRadius = corner,
            alpha = alpha
        )

        // Inner top light refraction
        drawRoundRect(
            brush = topRefractionBrush,
            topLeft = topLeft,
            size = pillSize,
            cornerRadius = corner,
            alpha = alpha
        )

        // Radial inner lens glow
        val center = Offset(left + pillWidth / 2f, verticalInset + pillHeight * 0.35f)
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.14f),
                    Color.Transparent
                ),
                center = center,
                radius = (pillWidth * 0.7f).coerceAtLeast(1f)
            ),
            topLeft = topLeft,
            size = pillSize,
            cornerRadius = corner,
            alpha = alpha
        )

        // Directional specular 1.2.dp border where the edge closest to the active tab glows brighter
        val normalizedPos = if (tabCount > 1) slot / (tabCount - 1).toFloat() else 0.5f
        val leftEdgeAlpha = (0.65f - normalizedPos * 0.40f).coerceIn(0.18f, 0.65f)
        val rightEdgeAlpha = (0.25f + normalizedPos * 0.40f).coerceIn(0.18f, 0.65f)
        val specularRimBrush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = leftEdgeAlpha),
                Color.White.copy(alpha = 0.14f),
                Color.White.copy(alpha = rightEdgeAlpha)
            ),
            start = Offset(left, verticalInset),
            end = Offset(left + pillWidth, verticalInset + pillHeight)
        )

        drawRoundRect(
            brush = specularRimBrush,
            topLeft = topLeft,
            size = pillSize,
            cornerRadius = corner,
            style = Stroke(width = borderStrokeWidth),
            alpha = alpha
        )
    }
}

@Composable
private fun RowScope.DetachedDockTab(
    screen: Screens,
    isSelected: Boolean,
    onItemClick: (Screens, Boolean) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val lit by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dock_tab_lit"
    )

    val unlitIcon = coil3.compose.rememberAsyncImagePainter(model = screen.iconIdInactive)
    val litIcon = coil3.compose.rememberAsyncImagePainter(model = screen.iconIdActive)
    val iconSize = if (screen == Screens.Home) 20.dp else 21.dp
    val labelText = when (screen) {
        Screens.Home -> "Home"
        Screens.Explore -> "Explore"
        Screens.Library -> "Library"
        else -> stringResource(screen.titleId)
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .pressBounce(interactionSource, pressedScale = 0.90f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onItemClick(screen, isSelected) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (screen == Screens.Library) {
                LibraryDownloadIcon { TabIcons(screen, unlitIcon, litIcon, iconSize) { lit } }
            } else {
                TabIcons(screen, unlitIcon, litIcon, iconSize) { lit }
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = labelText,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                color = Color.White.copy(alpha = 0.68f + 0.32f * lit),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DetachedSearchCircleButton(
    isSelected: Boolean,
    size: androidx.compose.ui.unit.Dp,
    glassState: GlassState,
    frostedSurfaceColor: Color,
    pureBlack: Boolean,
    accentWash: (() -> Color)?,
    borderBrush: Brush,
    onClick: () -> Unit,
    onDoubleTap: (() -> Unit)?,
) {
    val viewConfiguration = LocalViewConfiguration.current
    val interactionSource = remember { MutableInteractionSource() }
    val lastTap = remember { longArrayOf(0L) }
    val lit by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "search_circle_lit"
    )
    val unlitIcon = coil3.compose.rememberAsyncImagePainter(model = Screens.Search.iconIdInactive)
    val litIcon = coil3.compose.rememberAsyncImagePainter(model = Screens.Search.iconIdActive)

    Box(
        modifier = Modifier
            .size(size)
            .press3D(maxTilt = 4f, pressedScale = 0.92f)
            .dropShadow(CircleShape, ChromeShadow)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 1.2.dp else 1.dp,
                brush = borderBrush,
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    tabTapped(lastTap, viewConfiguration.doubleTapTimeoutMillis, onDoubleTap, onClick)
                }
            )
            .drawWithCache {
                val sheenBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.18f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = this.size.height * 0.48f
                )
                onDrawWithContent {
                    drawContent()
                    drawRect(brush = sheenBrush)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        GlassPillBackground(
            state = glassState,
            blurRadius = { GlassChromeBlurRadius },
            tint = frostedSurfaceColor,
            foundationColor = if (pureBlack) Color.Black else GlassChromeColor,
            accentWash = accentWash,
            tileMode = android.graphics.Shader.TileMode.CLAMP,
            bleed = true,
            modifier = Modifier.fillMaxSize(),
        )

        if (lit > 0.01f) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.14f * lit))
            )
        }

        TabIcons(Screens.Search, unlitIcon, litIcon, 22.dp) { lit }
    }
}

/**
 * Metrolist-style Liquid Glass sliding capsule highlight behind the active tab.
 * Uses drawWithCache & drawBehind so the spring slide never recomposes the bar.
 */
@Composable
private fun Modifier.liquidGlassNavHighlight(
    selectedIndex: Int,
    tabCount: Int,
    primaryColor: Color,
): Modifier {
    val start = remember { Animatable(selectedIndex.coerceAtLeast(0).toFloat()) }
    val end = remember { Animatable(selectedIndex.coerceAtLeast(0).toFloat()) }
    val shown = remember { Animatable(if (selectedIndex >= 0) 1f else 0f) }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex < 0) {
            shown.animateTo(0f, tween(200))
            return@LaunchedEffect
        }
        val target = selectedIndex.toFloat()
        if (shown.value == 0f) {
            start.snapTo(target)
            end.snapTo(target)
        }
        launch { shown.animateTo(1f, tween(200)) }
        val movingRight = target > (start.value + end.value) / 2f
        launch { start.animateTo(target, if (movingRight) NavHighlightTrail else NavHighlightLead) }
        end.animateTo(target, if (movingRight) NavHighlightLead else NavHighlightTrail)
    }

    return drawWithCache {
        val inset = NavHighlightInset.toPx()
        val height = (size.height - inset * 2f).coerceAtLeast(1f)
        val borderStrokeWidth = 1.dp.toPx()
        val pillBorderBrush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                primaryColor.copy(alpha = 0.15f)
            ),
            startY = inset,
            endY = inset + height
        )
        val pillSheenBrush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.22f),
                Color.Transparent
            ),
            startY = inset,
            endY = inset + height * 0.5f
        )
        val fillColor = primaryColor.copy(alpha = 0.24f)

        onDrawBehind {
            val alpha = shown.value
            if (alpha <= 0f || tabCount == 0) return@onDrawBehind
            val slot = size.width / tabCount
            val halfGap = NavHighlightGap.toPx() / 2f
            val left = start.value * slot + halfGap
            val right = (end.value + 1f) * slot - halfGap
            val pillSize = Size((right - left).coerceAtLeast(1f), height)
            val corner = CornerRadius(height / 2f)
            val topLeft = Offset(left, inset)

            drawRoundRect(
                color = fillColor,
                topLeft = topLeft,
                size = pillSize,
                cornerRadius = corner,
                alpha = alpha,
            )
            drawRoundRect(
                brush = pillSheenBrush,
                topLeft = topLeft,
                size = pillSize,
                cornerRadius = corner,
                alpha = alpha,
            )
            drawRoundRect(
                brush = pillBorderBrush,
                topLeft = topLeft,
                size = pillSize,
                cornerRadius = corner,
                style = Stroke(width = borderStrokeWidth),
                alpha = alpha,
            )
        }
    }
}

/**
 * One highlight behind the lit tab that slides to the next one, instead of each tab fading its own
 * in and out. Only read while drawing, so the slide never recomposes the bar.
 */
@Composable
private fun Modifier.navHighlight(selectedIndex: Int, tabCount: Int): Modifier {
    val start = remember { Animatable(selectedIndex.coerceAtLeast(0).toFloat()) }
    val end = remember { Animatable(selectedIndex.coerceAtLeast(0).toFloat()) }
    val shown = remember { Animatable(if (selectedIndex >= 0) 1f else 0f) }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex < 0) {
            shown.animateTo(0f, tween(200))
            return@LaunchedEffect
        }
        val target = selectedIndex.toFloat()
        // back from no lit tab, it fades in where it belongs rather than sliding over from the old one
        if (shown.value == 0f) {
            start.snapTo(target)
            end.snapTo(target)
        }
        launch { shown.animateTo(1f, tween(200)) }
        val movingRight = target > (start.value + end.value) / 2f
        launch { start.animateTo(target, if (movingRight) NavHighlightTrail else NavHighlightLead) }
        end.animateTo(target, if (movingRight) NavHighlightLead else NavHighlightTrail)
    }

    return drawBehind {
        val alpha = shown.value
        if (alpha <= 0f || tabCount == 0) return@drawBehind
        val slot = size.width / tabCount
        val halfGap = NavHighlightGap.toPx() / 2f
        val inset = NavHighlightInset.toPx()
        val left = start.value * slot + halfGap
        val right = (end.value + 1f) * slot - halfGap
        val height = size.height - inset * 2f
        drawRoundRect(
            color = ChromeHighlightColor,
            topLeft = Offset(left, inset),
            size = Size(right - left, height),
            cornerRadius = CornerRadius(height / 2f),
            alpha = alpha,
        )
    }
}

/**
 * A tab's tap, or with [onDoubleTap] a second tap close behind the first: the first still goes to
 * the tab at once, the second does [onDoubleTap] instead. [lastTap] holds the time of the one before.
 */
private fun tabTapped(lastTap: LongArray, doubleTapTimeoutMillis: Long, onDoubleTap: (() -> Unit)?, onTap: () -> Unit) {
    val now = android.os.SystemClock.uptimeMillis()
    if (onDoubleTap != null && now - lastTap[0] < doubleTapTimeoutMillis) {
        lastTap[0] = 0L
        onDoubleTap()
    } else {
        lastTap[0] = now
        onTap()
    }
}

/**
 * One tab of the bar. Its icon swaps to the filled one and brightens as it lights up, and it
 * squishes under a press. [onDoubleTap] is for Search, where two taps open the search field.
 * The Library's icon turns into a download badge while songs download.
 */
@Composable
private fun RowScope.NavTab(
    screen: Screens,
    isSelected: Boolean,
    liquidGlass: Boolean = true,
    primaryColor: Color = Color.White,
    onItemClick: (Screens, Boolean) -> Unit,
    onDoubleTap: (() -> Unit)?,
) {
    val viewConfiguration = LocalViewConfiguration.current
    val interactionSource = remember { MutableInteractionSource() }
    val lastTap = remember { longArrayOf(0L) }
    val lit = animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "nav_tab_lit"
    )

    // Both icons stay loaded, so lighting a tab never shows an empty frame while its filled icon loads.
    val isLyraLogo = screen == Screens.Settings || screen.iconIdInactive == R.drawable.ic_lyra_logo
    val unlitIcon = if (isLyraLogo) painterResource(R.drawable.ic_lyra_logo) else coil3.compose.rememberAsyncImagePainter(model = screen.iconIdInactive)
    val litIcon = if (isLyraLogo) painterResource(R.drawable.ic_lyra_logo) else coil3.compose.rememberAsyncImagePainter(model = screen.iconIdActive)
    val iconSize = if (screen == Screens.Home || screen == Screens.Search) 22.dp else 24.dp

    val capsuleBorderBrush = remember(primaryColor) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.45f),
                primaryColor.copy(alpha = 0.15f)
            )
        )
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .pressBounce(interactionSource, pressedScale = 0.85f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    tabTapped(lastTap, viewConfiguration.doubleTapTimeoutMillis, onDoubleTap) {
                        onItemClick(screen, isSelected)
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (liquidGlass) {
            Box(
                modifier = Modifier
                    .padding(horizontal = NavHighlightGap / 2, vertical = NavHighlightInset)
                    .fillMaxSize()
                    .graphicsLayer {
                        val progress = lit.value
                        alpha = progress
                        val scale = 0.88f + 0.12f * progress
                        scaleX = scale
                        scaleY = scale
                        compositingStrategy = CompositingStrategy.ModulateAlpha
                    }
                    .clip(LiquidGlassCapsuleShape)
                    .background(
                        color = primaryColor.copy(alpha = 0.24f),
                        shape = LiquidGlassCapsuleShape
                    )
                    .border(
                        width = 1.dp,
                        brush = capsuleBorderBrush,
                        shape = LiquidGlassCapsuleShape
                    )
            )
        }
        if (screen == Screens.Library) {
            LibraryDownloadIcon { TabIcons(screen, unlitIcon, litIcon, iconSize) { lit.value } }
        } else {
            TabIcons(screen, unlitIcon, litIcon, iconSize) { lit.value }
        }
    }
}

/** a tab's two icons, the filled one fading in over the outline as the tab lights up */
@Composable
private fun TabIcons(
    screen: Screens,
    unlitIcon: androidx.compose.ui.graphics.painter.Painter,
    litIcon: androidx.compose.ui.graphics.painter.Painter,
    iconSize: androidx.compose.ui.unit.Dp,
    lit: () -> Float,
) {
    val iconTint = if (screen == Screens.Settings) Color.Unspecified else Color.White
    Box(contentAlignment = Alignment.Center) {
        Icon(
            painter = unlitIcon,
            contentDescription = stringResource(screen.titleId),
            tint = iconTint,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    alpha = UnlitIconAlpha * (1f - lit())
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                }
        )
        Icon(
            painter = litIcon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    alpha = lit()
                    compositingStrategy = CompositingStrategy.ModulateAlpha
                }
        )
    }
}
