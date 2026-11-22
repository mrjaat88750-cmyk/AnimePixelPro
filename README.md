# AnimePixel Pro — Phase 1

Native Android starter built with Kotlin + Jetpack Compose.

## Included
- Dark premium Home UI
- Search and category chips
- Trending wallpaper sample cards
- Wallpaper preview screen
- Favorites state (in-memory for Phase 1)
- Explore and Settings tabs
- GitHub Actions workflow that builds and uploads a debug APK

## Important
Wallpaper art in this starter is generated with Compose gradients and placeholder glyphs. It is not a real 4K wallpaper catalog yet. The Set Wallpaper button is a UI placeholder; Android WallpaperManager / WallpaperService integration is planned for Phase 2.

## Build
Requires JDK 21 and Android SDK platform 37 / build tools supplied by the CI runner. Run `gradle assembleDebug` in a configured Android build environment.
