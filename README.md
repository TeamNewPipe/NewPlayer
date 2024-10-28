
<p align="center">
<img src="misc/logo_shadow.png">
</p>

<h2 align="center"><b>NewPlayer</b></h3>

<h3 align="center">NewPipe's next media player framework</h3>

<br>
<br>
<i>Icon by <a href="https://jaim3.com">Jaime López</a></i>
<br>

### So what is NewPlayer then?
NewPlayer is a media framework, which is independent of NewPipe itself. I decided to make it independent, because one of the big issues we have with the current player is that it is deeply integrated into NewPipe. Therefore, I wanted to make NewPlayer a separate module in order to enforce that the interface between NewPipe and the player is only as big as necessary. This also has the advantage that NewPlayer can be used independently of NewPipe itself, which means it can be used in other apps too.

## Content

1. [Quick overview](#let-me-give-you-a-profile-about-newplayer)
2. [Preview](#how-does-newplayer-look-like)
3. [Documentation](#documentation)
   - [Getting started](#getting-started)
   - [How NewPlayer works](#how-does-newplayer-work)
4. [Code Documentation](https://teamnewpipe.github.io/NewPlayer/)

### Let me give you a Profile about NewPlayer:
- It is a module, separate from NewPipe and can be used as an independent player framework
- It is based on the [Media3](https://developer.android.com/media/media3) library
- Its UI is created with [Jetpack Compose](https://developer.android.com/compose)
- It's fully written in Kotlin
- Its UI resembles the UI of the current NewPipe player, but improves it
- The UI uses [Material You](https://m3.material.io/blog/announcing-material-you) theming
- It follows an [MVVM](https://www.geeksforgeeks.org/mvvm-model-view-viewmodel-architecture-pattern-in-android/) architecture
- It is GPLv3 licensed
- It uses [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) dependency injection

### How does NewPlayer look like?
<table>
<tr>
<td>
<img width="200" src="misc/screenshots/373686583-1164cf7c-66eb-48be-aeda-55e6e6294cf1.png">
<br>
Embedded Screen in test App
</td>
<td><img width="550" src="misc/screenshots/373685724-42609e51-6bf7-4008-b084-a59ce111f3c1.png">
<br>
Fullscreen
</td>
</tr>
</table>
<table>
<tr>
<td>
<img width="350" src="misc/screenshots/373688583-9011749c-3aec-4bf7-a368-40000c84f8e3.png">
<br>
Audio frontend
</td>
<td>
<img width="400" src="misc/screenshots/373689058-9fc27dfd-7f89-48de-b0ff-cd6e9bd4fdbd.png">
<br>
Audio frontend landscape
</td>
</tr>
</table>

<table>
<tr>
<td>
<img width="500" src="misc/screenshots/373690788-a7af6db0-eac2-4913-8f60-3d621c5afd9f.png">
<br>
Playlist screen
</td>
<td>
<img width="500" src="misc/screenshots/373690876-9b96ae22-d537-4b49-ac1c-4549a94bebcb.png">
<br>
Chapter screen
</td>
<td>
<img width="500" src="misc/screenshots/373691456-4aaff87d-dbf8-4877-866b-60e6fc05ea6a.png">
<br>
Picture in Picture
</td>
</tr>
</table>
<table>
<tr>
<td>
<img width="400" src="misc/screenshots/373692488-5e861e22-a969-4eae-aa05-ecd9a339e80d.png">
<br>
Volume indicator
</td>
<td>
<img width = "400" src="misc/screenshots/373695908-341112d4-dac0-488f-961c-9b389396d289.png">
<br>
Main menu
</td>
</tr>
</table>

## Getting started

1. **Add NewPlayer to your project** 

   You can do this by adding the [JitPack](https://jitpack.io/) repository:
   ```
   implementation 'com.github.TeamNewPipe:NewPlayer:master-SNAPSHOT'
   ```

2. **Modify your Activity in the `AndroidManifest.xml`**
   - Add `android:supportsPictureInPicture="true"` to the `<activity>` tag of the activity that will hold the [NewPlayerUI](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.ui/-new-player-u-i.html)
   - Also add `android:configChanges="screenSize|smallestScreenSize|screenLayout|orientation"` to the `<activity>` tag. This will ensure a smooth transition from and to PiP mode. However, be aware that when you do this, a screen rotation or size change does not trigger an activity reconfiguration. You may have to trigger this by yourself. See [this](https://github.com/TeamNewPipe/NewPlayer/blob/72c14d39822b96420f5c71bb559b47f39dc9ed90/test-app/src/main/java/net/newpipe/newplayer/testapp/MainActivity.kt#L173-L198) code from the text app if you want to know how you could achieve this. However, bear in mind that if you use compose you might not need a screen reconfiguration at all. There just use [androidx.adaptive](https://developer.android.com/reference/kotlin/androidx/compose/material3/adaptive/package-summary) framework to handle screen rotation foo.

3. **Install NewPlayer in your Activity's layout**

    NewPlayer can be used in a [compose](https://developer.android.com/compose) as well as the classic views environment.
    - **Use NewPlayer with Compose**

      You can add NewPlayer in a compose environment by using the [`NewPlayerUI`](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.ui/-new-player-u-i.html) composable for now we will add it with a dummy view model (later more about that):
      ```kotlin
      NewPlayerUI(NewPlayerViewModelDummy())
      ```
    - **Use NewPlayer with views**

      For a views environment (and for compatibility with NewPipe before its UI refactoring), NewPlayer provides a [`NewPlayerView`](https://github.com/TeamNewPipe/NewPlayer/blob/dev/new-player/src/main/java/net/newpipe/newplayer/ui/NewPlayerView.kt). This acts as a simple wrapper for the [`NewPlayerUI`](https://github.com/TeamNewPipe/NewPlayer/blob/72c14d39822b96420f5c71bb559b47f39dc9ed90/new-player/src/main/java/net/newpipe/newplayer/ui/NewPlayerUI.kt#L59) composable.
      
      In order to use it simply put it into the layout of the activity/fragment that should host NewPlayer. You can put multiple instances of `NewPlayerView` into your layout [`NewPlayerViewModel`](https://github.com/TeamNewPipe/NewPlayer/blob/dev/new-player/src/main/java/net/newpipe/newplayer/uiModel/NewPlayerViewModel.kt). You should only have one instance of `NewPlayerView` in your layout.

      You can find an exmapple of how to use `NewPlayerView` in the [test-app](https://github.com/TeamNewPipe/NewPlayer/blob/0cec868ec310a79de19af27c0ab47e54e4dda6a3/test-app/src/main/res/layout/activity_main.xml#L28) *(If there are still two versions of `NewPlayerView` in the test-app's layout: Dear NewPlayer devs, please make it one asap.)*

      Remember to also give the `NewPlayerView` an instance of `NewPlayerViewModel` in the `onCreate()` function of your activity/fragment.

4. **Install NewPlayer in your code**

   NewPlayer requires [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection. Therefore, you must create an instance of the NewPlayer object through a Component that must install the NewPlayer instance in the [`Application`](https://developer.android.com/reference/android/app/Application) instance. The NewPlayer instance must live for as long as the app lives. An example of how to do this can be found again in the [`test-app`](https://github.com/TeamNewPipe/NewPlayer/blob/dev/test-app/src/main/java/net/newpipe/newplayer/testapp/NewPlayerComponent.kt#L40-L62).

   In order to use NewPlayer, the NewPlayer object needs an instance of the [`MediaRepository`](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.repository/-media-repository/index.html). The `MediaRepository` is the primary way that NewPlayer can access data and request the information it needs to operate. In other words you provide NewPlayer with the information it needs through the `MediaRepository`. Because of this you will have to implement this yourself and provide it to `NewPlayer`. For the sake of simplicity however for now you can give the NewPlayer object the [`PlaceHolderRepository`](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.repository/-place-holder-repository/index.html). This repository implementation does nothing but at least allows you to continue installing NewPlayer without a functioning Repository yet.

5. **Install NewPlayer in the NewPlayerViewModel**

   Eventually you will have to put install the NewPlayer instance in the `NewPlayerViewModel` instance in the Activity that hosts the viewmodel.
   This way `NewPlayer` can actually talk to your UI. You can do this by simply setting the viewmodel's NewPlayer: `myNewPlayerViewModel.newPlayer = myNewPlayer`.

6. **Give NewPlayer access to your media**

   You can do this by implementing your own [`MediaRepository`](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.repository/-media-repository/index.html). More information can be found about thins in the [`MediaRepository code documentation`](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.repository/-media-repository/index.html). There is also an [example implementation](https://github.com/TeamNewPipe/NewPlayer/blob/dev/test-app/src/main/java/net/newpipe/newplayer/testapp/TestMediaRepository.kt) of it in the test-app.

7. **Do advanced things**
   Like applying caching and prefetching to your media repository using the meta `MediaRepository` implementations, or perform error handling and error recovering. *TOOD: Write the documentation for this*


## How does NewPlayer work

![Immage showing NewPlayer's architecture](/misc/newplayer_architecture.svg)

NewPlayer uses [MVVM](https://www.geeksforgeeks.org/mvvm-model-view-viewmodel-architecture-pattern-in-android/) architecture design pattern.


### [NewPlayerUI](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.ui/-new-player-view/index.html)

This contains the whole UI of NewPlayer. This composable represents all. From the fullscreen video playback mode to the embedded audio playback mode. This composable resizes or changes its content depending on what should be displayed.
By itself it is dump. It will only render out what is stated by [NewPlayerUIState](#newplayeruistate), and forward any input to the view model. The logic behind the UI is defined by [NewPlayerViewModel](#newplayerviewmodel) instead.

### [NewPlayerViewModel](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.uiModel/-new-player-view-model/index.html)

The [NewPlayerViewModel](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.uiModel/-new-player-view-model/index.html) contains the logic for the UI itself. This will produce a [NewPlayerUIState](#newplayeruistate) whenever the [NewPlayerUI](#newplayerui) should change. It will not take care about the playback or data gathering logic. This is the duty of the [NewPlayer](#newplayer) object.


### [NewPlayerUIState](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.uiModel/-new-player-u-i-state/index.html)

This defines a state of the UI. [NewPlayerUI](#newplayerui) basically just renders what this object defines. It is changed and produced by [NewPlayerViewModel](#newplayerviewmodel) whenever the UI should be updated.

### [NewPlayer](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer/-new-player/index.html)

This object contains the business logic of NewPlayer. It contains the actual instance of the [Media3 ExoPlayer](https://developer.android.com/media/media3/exoplayer). You can controll playback and else through this object. However, also the [NewPlayerViewModel](#newplayerviewmodel) as well as NewPlayer's implementation of the [MediaSessionService](https://developer.android.com/media/media3/session/background-playback) will interact with this object.

### [MediaRepository](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.repository/-media-repository/index.html)

The [MediaRepository](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.repository/-media-repository/index.html) is NewPlayer's way to access data. Through that repository NewPlayers is getting the information it needs to display on screen. The repository can deliver data that is either stored on disk or is available online.
NewPlayer itself only supplies the interface of the MediaRepository. It is the duty of the developer using NewPlayer to implement it.
You can find more information about the [MediaRepository at its code documentation](https://teamnewpipe.github.io/NewPlayer/new-player/net.newpipe.newplayer.repository/-media-repository/index.html).


