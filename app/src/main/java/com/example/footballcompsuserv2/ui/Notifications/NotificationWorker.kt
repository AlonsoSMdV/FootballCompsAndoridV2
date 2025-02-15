package com.example.footballcompsuserv2.ui.Notifications

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

    override suspend fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val channelId = "noti_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificaciones de la App",
                NotificationManager.IMPORTANCE_HIGH // Cambio a HIGH para que sea emergente
            ).apply {
                description = "Notificaciones importantes sobre competiciones"
                enableVibration(true) // Activa vibración para más impacto
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("¡Hora de revisar la app!")
            .setContentText("No te pierdas las últimas noticias y competiciones.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Cambio a HIGH para que sea emergente
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Sonido, vibración, luces
            .setAutoCancel(true) // Se elimina al tocar
            .build()

        notificationManager.notify(1, notification)
    }
}