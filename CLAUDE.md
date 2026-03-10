# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**NewPlayer** is a standalone, modular media player framework for Android, built on Media3 (ExoPlayer). It provides a reusable library that can be embedded in other Android apps, with a Jetpack Compose UI using Material You theming. Written in Kotlin, it follows MVVM architecture with Hilt for DI.

## Build Commands

```bash
./gradlew assembleDebug              # Build debug APK
./gradlew lintDebug                  # Run lint checks
./gradlew testDebugUnitTest          # Run JVM unit tests
./gradlew connectedCheck             # Run instrumented tests (requires device/emulator)
./gradlew dokkaHtml                  # Generate API documentation
```

## Architecture

Two Gradle modules:
- **`:new-player`** — The core library (publishable via JitPack)
- **`:test-app`** — Demo/test application showing usage

### MVVM Layers

```
NewPlayerUI (Compose, dumb/stateless)
    ↕
NewPlayerViewModel / NewPlayerViewModelImpl  (UI state & user input)
    ↕
NewPlayer / NewPlayerImpl  (business logic, ExoPlayer, MediaSession)
    ↕
MediaRepository (interface implemented by the consuming app)
```

**Key classes:**
- `NewPlayer` interface — the main public API (`new-player/src/main/java/net/newpipe/newplayer/NewPlayer.kt`)
- `NewPlayerImpl` — implementation with ExoPlayer (`NewPlayerImpl.kt`)
- `NewPlayerViewModelImpl` — drives `NewPlayerUIState` exposed as StateFlow
- `MediaRepository` — data access interface (streams, chapters, subtitles, thumbnails)

### Package Structure (`:new-player`)

| Package | Contents |
|---|---|
| `logic/` | Playback logic, stream/track selection, `MediaSourceBuilder`, `AutoStreamSelector` |
| `ui/` | Compose UI components split into `videoplayer/`, `audioplayer/`, `common/`, `seeker/`, `theme/` |
| `uiModel/` | ViewModel, `NewPlayerUIState`, Hilt module |
| `repository/` | `MediaRepository` interface + `CachingRepository`, `PrefetchingRepository`, `MultiRepository` |
| `service/` | `NewPlayerService` (background playback), `MediaNotification` |
| `data/` | Data models: `Chapter`, `Stream`, `StreamTrack`, `Subtitle`, `PlayMode`, `RepeatMode` |

## Gradle
Run gradle --offline you don't have network access.

## Offlimits
Consider the new-player repo read only. Don't touch it.

## Tech Stack

| Concern | Library/Version |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Media | Media3 (androidx.media3) 1.8.0 |
| DI | Hilt 2.57.1 |
| Kotlin | 2.2.10 |
| Min SDK | 21 (Android 5.0) |
| Compile SDK | 36 |

## Testing

- **Unit tests** (JVM): `new-player/src/test/` — JUnit 4 + MockK + Coroutines Test, focused on repository layer
- **Instrumented tests**: `test-app/src/androidTest/` — Compose UI Test + Espresso + Hilt, uses custom `HiltTestRunner`
