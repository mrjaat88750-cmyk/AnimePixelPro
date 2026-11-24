package com.animepixel.pro
import androidx.compose.animation.core.*

import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView

private val Bg = Color(0xFF090711)
private val Surface = Color(0xFF171326)
private val Purple = Color(0xFF9B5CFF)
private val Blue = Color(0xFF35B8FF)
private val Pink = Color(0xFFFF4FA3)
private val TextMain = Color(0xFFF7F2FF)
private val TextMuted = Color(0xFFAAA1BE)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(9, 7, 17)
        window.navigationBarColor = android.graphics.Color.rgb(9, 7, 17)
        setContent { AnimePixelApp() }
    }
}

private data class Wallpaper(
    val id: Int,
    val title: String,
    val category: String,
    val colors: List<Color>,
    val tag: String,
    val videoRes: Int? = null,
    val posterRes: Int? = null
)

private val wallpapers = listOf(
    Wallpaper(1, "Neon Ronin", "Action", listOf(Color(0xFF25104C), Color(0xFF9B36D8), Color(0xFF111827)), "4K"),
    Wallpaper(2, "Moonlit Sakura", "Nature", listOf(Color(0xFF401C5C), Color(0xFFDF76B8), Color(0xFF17152F)), "4K"),
    Wallpaper(3, "Cyber City", "Cyberpunk", listOf(Color(0xFF071D42), Color(0xFF087EAB), Color(0xFF231044)), "HD"),
    Wallpaper(4, "Spirit Flame", "Fantasy", listOf(Color(0xFF4A142D), Color(0xFFFF743B), Color(0xFF20112F)), "4K"),
    Wallpaper(5, "Silent Horizon", "Nature", listOf(Color(0xFF153A55), Color(0xFF6C85C8), Color(0xFF17152F)), "HD"),
    Wallpaper(6, "Shadow Blade", "Action", listOf(Color(0xFF101827), Color(0xFF5B3CA6), Color(0xFF090711)), "4K"),
    Wallpaper(7, "Dream Galaxy", "Fantasy", listOf(Color(0xFF22104A), Color(0xFF4C6ACB), Color(0xFFB45CBA)), "4K"),
    Wallpaper(8, "Tokyo Pulse", "Cyberpunk", listOf(Color(0xFF35103D), Color(0xFFE43D83), Color(0xFF14213D)), "HD"),
    Wallpaper(9, "Wolf • Crimson Storm", "Action", listOf(Color(0xFF071B35), Color(0xFF1E5C9A), Color(0xFF6B142D)), "LIVE", R.raw.wolf_blue_red, R.drawable.wolf_poster_1),
    Wallpaper(10, "Wolf • Neon Halo", "Fantasy", listOf(Color(0xFF17133D), Color(0xFF3559A8), Color(0xFF9B3B7D)), "LIVE", R.raw.wolf_neon_halo, R.drawable.wolf_poster_2),
    Wallpaper(11, "Wolf • Ember Spirit", "Fantasy", listOf(Color(0xFF4A1D12), Color(0xFFFF7A27), Color(0xFF25245E)), "LIVE", R.raw.wolf_ember_blue, R.drawable.wolf_poster_3),
    Wallpaper(12, "Wolf • Electric Close-up", "Action", listOf(Color(0xFF071B35), Color(0xFF1E5C9A), Color(0xFF6B142D)), "LIVE", R.raw.wolf_blue_red_closeup, R.drawable.wolf_poster_4)
)

