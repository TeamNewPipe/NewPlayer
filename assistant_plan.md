# Standalone App — Implementation Plan

## Context
Add a `:standalone-app` Gradle module to the NewPlayer repo. It lets users watch local video files (via SAF) or play network streams (URL input), using the NewPlayer framework directly. The app handles its own `MediaRepository`, has a pure-Compose `MainActivity`, and uses an embedded player layout so queue-management controls stay visible alongside the player. A Share target handles adding items from other apps.

`:new-player` is left entirely untouched.

---

## Module setup

**`settings.gradle.kts`** — add `include(":standalone-app")`

**`standalone-app/build.gradle.kts`** — mirror `test-app/build.gradle.kts`:
- `id("com.android.application")`
- Hilt KSP, Compose, ViewBinding=false
- Dependencies: `project(":new-player")`, `hilt-android`, `androidx.activity:activity-compose`, `media3-exoplayer`, `coil-compose`
- applicationId: `net.newpipe.newplayer.standalone`

**`AndroidManifest.xml`**:
- Single activity `MainActivity`
- `android:supportsPictureInPicture="true"`
- `android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation"` (required for PiP, mirrors test-app)
- Permission: `android.permission.INTERNET`
- Share target intent-filters on MainActivity:
  - `action.SEND` + `video/*` (local file share)
  - `action.SEND` + `text/plain` (URL share)

---

## Files to create

### `StandaloneApp.kt`
`@HiltAndroidApp` Application class with `appScope: CoroutineScope`.

### `AppModule.kt`
`@Module @InstallIn(SingletonComponent::class)`:
- `provideNewPlayer()` — creates `NewPlayerImpl`, wraps in `PrefetchingRepository(CachingRepository(StandaloneMediaRepository(app)))`, sets `playerActivityClass = MainActivity::class.java`, subscribes to `errorFlow` for logging.

### `StandaloneMediaRepository.kt`
Implements `MediaRepository`. Item identifier = the URI/URL string itself.

| Method | Implementation |
|---|---|
| `getMetaInfo(item)` | Extract filename from URI path as title via `DocumentFile.fromSingleUri` or `Uri.lastPathSegment`; no artwork |
| `getStreams(item)` | Single `Stream(item, Uri.parse(item), listOf(VideoStreamTrack(0,0,0,"unknown")))`. MIME via `ContentResolver.getType(uri)` for `file://`, null for HTTP (Media3 probes). `isDashOrHls = false`. |
| `getSubtitles()` | `emptyList()` |
| `getPreviewThumbnailsInfo()` | `PreviewThumbnailsInfo(isAvailable=false, distanceInMs=0)` |
| `getChapters()` | `emptyList()` |
| `getTimestampLink()` | `""` |
| `getHttpDataSourceFactory()` | default (inherited / no override needed) |
| `getRepoInfo()` | `RepoMetaInfo(pullsDataFromNetwork=true, pullsAudioTracks=false)` |

For local file URIs the `ContentResolver` is used directly — no File path needed, SAF grants are URI-scoped.

### `MainActivity.kt`
`@AndroidEntryPoint`, extends `ComponentActivity`.

- Inject `NewPlayer` via Hilt (`@Inject`)
- Obtain ViewModel: `val viewModel: NewPlayerViewModelImpl = hiltViewModel()` (inside `setContent`)
- Assign: `viewModel.newPlayer = newPlayer` in `onCreate`
- **PiP callback** (required by NewPlayer, mirrors test-app):
  ```kotlin
  addOnPictureInPictureModeChangedListener { mode ->
      viewModel.onPictureInPictureModeChanged(mode.isInPictureInPictureMode)
  }
  ```
- Handle incoming share intents via `intent` and `onNewIntent`:
  - Extract URI (`ClipData`) or URL (`EXTRA_TEXT`)
  - If `newPlayer.playBackMode.value != PlayMode.IDLE` → `newPlayer.addToPlaylist(item)`
  - Else → `newPlayer.playStream(item, PlayMode.EMBEDDED_VIDEO)`

`setContent { StandaloneAppUI(viewModel, newPlayer) }` — no XML layout, no ViewBinding.

### `ui/StandaloneAppUI.kt`
Top-level Compose entry point. Uses `Scaffold` with a `SnackbarHost`:

```
Observe viewModel.uiState → uiState
Observe newPlayer.errorFlow → show snackbar

When uiState.uiMode == UIModeState.PLACEHOLDER (IDLE):
    StartScreen(onPickFile, onEnterUrl)

Else (playing, any mode):
    Column {
        NewPlayerUI(viewModel)           // embedded; goes fullscreen internally
        AnimatedVisibility(!uiState.uiMode.fullscreen) {
            AddToQueueBar(onPickFile, onEnterUrl)   // shown only in embedded mode
        }
    }
```

`AddToQueueBar` — a Material You `BottomAppBar`-style `Surface(tonalElevation=...)` row with two `OutlinedButton`s: **"+ Add file"** and **"+ Add URL"**. Calls `newPlayer.addToPlaylist(item)`.

### `ui/StartScreen.kt`
Shown when `playMode == IDLE`. Full-screen Compose:
- `Column` centered with app title
- Two `FilledTonalButton`s:
  - **"Open video file"** → launches SAF picker (`ActivityResultContracts.OpenDocument("video/*")`) → on result: `takePersistableUriPermission(READ)`, then `newPlayer.playStream(uri.toString(), PlayMode.EMBEDDED_VIDEO)`
  - **"Play network stream"** → opens `AlertDialog` with a `TextField` for URL → on confirm: `newPlayer.playStream(url, PlayMode.EMBEDDED_VIDEO)`

The SAF launcher and URL dialog state both live in `StartScreen` (or hoisted to `StandaloneAppUI`). The same two actions are reused in `AddToQueueBar` (just calls `addToPlaylist` instead).

---

## Implementation order

1. Module scaffolding (`settings.gradle.kts`, `build.gradle.kts`, `AndroidManifest.xml`)
2. `StandaloneApp.kt` + `AppModule.kt` + `StandaloneMediaRepository.kt`
3. `StartScreen.kt` (SAF picker + URL dialog, no player yet)
4. `StandaloneAppUI.kt` + `MainActivity.kt` (wire player, embedded layout, snackbar)
5. `AddToQueueBar` (reuse picker/dialog logic from StartScreen)
6. Share intent handling in `MainActivity.onNewIntent`

---

## Verification

- Launch app → start screen shown
- Pick a local video file → SAF picker opens → file plays in embedded mode → `AddToQueueBar` visible below player
- Tap fullscreen inside player → fullscreen, `AddToQueueBar` hides
- Exit fullscreen → embedded mode, `AddToQueueBar` returns
- Tap "+ Add file" in `AddToQueueBar` → SAF picker → added to queue
- Tap "+ Add URL" → dialog → valid URL added to queue
- Share a video from Files app to StandalonePlayer → added to queue while playing
- Share a URL from browser → added to queue while playing
- Share when player idle → starts playback
- Bad URL → snackbar shown (from `errorFlow`)
- PiP mode → triggers correctly, orientation handled
- Run `./gradlew :standalone-app:assembleDebug` and `./gradlew :standalone-app:lintDebug`
