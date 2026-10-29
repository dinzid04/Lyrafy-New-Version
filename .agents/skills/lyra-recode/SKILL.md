---
name: lyra-recode
description: >-
  Complete reference skill for Lyra (Lyrafy): Custom Lyra Harp & Orbit SVG logo
  replacement, Detached Pill + Circle Search Liquid Glass Navbar (as shown in
  reference video), QRIS Liquid Glass Support Sheet, Dinz Developer & About Lyra
  Screen, and Player Horizontal Swipe On/Off Toggle.
---

# Lyra Complete UI & Feature Specification

Always adhere to `.agents/skills/performance/SKILL.md` when building Compose UI and shaders.

## 1. Complete Logo Replacement (`ic_lyra_logo.xml`)
- The valid Android VectorDrawable for the new Lyra Harp & Orbit logo is already generated at:
  - `app/src/main/res/drawable/ic_lyra_logo.xml`
  - `app/src/main/res/drawable/ic_musicfy_mark.xml`
  - `app/src/main/res/drawable/musicfy_icon.xml`
  - `app/src/main/res/drawable/ic_splash_mark.xml`
- **CRITICAL**: Search the entire codebase (`HomeScreen.kt`, `AppNavigation.kt`, `AboutSheet.kt`, `ProfileScreen.kt`, `WelcomeStep.kt`, `LaunchIntro.kt`, `CardMoments.kt`, `OnboardingPages.kt`, etc.) for any custom `Canvas` or `ImageVector` or `painterResource` drawing the old triangle-wave Musicfy play icon.
- Replace ALL of them with `painterResource(R.drawable.ic_lyra_logo)` with `Color.Unspecified` tint so the golden & cream Lyra harp colors (`#E2B963` and `#F8EDDB`) render authentically!
- Specifically ensure:
  1. Top-left logo in `HomeScreen.kt` uses `R.drawable.ic_lyra_logo` (`tint = Color.Unspecified`).
  2. Loading / Explore header mark uses `R.drawable.ic_lyra_logo` (`tint = Color.Unspecified`).
  3. Any navbar or profile mark uses `R.drawable.ic_lyra_logo`.

## 2. Detached Liquid Glass Navbar (Exact Video & Screenshot Design)
When `LiquidGlassNavbarKey` (`liquidGlassNavbar`) is enabled in `AppNavigation.kt` / `PinnedBottomScaffold.kt`:
- **Layout Architecture (Detached Dock + Right Circle Button)**:
  - Outer `Row` at the bottom with `Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)`, `horizontalArrangement = Arrangement.spacedBy(12.dp)`, `verticalAlignment = Alignment.CenterVertically`.
  - **Left/Main Capsule Dock (`Modifier.weight(1f).height(64.dp)`)**:
    - Shape: `RoundedCornerShape(50)` (full pill capsule).
    - Contains the 3 main tabs: **Home**, **Explore**, and **Library** (each tab displays its **Icon on top** + **Text label below**: `"Home"`, `"Explore"`, `"Library"`).
    - Background: Frosted dark translucent glass (`Color(0xFF14151A).copy(alpha = 0.72f)`) with backdrop blur (`GlassPillBackground` / `DownscaledBlur`) and a 1.dp refractive specular border (`Brush.linearGradient` with bright white-alpha rim highlights).
    - **Interactive Sliding Liquid Glass Pill Indicator**:
      - A capsule indicator (`RoundedCornerShape(50)`) that smoothly slides/morphs horizontally across the 3 tabs using spring physics (`spring(dampingRatio = 0.78f, stiffness = 380f)`) and supports horizontal drag gesture across the dock so the user can slide the glass lens between Home, Explore, and Library just like in the reference video!
      - The active sliding pill has a translucent glass fill (`Color.White.copy(alpha = 0.12f)`) + radial/linear inner light refraction + a glowing 1.2.dp specular border (`Brush.linearGradient` where the edge closest to the active tab has a brighter white highlight `Color.White.copy(alpha = 0.65f)` fading to `0.12f`).
  - **Right Detached Circular Button (`Modifier.size(64.dp)`)**:
    - Shape: `CircleShape`.
    - Displays the **Search** (`🔍`) icon (or triggers Search navigation).
    - Styled with matching Liquid Glass surface (`Color(0xFF14151A).copy(alpha = 0.72f)`), backdrop blur, inner top sheen, and 1.dp specular `CircleShape` border (glowing brighter when Search is active).

