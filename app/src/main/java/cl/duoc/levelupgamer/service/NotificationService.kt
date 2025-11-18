package cl.duoc.levelupgamer.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import cl.duoc.levelupgamer.LevelUpGamerApp
import cl.duoc.levelupgamer.R

class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)

    fun mostrarNotificacionCompraExitosa(pedidoId: Long? = null) {
        val title = if (pedidoId != null) "Pago realizado (Pedido #$pedidoId)" else "¡Compra Exitosa!"
        val longText = if (pedidoId != null)
            "Pago realizado exitosamente (Pedido #$pedidoId). Tu pedido está en proceso y recibirás una notificación cuando sea enviado. ¡Gracias por tu compra!"
        else
            "Tu pedido ha sido procesado y está en camino. Recibirás una actualización por correo cuando sea enviado, ¡gracias por tu compra!"

        val notification = NotificationCompat.Builder(context, LevelUpGamerApp.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(longText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(longText))
            .setAutoCancel(false)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
