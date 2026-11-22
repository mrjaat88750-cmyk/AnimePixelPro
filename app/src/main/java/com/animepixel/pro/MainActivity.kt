package com.animepixel.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF090A12)
private val Panel = Color(0xFF171927)
private val Purple = Color(0xFF9B72FF)
private val Muted = Color(0xFF9DA2B7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AnimePixelApp() }
    }
}

private data class Wallpaper(val title: String, val tag: String, val colors: List<Color>)
private val wallpapers = listOf(
    Wallpaper("Shadow Hunter", "TRENDING", listOf(Color(0xFF101D49), Color(0xFF6D42C8))),
    Wallpaper("Neon Samurai", "4K AMOLED", listOf(Color(0xFF3A123F), Color(0xFFE34C83))),
    Wallpaper("Moonlight Anime", "NEW", listOf(Color(0xFF092F43), Color(0xFF4B9BB8))),
    Wallpaper("Crimson Warrior", "POPULAR", listOf(Color(0xFF4A171D), Color(0xFFDA694A)))
)
private val categories = listOf("All", "Trending", "AMOLED", "Live", "Gaming", "Romance", "Dark")

@Composable
private fun AnimePixelApp() {
    var tab by remember { mutableStateOf("Home") }
    var category by remember { mutableStateOf("All") }
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<Wallpaper?>(null) }
    val favorites = remember { mutableStateListOf<String>() }

    MaterialTheme(colorScheme = darkColorScheme(primary = Purple, background = Bg, surface = Panel)) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            if (selected != null) {
                WallpaperDetail(selected!!, favorites.contains(selected!!.title),
                    onBack = { selected = null },
                    onFavorite = {
                        val name = selected!!.title
                        if (favorites.contains(name)) favorites.remove(name) else favorites.add(name)
                    })
            } else {
                Scaffold(containerColor = Bg, bottomBar = {
                    NavigationBar(containerColor = Color(0xFF111320), contentColor = Color.White) {
                        val items = listOf("Home" to Icons.Default.Home, "Explore" to Icons.Default.Explore,
                            "Favorites" to Icons.Default.Favorite, "Settings" to Icons.Default.Settings)
                        items.forEach { (name, icon) ->
                            NavigationBarItem(selected = tab == name, onClick = { tab = name },
                                icon = { Icon(icon, contentDescription = name) },
                                label = { Text(name, fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Purple,
                                    selectedTextColor = Purple, indicatorColor = Color(0xFF30264C),
                                    unselectedIconColor = Muted, unselectedTextColor = Muted))
                        }
                    }
                }) { padding ->
                    when (tab) {
                        "Home" -> HomeScreen(padding, category, { category = it }, query, { query = it }, { selected = it })
                        "Explore" -> ExploreScreen(padding, query, { query = it }, { selected = it })
                        "Favorites" -> FavoritesScreen(padding, favorites, { title -> wallpapers.find { it.title == title }?.let { selected = it } })
                        else -> SettingsScreen(padding)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreen(padding: PaddingValues, category: String, onCategory: (String) -> Unit,
                       query: String, onQuery: (String) -> Unit, onSelect: (Wallpaper) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 18.dp)) {
        item { Header() }
        item { SearchBox(query, onQuery) }
        item { HeroBanner() }
        item { SectionTitle("Categories", "See all") }
        item {
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                categories.forEach { item ->
                    FilterChip(selected = category == item, onClick = { onCategory(item) }, label = { Text(item) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF38285D),
                            selectedLabelColor = Color.White, labelColor = Muted))
                }
            }
        }
        item { SectionTitle("Trending Wallpapers", "View all") }
        items(wallpapers.filter { query.isBlank() || it.title.contains(query, true) }) { wallpaper ->
            WallpaperRow(wallpaper, onSelect)
        }
        item { SectionTitle("Made for your screen", "Explore") }
        item { MotionStudioCard() }
    }
}

@Composable
private fun Header() {
    Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(Purple, Color(0xFF3D7BFF)))), contentAlignment = Alignment.Center) {
            Text("A", color = Color.White, fontWeight = FontWeight.Black, fontSize = 25.sp)
        }
        Spacer(Modifier.width(11.dp))
        Column(Modifier.weight(1f)) {
            Text("AnimePixel Pro", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Your screen. Your anime universe.", color = Muted, fontSize = 11.sp)
        }
        IconButton(onClick = {}) { Icon(Icons.Default.NotificationsNone, null, tint = Color.White) }
    }
}

@Composable
private fun SearchBox(query: String, onQuery: (String) -> Unit) {
    OutlinedTextField(value = query, onValueChange = onQuery, modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        placeholder = { Text("Search anime, character, mood...", color = Muted, fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = Muted) }, singleLine = true,
        shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Purple, unfocusedBorderColor = Color(0xFF292D40),
            focusedContainerColor = Panel, unfocusedContainerColor = Panel, cursorColor = Purple,
            focusedTextColor = Color.White, unfocusedTextColor = Color.White))
}

