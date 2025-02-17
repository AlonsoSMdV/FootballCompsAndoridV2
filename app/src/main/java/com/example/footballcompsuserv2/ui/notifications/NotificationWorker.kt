package com.example.footballcompsuserv2.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.footballcompsuserv2.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val PREFS_NAME = "NotificationPrefs"
    private val KEY_WELCOME_SENT = "welcome_sent"

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isWelcomeSent = prefs.getBoolean(KEY_WELCOME_SENT, false)

        if (!isWelcomeSent) {
            showWelcomeNotification()
            prefs.edit().putBoolean(KEY_WELCOME_SENT, true).apply() // Marcar bienvenida como enviada
        } else {
            showRegularNotification()
        }

        return Result.success()
    }

    private fun showWelcomeNotification() {
        val channelId = "noti_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de la App",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("¡Bienvenido a la app!")
            .setContentText("Gracias por unirte, recibirás notificaciones importantes aquí.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun showRegularNotification() {
        val channelId = "noti_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("¡Hora de revisar la app!")
            .setContentText("No te pierdas las últimas noticias y competiciones.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification) // Cambia el ID para que no reemplace la bienvenida
    }
}