package net.newpipe.newplayer.service

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import net.newpipe.newplayer.R

const val NEW_PLAYER_MEDIA_NOTIFICATION_ID = 17480
const val NEW_PLAYER_MEDIA_NOTIFICATION_CHANNEL_NAME = "Player"

const val NEW_PLAYER_REQUEST_CODE_OPEN_ACTIVITY = 0

@OptIn(UnstableApi::class)
internal fun createNewPlayerNotification(
    service: NewPlayerService,
    session: MediaSession,
    notificationManager: NotificationManager,
    notificationIcon: IconCompat,
    playerActivity: Class<Activity>
): Notification {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NEW_PLAYER_MEDIA_NOTIFICATION_CHANNEL_NAME,
                NEW_PLAYER_MEDIA_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }


    val onNotificationClickIntent = Intent(service, playerActivity).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
    val onNotificationClickPendingIntent =
        PendingIntent.getActivity(
            service,
            NEW_PLAYER_REQUEST_CODE_OPEN_ACTIVITY,
            onNotificationClickIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

    val notificationBuilder =
        NotificationCompat.Builder(service, NEW_PLAYER_MEDIA_NOTIFICATION_CHANNEL_NAME)
            .setContentTitle(service.resources.getString(R.string.new_player_name))
            .setContentText(service.resources.getString(R.string.playing_in_background))
            .setStyle(MediaStyleNotificationHelper.MediaStyle(session))
            .setContentIntent(onNotificationClickPendingIntent)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        notificationBuilder.setSmallIcon(notificationIcon)
    } else {
        notificationBuilder
            .setSmallIcon(R.drawable.new_player_tiny_icon)
    }

    return notificationBuilder.build()
}