## 3. Support Development / Donate Sheet with QRIS & Liquid Glass Overlay (`DonateSheet.kt`)
In `app/src/main/kotlin/com/example/musicfy/ui/screens/donate/DonateSheet.kt` (and `DonatePrompt.kt`):
- **Profile Avatar**: Replace the old anime avatar (`R.drawable.dev` / `dev_keycii`) with `R.drawable.dev_dinz_profile` (and fallback URL `"https://url.dinzid.my.id/HOjKIXC"`).
- **Header & Copywriting**:
  - Title: `"Support LYRA Development"` (or `"Dukung Pengembangan LYRA"`)
  - Description: `"Terima kasih telah menggunakan LYRA! Setiap dukungan yang kamu berikan sangat berarti untuk menjaga aplikasi ini tetap hidup, bebas iklan, dan terus berkembang menjadi lebih baik."`
- **Remove Old Links**: Completely delete the 3 buttons: `"Support me on KoFi!"`, `"Support me with SocialBuzz!"`, and `"Buy me a coffee!"`.
- **Direct QRIS Display + Fullscreen Liquid Glass QRIS Overlay**:
  - Directly display a sleek **QRIS Card** inside the sheet wrapped in a Liquid Glass container (frosted glass surface, 1.dp specular gradient border, golden accent badge `"QRIS • All E-Wallet & Mobile Banking"`, owner name `"Dinz / Gading Kencana"`, and the QRIS image).
  - Load the QRIS image from `R.drawable.qris_dinz` if present, or render a high-contrast stylised QRIS preview card with a tap action `"Ketuk untuk memperbesar QRIS"` that opens a **Liquid Glass Fullscreen Overlay** (with blurred backdrop, refractive glass card frame, pinch-to-zoom, and a Close/Done liquid glass pill button).

## 4. Redesigned "Version & About App" -> Developer & About LYRA Screen (`AboutSheet.kt`)
Transform `app/src/main/kotlin/com/example/musicfy/ui/screens/update/AboutSheet.kt` to match the exact reference design:
- **Top Section**:
  - Back button (`<-`) in a dark circular button (`Color(0xFF1A1B20)`).
  - Title: `"Developer"` (28.sp, Bold, White).
  - Subtitle: `"Di balik LYRA, ada satu orang yang percaya bahwa musik bisa membuat hidup lebih bermakna."` (13.sp, Gray `Color(0xFFA0A4B0)`).
- **Banner & Overlapping Avatar**:
  - Banner (`160.dp` height, full width): Load `R.drawable.dev_dinz_banner` (fallback `"https://url.dinzid.my.id/OiHa2Ll"`), `ContentScale.Crop`.
  - Overlapping Circular Avatar (`84.dp`, positioned at bottom-left of banner with `-42.dp` vertical offset, `2.dp` white border): Load `R.drawable.dev_dinz_profile` (fallback `"https://url.dinzid.my.id/HOjKIXC"`).
- **Identity & Bio**:
  - Name: `"Dinz"` (20.sp, Bold, White).
  - Handle: `"@GadingKecana"` (13.sp, `Color(0xFF9AA0A6)`).
  - Quote: `"\"Not every inspiration needs to be known. Some are simply meant to live on through what we create.\""` (13.5.sp, SemiBold, White).
  - **Social Icons Row** (clickable):
    1. **X** icon -> opens `"https://x.com/dinzid04"`
    2. **GitHub** icon (`R.drawable.github`) -> opens `"https://github.com/dinzid04/Linefy"`
    3. **Mail** icon -> opens `"mailto:gadingkencana05@gmail.com"`
  - **Info Pills** (2 full-width pill boxes, `RoundedCornerShape(50)`, surface `Color(0xFF121316)`, border `1.dp` `Color(0xFF2A2C32)`):
    1. Graduation icon + `"SMAN 1 Pabuaran"`
    2. Location pin icon (`R.drawable.location_on`) + `"Indonesia"`