@Composable
private fun AnimePixelApp() {
    var tab by remember { mutableStateOf("Home") }
    var selected by remember { mutableStateOf<Wallpaper?>(null) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("All") }
    var favorites by remember { mutableStateOf(setOf<Int>()) }
    var autoChange by remember { mutableStateOf(false) }
    var interval by remember { mutableStateOf("Every day") }
    var selectedEffect by remember { mutableStateOf("Petals") }
    val filtered = wallpapers.filter { w ->
        (tab != "Favorites" || w.id in favorites) &&
            (category == "All" || w.category == category) &&
            (query.isBlank() || w.title.contains(query, true) || w.category.contains(query, true))
    }
    MaterialTheme(colorScheme = darkColorScheme(background = Bg, surface = Surface, primary = Purple)) {
        Scaffold(containerColor = Bg, bottomBar = {
            NavigationBar(containerColor = Color(0xFF100D1B), contentColor = TextMuted) {
                listOf("Home", "Explore", "Favorites", "Studio", "Settings").forEach { name ->
                    val icon = when (name) {
                        "Home" -> Icons.Default.Home
                        "Explore" -> Icons.Default.Explore
                        "Favorites" -> Icons.Default.Favorite
                        "Studio" -> Icons.Default.AutoAwesome
                        else -> Icons.Default.Settings
                    }
                    NavigationBarItem(selected = tab == name, onClick = { tab = name }, icon = { Icon(icon, null) }, label = { Text(name, fontSize = 10.sp) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = Purple, selectedTextColor = TextMain, indicatorColor = Purple.copy(alpha = .16f), unselectedIconColor = TextMuted, unselectedTextColor = TextMuted))
                }
            }
        }) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                when (tab) {
                    "Studio" -> MotionStudio(selectedEffect, { selectedEffect = it })
                    "Settings" -> SettingsScreen(autoChange, { autoChange = it }, interval, { interval = it })
                    else -> {
                        Header(tab)
                        if (tab != "Favorites") SearchBar(query) { query = it }
                        if (tab == "Home") FeaturedBanner { tab = "Explore" }
                        CategoryRow(category) { category = it }
                        Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(if (tab == "Favorites") "YOUR COLLECTION" else "${if (tab == "Home") "TRENDING WALLPAPERS" else "EXPLORE WALLPAPERS"}", color = TextMain, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, fontSize = 13.sp)
                            Text("${filtered.size} wallpapers", color = TextMuted, fontSize = 11.sp)
                        }
                        LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                            items(filtered, key = { it.id }) { wallpaper ->
                                WallpaperCard(wallpaper, wallpaper.id in favorites, { favorites = if (wallpaper.id in favorites) favorites - wallpaper.id else favorites + wallpaper.id }) { selected = wallpaper }
                            }
                        }
                    }
                }
            }
            selected?.let { w ->
                WallpaperDetail(w, w.id in favorites, { favorites = if (w.id in favorites) favorites - w.id else favorites + w.id }, { selected = null })
            }
        }
    }
}

@Composable private fun Header(tab: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(Purple, Blue))), contentAlignment = Alignment.Center) { Icon(Icons.Default.Wallpaper, null, tint = Color.White) }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("ANIMEPIXEL", color = TextMain, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
            Text("PRO  •  4K WALLPAPER STUDIO", color = TextMuted, fontSize = 9.sp, letterSpacing = 1.sp)
        }
        IconButton(onClick = {}) { Icon(Icons.Default.NotificationsNone, null, tint = TextMain) }
        IconButton(onClick = {}) { Icon(Icons.Default.Diamond, null, tint = Color(0xFFFFC857)) }
    }
}

@Composable private fun SearchBar(value: String, onValue: (String) -> Unit) {
    OutlinedTextField(value, onValue, Modifier.fillMaxWidth().padding(horizontal = 16.dp), placeholder = { Text("Search anime, character, style...", color = TextMuted, fontSize = 13.sp) }, leadingIcon = { Icon(Icons.Default.Search, null, tint = Purple) }, singleLine = true, shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Purple, unfocusedBorderColor = Color(0xFF302744), focusedTextColor = TextMain, unfocusedTextColor = TextMain, cursorColor = Purple))
}

@Composable private fun FeaturedBanner(onExplore: () -> Unit) {
    val infinite = rememberInfiniteTransition(label = "banner")
    val glow by infinite.animateFloat(0.65f, 1f, infiniteRepeatable(tween(1800), RepeatMode.Reverse), label = "glow")
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp).height(154.dp).clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(Color(0xFF35105D), Color(0xFF152D58), Color(0xFF120D21)))).border(1.dp, Purple.copy(alpha = glow), RoundedCornerShape(22.dp))) {
        Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Purple.copy(alpha = .35f), Color.Transparent), radius = 420f)))
        Column(Modifier.align(Alignment.CenterStart).padding(18.dp)) {
            Text("THE ANIME UNIVERSE", color = Color(0xFFD6B8FF), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(6.dp))
            Text("Your screen.\nYour universe.", color = TextMain, fontSize = 23.sp, fontWeight = FontWeight.Black, lineHeight = 27.sp)
            Spacer(Modifier.height(10.dp))
            Button(onClick = onExplore, colors = ButtonDefaults.buttonColors(containerColor = Purple), contentPadding = PaddingValues(horizontal = 15.dp, vertical = 6.dp)) { Text("Explore 4K  →", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
        }
        Icon(Icons.Default.AutoAwesome, null, Modifier.align(Alignment.TopEnd).padding(22.dp).size(42.dp), tint = Color.White.copy(alpha = .7f))
    }
}

