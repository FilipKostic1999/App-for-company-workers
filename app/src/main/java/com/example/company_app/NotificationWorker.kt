import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.company_app.R

class NotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("NotificationWorker", "doWork called") // Log when doWork is called
        showNotification() // Call the method to show the notification
        Log.d("NotificationWorker", "Notification shown") // Log after the notification is shown
        return Result.success() // Indicate that the work is complete
    }

    private fun showNotification() {
        // Create a notification channel (for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "YOUR_CHANNEL_ID"
            val channelName = "Your Channel Name"
            val channelDescription = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
            }

            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val builder = NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.baseline_notifications_24) // Your notification icon
            .setContentTitle("Check in!") // Title of the notification
            .setContentText("Remember to check in after finishing work at 5:00 PM.") // Text of the notification
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Set priority (for compatibility)
            .setAutoCancel(true) // Auto cancel the notification when tapped

        // Show the notification
        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Handle the case where permission is not granted
                return
            }
            notify(1, builder.build()) // Show the notification with ID 1
        }
    }
}
