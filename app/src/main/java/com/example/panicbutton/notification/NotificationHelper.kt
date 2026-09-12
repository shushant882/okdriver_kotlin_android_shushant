package com.example.panicbutton.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "panic_help_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_YES = "com.example.panicbutton.ACTION_YES"
        const val ACTION_NO = "com.example.panicbutton.ACTION_NO"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Help Requests",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for incoming help requests"
            enableVibration(true)
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun showHelpRequestNotification(helperName: String, requesterName: String) {
        val yesIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_YES
        }
        val noIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = ACTION_NO
        }

        val yesPendingIntent = PendingIntent.getBroadcast(
            context, 0, yesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val noPendingIntent = PendingIntent.getBroadcast(
            context, 1, noIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🆘 Help Request")
            .setContentText("$helperName — Are you available to help $requesterName?")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$helperName — Are you available to help $requesterName?\n\nTap Yes to accept or No to decline.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_save, "✅ Yes", yesPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "❌ No", noPendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
        }
    }

    fun cancelNotification() {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
    }
}
