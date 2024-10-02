package com.example.company_app

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(context: Context) {
        // Skapa en notifikationskanal (för Android Oreo och senare)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "YOUR_CHANNEL_ID"
            val channelName = "Your Channel Name"
            val channelDescription = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Bygg notifikationen
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.work) // Ersätt med din stora ikon

        val builder = NotificationCompat.Builder(context, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.baseline_notifications_24) // Din notifikationsikon
            .setLargeIcon(largeIcon) // Den stora ikonen som visas
            .setContentTitle("Check in!") // Titeln på notifikationen
            .setContentText("Remember to check in after finishing work at 5:00 PM.") // Texten i notifikationen
            .setStyle(NotificationCompat.BigTextStyle().bigText("Remember to check in after finishing work at 5:00 PM.")) // Utökad textstil
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Sätt prioritet
            .setAutoCancel(true) // Auto avbryt när den trycks

        // Visa notifikationen
        with(NotificationManagerCompat.from(context)) {
            notify(1, builder.build()) // Visa notifikationen med ID 1
        }
    }
}