@Composable
private fun HeroBanner() {
    Box(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 18.dp).height(172.dp)
        .clip(RoundedCornerShape(24.dp)).background(Brush.linearGradient(listOf(Color(0xFF21174B), Color(0xFF6B3D9C), Color(0xFF172A50))))) {
        Column(Modifier.align(Alignment.CenterStart).padding(20.dp)) {
            Text("ANIME COLLECTION 2026", color = Color(0xFFD8C8FF), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Spacer(Modifier.height(8.dp))
            Text("Make your screen\nlegendary.", color = Color.White, fontSize = 25.sp, lineHeight = 29.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(10.dp))
            Text("Discover stunning 4K worlds  →", color = Color.White.copy(alpha = .85f), fontSize = 11.sp)
        }
        Text("✦", Modifier.align(Alignment.TopEnd).padding(22.dp), color = Color(0xFFFFD98B), fontSize = 38.sp)
        Text("✧", Modifier.align(Alignment.BottomEnd).padding(35.dp), color = Color.White, fontSize = 48.sp)
    }
}

@Composable
private fun SectionTitle(title: String, action: String) {
    Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, Modifier.weight(1f), color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        Text(action, color = Purple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun WallpaperRow(wallpaper: Wallpaper, onSelect: (Wallpaper) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 6.dp).clip(RoundedCornerShape(18.dp))
        .background(Panel).clickable { onSelect(wallpaper) }.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        WallpaperArt(wallpaper, Modifier.width(94.dp).height(112.dp))
        Column(Modifier.weight(1f).padding(start = 13.dp)) {
            Text(wallpaper.tag, color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(Modifier.height(5.dp))
            Text(wallpaper.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text("4K Ultra HD  •  AMOLED", color = Muted, fontSize = 10.sp)
            Spacer(Modifier.height(9.dp))
            Text("Preview wallpaper  →", color = Color(0xFFD3C5FF), fontSize = 11.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Muted)
    }
}

@Composable
private fun WallpaperArt(w: Wallpaper, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(15.dp)).background(Brush.linearGradient(w.colors)), contentAlignment = Alignment.Center) {
        Text("✦", Modifier.align(Alignment.TopEnd).padding(9.dp), color = Color.White.copy(alpha = .7f), fontSize = 20.sp)
        Text("影", color = Color.White.copy(alpha = .9f), fontSize = 44.sp, fontWeight = FontWeight.Black)
        Box(Modifier.fillMaxWidth().height(28.dp).align(Alignment.BottomCenter).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = .5f)))))
    }
}

@Composable
private fun MotionStudioCard() {
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp).clip(RoundedCornerShape(20.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF31204D), Color(0xFF172B43)))).padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.AutoAwesome, null, tint = Color(0xFFFFD98B), modifier = Modifier.size(34.dp))
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text("Motion Studio", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Bring wallpapers to life with effects", color = Color.White.copy(alpha = .7f), fontSize = 10.sp)
        }
        Text("SOON", color = Color(0xFFFFD98B), fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ExploreScreen(padding: PaddingValues, query: String, onQuery: (String) -> Unit, onSelect: (Wallpaper) -> Unit) {
    Column(Modifier.fillMaxSize().padding(padding)) {
        Text("Explore", Modifier.padding(20.dp), color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        SearchBox(query, onQuery)
        LazyColumn(contentPadding = PaddingValues(bottom = 20.dp)) {
            items(wallpapers.filter { query.isBlank() || it.title.contains(query, true) }) { WallpaperRow(it, onSelect) }
        }
    }
}

@Composable
private fun FavoritesScreen(padding: PaddingValues, favorites: List<String>, onSelect: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(padding)) {
        Text("Your Favorites", Modifier.padding(20.dp), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        if (favorites.isEmpty()) Text("Your saved wallpapers will appear here.", Modifier.padding(20.dp), color = Muted)
        else LazyColumn { items(favorites) { name -> wallpapers.find { it.title == name }?.let { WallpaperRow(it) { onSelect(name) } } } }
    }
}

@Composable
private fun SettingsScreen(padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(20.dp)) {
        Text("Settings", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        listOf("Wallpaper quality" to "Ultra HD", "Auto wallpaper changer" to "Coming soon", "Notifications" to "Enabled", "Language" to "English", "About AnimePixel Pro" to "Version 1.0.0").forEach { (a, b) ->
            Row(Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(a, Modifier.weight(1f), color = Color.White, fontSize = 14.sp)
                Text(b, color = Muted, fontSize = 12.sp)
            }
            HorizontalDivider(color = Color(0xFF25283A))
        }
    }
}

@Composable
private fun WallpaperDetail(w: Wallpaper, favorite: Boolean, onBack: () -> Unit, onFavorite: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg)) {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
            Text("Wallpaper Preview", Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold)
            IconButton(onClick = onFavorite) { Icon(if (favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (favorite) Color(0xFFFF5D8F) else Color.White) }
        }
        Box(Modifier.weight(1f).fillMaxWidth().padding(horizontal = 28.dp, vertical = 12.dp)) {
            WallpaperArt(w, Modifier.fillMaxSize())
        }
        Text(w.title, Modifier.align(Alignment.CenterHorizontally), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("4K Ultra HD  •  AMOLED Optimized", Modifier.align(Alignment.CenterHorizontally).padding(top = 5.dp), color = Muted, fontSize = 11.sp)
        Button(onClick = { }, Modifier.fillMaxWidth().padding(20.dp).height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Purple)) {
            Icon(Icons.Default.Wallpaper, null)
            Spacer(Modifier.width(8.dp))
            Text("Set Wallpaper")
        }
    }
}
