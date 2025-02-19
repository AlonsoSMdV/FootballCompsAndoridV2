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
import kotlin.random.Random

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
            prefs.edit().putBoolean(KEY_WELCOME_SENT, true).apply() // Marca la bienvenida como enviada
        } else {
            val notificationCount = Random.nextInt(1, 4) // Genera entre 1 y 3 notificaciones
            for (i in 1..notificationCount) {
                showRegularNotification(i)
            }
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

    private fun showRegularNotification(notificationId: Int) {
        val channelId = "noti_channel"
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val sentMessages = prefs.getStringSet("sent_messages", mutableSetOf()) ?: mutableSetOf()

        val messages = listOf(
            "No te pierdas las últimas competiciones 🔥",
            "¡Revisa los próximos partidos! ⚽",
            "¡Sigue a tus equipos favoritos! 📊",
            "¿Ya viste las nuevas noticias del fútbol? 📰",
            "Descubre los equipos en tendencia 📈"
        )

        // Filtramos los mensajes que ya fueron enviados
        val availableMessages = messages.filterNot { sentMessages.contains(it) }

        if (availableMessages.isNotEmpty()) {
            val randomMessage = availableMessages.random() // Selecciona un mensaje aleatorio

            // Guardamos el mensaje como enviado
            sentMessages.add(randomMessage)
            prefs.edit().putStringSet("sent_messages", sentMessages).apply()

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("⚽ ¡¡¡ Atención !!!")
                .setContentText(randomMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(100 + notificationId, notification)
        } else {
            // Si ya se enviaron todos los mensajes, reiniciamos la lista
            prefs.edit().remove("sent_messages").apply()
            showRegularNotification(notificationId) // Vuelve a enviar un mensaje cuando todos hayan sido enviados
        }
    }
}