@Composable private fun CategoryRow(selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("All", "Action", "Fantasy", "Nature", "Cyberpunk").forEach { c ->
            FilterChip(selected = selected == c, onClick = { onSelect(c) }, label = { Text(c, fontSize = 11.sp) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Purple.copy(alpha = .25f), selectedLabelColor = TextMain, labelColor = TextMuted), border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selected == c, borderColor = Color(0xFF382C50), selectedBorderColor = Purple))
        }
    }
}

@Composable private fun WallpaperCard(w: Wallpaper, favorite: Boolean, onFavorite: () -> Unit, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface).clickable(onClick = onClick).border(1.dp, Color(0xFF2B223C), RoundedCornerShape(18.dp))) {
        Box(Modifier.fillMaxWidth().height(190.dp).background(Brush.linearGradient(w.colors))) {
            if (w.posterRes != null) {
                Image(painterResource(w.posterRes), contentDescription = w.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            } else {
                WallpaperArt(w)
            }
            Text(w.tag, Modifier.align(Alignment.TopStart).padding(9.dp).clip(RoundedCornerShape(7.dp)).background(if (w.videoRes != null) Purple.copy(alpha = .9f) else Color.Black.copy(alpha = .45f)).padding(horizontal = 8.dp, vertical = 4.dp), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            if (w.videoRes != null) Icon(Icons.Default.PlayCircleFilled, null, Modifier.align(Alignment.Center).size(42.dp), tint = Color.White.copy(alpha = .92f))
            IconButton(onClick = onFavorite, modifier = Modifier.align(Alignment.TopEnd).padding(5.dp).size(34.dp).background(Color.Black.copy(alpha = .4f), CircleShape)) { Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (favorite) Pink else Color.White, modifier = Modifier.size(18.dp)) }
            Icon(Icons.Default.OpenInFull, null, Modifier.align(Alignment.BottomEnd).padding(10.dp).size(17.dp), tint = Color.White.copy(alpha = .8f))
        }
        Column(Modifier.padding(horizontal = 11.dp, vertical = 9.dp)) {
            Text(w.title, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(if (w.videoRes != null) "ANIMATED VIDEO • TAP TO PLAY" else w.category.uppercase(), color = TextMuted, fontSize = 9.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable private fun WallpaperArt(w: Wallpaper) {
    Canvas(Modifier.fillMaxSize()) {
        val w0 = size.width; val h0 = size.height
        for (i in 0..24) {
            val x = (i * 73f % w0); val y = (i * 47f % h0)
            drawCircle(Color.White.copy(alpha = if (i % 3 == 0) .65f else .22f), radius = if (i % 4 == 0) 2.3f else 1.2f, center = androidx.compose.ui.geometry.Offset(x, y))
        }
        drawCircle(w.colors[1].copy(alpha = .38f), radius = size.minDimension * .27f, center = androidx.compose.ui.geometry.Offset(w0 * .72f, h0 * .42f))
        val path = Path().apply { moveTo(0f, h0 * .78f); lineTo(w0 * .28f, h0 * .48f); lineTo(w0 * .52f, h0 * .78f); lineTo(w0 * .78f, h0 * .4f); lineTo(w0, h0 * .74f); lineTo(w0, h0); lineTo(0f, h0); close() }
        drawPath(path, Brush.verticalGradient(listOf(w.colors[1].copy(alpha = .85f), Color.Black.copy(alpha = .8f))))
    }
}

@Composable private fun VideoWallpaperPreview(videoRes: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val videoView = remember(videoRes) {
        VideoView(context).apply {
            setVideoURI(Uri.parse("android.resource://${context.packageName}/$videoRes"))
            setOnPreparedListener { player ->
                player.isLooping = true
                player.setVolume(0f, 0f)
                start()
            }
        }
    }
    DisposableEffect(videoView) {
        onDispose { videoView.stopPlayback() }
    }
    AndroidView(factory = { videoView }, modifier = modifier)
}

@Composable private fun WallpaperDetail(w: Wallpaper, favorite: Boolean, onFavorite: () -> Unit, onClose: () -> Unit) {
    AlertDialog(onDismissRequest = onClose, containerColor = Surface, title = { Text(w.title, color = TextMain, fontWeight = FontWeight.Bold) }, text = {
        Column {
            if (w.videoRes != null) {
                VideoWallpaperPreview(w.videoRes, Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(18.dp)).background(Color.Black))
            } else {
                Box(Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(w.colors))) { WallpaperArt(w) }
            }
            Spacer(Modifier.height(12.dp))
            Text(if (w.videoRes != null) "${w.category}  •  LOOPING VIDEO PREVIEW" else "${w.category}  •  ${w.tag}", color = TextMuted)
            Text(if (w.videoRes != null) "Your uploaded wolf animation is playing in a silent loop. Tap Close to stop playback." else "Preview artwork is a design placeholder. Connect a licensed wallpaper source to serve real downloadable 4K images.", color = TextMuted, fontSize = 12.sp)
        }
    }, confirmButton = { TextButton(onClick = onFavorite) { Text(if (favorite) "Remove favorite" else "Add favorite", color = Purple) } }, dismissButton = { TextButton(onClick = onClose) { Text("Close", color = TextMuted) } })
}

@Composable private fun MotionStudio(effect: String, onEffect: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text("MOTION STUDIO", color = TextMain, fontWeight = FontWeight.Black, fontSize = 21.sp, letterSpacing = 1.sp)
        Text("Create animated looks for your wallpaper", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(18.dp))
        val infinite = rememberInfiniteTransition(label = "motion")
        val alpha by infinite.animateFloat(.3f, .95f, infiniteRepeatable(tween(1300), RepeatMode.Reverse), label = "pulse")
        Box(Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFF271044), Color(0xFF123A65), Color(0xFF120D21)))), contentAlignment = Alignment.Center) {
            repeat(18) { i -> Box(Modifier.offset(x = ((i * 31) % 260 - 130).dp, y = ((i * 47) % 220 - 110).dp).size((3 + i % 4).dp).background(Purple.copy(alpha = alpha), CircleShape)) }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.AutoAwesome, null, tint = Color.White, modifier = Modifier.size(54.dp)); Text(effect.uppercase(), color = TextMain, fontWeight = FontWeight.Black, fontSize = 17.sp); Text("Animated effect preview", color = TextMuted, fontSize = 11.sp) }
        }
        Spacer(Modifier.height(18.dp)); Text("EFFECT LIBRARY", color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        listOf("Petals", "Rain", "Snow", "Neon", "Energy", "Glitch", "3D Depth").chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                row.forEach { item -> OutlinedButton(onClick = { onEffect(item) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = if (effect == item) Color.White else TextMuted), border = BorderStroke(1.dp, if (effect == item) Purple else Color(0xFF382C50))) { Text(item, fontSize = 11.sp) } }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(18.dp)); Text("Note: this screen previews UI effects only. A real animated live-wallpaper service and export pipeline are not connected in this starter.", color = TextMuted, fontSize = 11.sp)
    }
}

