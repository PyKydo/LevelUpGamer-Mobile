package cl.duoc.levelupgamer.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import cl.duoc.levelupgamer.LevelUpGamerApp
import cl.duoc.levelupgamer.R

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun mostrarNotificacionCompraExitosa() {
        val longText = "Tu pedido ha sido procesado y está en camino. Recibirás una actualización por correo cuando sea enviado, ¡gracias por tu compra!"

        val notification = NotificationCompat.Builder(context, LevelUpGamerApp.CHANNEL_ID)
            .setContentTitle("¡Compra Exitosa!")
            .setContentText(longText) // Texto que se ve cuando está contraída
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Asegúrate de tener este ícono
            // 1. Aumentamos la prioridad para que aparezca como "heads-up" y dure más en pantalla.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // 2. Añadimos un estilo para que el texto completo se pueda expandir y leer.
            .setStyle(NotificationCompat.BigTextStyle().bigText(longText))
            .setAutoCancel(false) // La notificación no se cierra al tocarla
            .build()

        // Usamos un ID único para cada notificación para que no se sobreescriban
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
