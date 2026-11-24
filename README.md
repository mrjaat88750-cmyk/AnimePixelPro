# AnimePixel Pro – Starter UI Package

This ZIP is a fresh starter scaffold for the AnimePixel Pro Android app (Kotlin + Jetpack Compose). It includes a premium dark/neon UI, navigation, wallpaper card mockups, category/search filtering, in-memory favorites, Motion Studio effect preview, Auto Wallpaper Changer settings UI, and GitHub Actions APK build workflow.

## Build

GitHub Actions runs `gradle --no-daemon assembleDebug` and uploads `app-debug.apk` as an artifact.

## Important implementation status

This is **not yet a production-complete wallpaper app**. The wallpaper tiles are generated gradient placeholders, not licensed real 4K anime images. The following integrations still need implementation and testing:

- Licensed 4K wallpaper API / storage / CDN and real image caching.
- Persistent favorites and download manager.
- Actual WallpaperManager apply flow and lock-screen support.
- Android live-wallpaper service and real motion renderer (rain, snow, petals, neon, energy, glitch, parallax).
- Scheduled auto-change using WorkManager and Android background restrictions.
- AdMob / billing / premium entitlement integration and privacy disclosures.
- Release signing, policy review, device testing and Play Store assets.

Do not treat placeholder buttons or previews as completed production features. Add only wallpaper content that you have permission to distribute.

## AGP 9 Kotlin fix

The project intentionally omits `org.jetbrains.kotlin.android`, because AGP 9 provides built-in Kotlin support. The Compose compiler plugin remains applied.
