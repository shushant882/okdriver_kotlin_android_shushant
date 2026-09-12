package com.example.panicbutton.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationActionReceiver : BroadcastReceiver() {

    companion object {
        var onResponse: ((Boolean) -> Unit)? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        NotificationManagerCompat.from(context).cancel(NotificationHelper.NOTIFICATION_ID)

        when (intent.action) {
            NotificationHelper.ACTION_YES -> {
                onResponse?.invoke(true)
            }
            NotificationHelper.ACTION_NO -> {
                onResponse?.invoke(false)
            }
        }
    }
}