- **Story Cards** (`RoundedCornerShape(18.dp)`, surface `Color(0xFF121316)`, border `1.dp` `Color(0xFF26282E)`, padding `16.dp`, vertical spacing `12.dp`):
  - **Card 1 (`ABOUT LYRA MUSIC`)**:
    - Label: `"ABOUT LYRA MUSIC"` (11.sp, letterSpacing 1.2.sp, `Color(0xFF8E929B)`).
    - Row: `R.drawable.ic_lyra_logo` (`64.dp`, `tint = Color.Unspecified`) on left + Column on right with `"LYRA"` (28.sp, ExtraBold, White) and `"Music for a brighter you"` (13.sp, Medium, `Color(0xFFE2B963)`).
    - Body: `"LYRA bukan sekadar nama sebuah aplikasi musik. Di baliknya, ada cerita sederhana tentang cahaya, kekaguman, dan seseorang yang menginspirasi tanpa pernah harus menjadi pusat perhatian."`
  - **Card 2 (`L — Luminous`)**:
    - Title: `"L — Luminous"` (15.sp, Bold, `Color(0xFFE2B963)`).
    - Body: `"Melambangkan cahaya, keindahan, dan harapan yang membuat sesuatu terasa lebih berarti."`
  - **Card 3 (`Y • RA (D)`)**:
    - Title: `"Y • RA (D)"` (15.sp, Bold, `Color(0xFFE2B963)`).
    - Body: `"Sebuah rangkaian inisial yang menyimpan cerita pribadi. Tidak semua maknanya perlu dijelaskan kepada dunia."`
  - **Card 4 (Closing Reflection)**:
    - Body: `"Bagi saya, LYRA adalah cara untuk menyimpan sebuah cerita dalam sesuatu yang nyata. Seperti musik, ada hal-hal yang tidak selalu bisa dijelaskan dengan kata-kata, tetapi tetap memiliki arti."`
  - **Card 5 (`Hubungi Developer`)**:
    - Title: `"Hubungi Developer"` (15.sp, Bold, White).
    - Inner clickable box 1: GitHub icon (tint `Color(0xFFE2B963)`) + `"github.com/dinzid04/Linefy"` -> opens `"https://github.com/dinzid04/Linefy"`
    - Inner clickable box 2: Email icon (tint `Color(0xFFE2B963)`) + `"gadingkencana05@gmail.com"` -> opens `"mailto:gadingkencana05@gmail.com"`
  - **Version & Support Button**:
    - Include version badge (`Lyra | 8.1.1`) and `"Support development"` button at the bottom that opens `DonateSheet` (QRIS overlay).

## 5. On/Off Toggle for Horizontal Swipe to Change Song in Full Music Player
- **Preference Key (`PreferenceKeys.kt`)**:
  - Add `val SwipeToChangeSongKey = booleanPreferencesKey("swipeToChangeSong")` (default = `false` so accidental swipes do not skip songs unless enabled).
- **Player Implementation (`SongCarousel.kt`, `BottomSheetPlayer.kt`, `PlayerCoverLayout.kt`)**:
  - Read `val (swipeToChangeSong, _) = rememberPreference(SwipeToChangeSongKey, defaultValue = false)`.
  - In `SongCarousel.kt` (where `HorizontalPager` swipes between previous/current/next songs) and any horizontal drag/swipe detector in `BottomSheetPlayer.kt` or `PlayerCoverLayout.kt`:
    - Set `userScrollEnabled = swipeToChangeSong` on the `HorizontalPager` and guard any horizontal swipe-to-skip gesture with `if (swipeToChangeSong)`.
- **Settings UI (`GestureScreen.kt` & `PlaybackSettingsScreen.kt`)**:
  - Add a toggle switch `"Geser untuk Ganti Lagu (Swipe to Change Song)"` with subtitle `"Aktifkan untuk mengganti lagu dengan menggeser ke kanan atau kiri di layar penuh pemutar musik"`.
Catatan tambahan: Gunakan gambar `R.drawable.qris_dinz` dan `R.drawable.lyra_logo_img` yang sudah saya taruh di `app/src/main/res/drawable-nodpi/`, serta lihat file referensi visual di `.agents/skills/lyra-recode/references/` (`ref_developer_about.jpg` dan `ref_liquid_navbar.mp4`) sebagai acuan desainnya.
