package cl.duoc.levelupgamer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class LevelUpGamerApp : Application() {

    companion object {
        const val CHANNEL_ID = "compras"
    }

    override fun onCreate() {
        super.onCreate()
        crearCanalDeNotificaciones()
    }

    private fun crearCanalDeNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Compras"
            val descripcion = "Notificaciones sobre el estado de tus compras"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            
            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcion
            }


            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}
