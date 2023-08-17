package com.example.library

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

internal object NotificationGenerator {
    private const val NOTIFICATION_ID = 9999
    private const val ANDROID_CHANNEL_ID = "com.example.foregroundaudiorecorder"

    internal fun generateNotification(context: Context, layout: Int): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ANDROID_CHANNEL_ID,
                "AudioService",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notificationLayout = RemoteViews(context.packageName, layout).apply {
            setTextViewText(R.id.timeTextView, AudioTimer.getTimeStamp())
            setOnClickPendingIntent(
                R.id.stopButton,
                PendingIntent.getBroadcast(
                    context,
                    1000,
                    Intent(context, AudioReceiver::class.java)
                        .apply {
                            action = AudioReceiver.ACTION_STOP
                        },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            setOnClickPendingIntent(
                R.id.recordButton,
                PendingIntent.getBroadcast(
                    context,
                    1000,
                    Intent(context, AudioReceiver::class.java)
                        .apply {
                            action = AudioReceiver.ACTION_RECORD
                        },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            setOnClickPendingIntent(
                R.id.cancelButton,
                PendingIntent.getBroadcast(
                    context,
                    1000,
                    Intent(context, AudioReceiver::class.java)
                        .apply {
                            action = AudioReceiver.ACTION_CANCEL
                        },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        return NotificationCompat.Builder(context, ANDROID_CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_mic_24)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .build()
    }

     internal fun notifyNotification(context: Context, layout: Int) {
         val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
         val notification = generateNotification(context, layout)
         manager.notify(NOTIFICATION_ID, notification)
     }
}