@Composable private fun SettingsScreen(auto: Boolean, onAuto: (Boolean) -> Unit, interval: String, onInterval: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        Text("SETTINGS", color = TextMain, fontWeight = FontWeight.Black, fontSize = 22.sp)
        Text("Personalize your wallpaper experience", color = TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(20.dp))
        SettingRow("Auto Wallpaper Changer", "Rotate wallpapers automatically", auto, onAuto)
        Spacer(Modifier.height(12.dp))
        Text("CHANGE INTERVAL", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
            listOf("Every hour", "Every day", "Every week").forEach { v -> FilterChip(selected = interval == v, onClick = { onInterval(v) }, label = { Text(v, fontSize = 10.sp) }) }
        }
        Spacer(Modifier.height(22.dp)); SettingRow("Dark Theme", "Premium dark appearance", true, {})
        Spacer(Modifier.height(12.dp)); SettingRow("Notifications", "New wallpaper alerts", false, {})
        Spacer(Modifier.height(24.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF211632)), shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp)) { Text("GO PREMIUM", color = Color(0xFFFFD27A), fontWeight = FontWeight.Black); Text("Unlock exclusive collections and an ad-free experience.", color = TextMuted, fontSize = 12.sp); Button(onClick = {}, modifier = Modifier.padding(top = 10.dp), colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("Explore Premium") } }
        }
        Spacer(Modifier.height(20.dp)); Text("AnimePixel Pro • Wolf Video Update • v1.2.0", color = TextMuted, fontSize = 10.sp)
    }
}

@Composable private fun SettingRow(title: String, subtitle: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Surface).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, color = TextMain, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text(subtitle, color = TextMuted, fontSize = 10.sp) }
        Switch(checked, onCheckedChange = onChecked, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Purple))
    }
}
