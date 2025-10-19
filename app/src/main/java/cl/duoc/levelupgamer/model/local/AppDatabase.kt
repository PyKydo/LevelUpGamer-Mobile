package cl.duoc.levelupgamer.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import cl.duoc.levelupgamer.model.local.dao.UsuarioDao

@Database(
    entities = [Usuario::class, Producto::class, CarritoItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun carritoItemDao(): CarritoItemDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelupgamer.db"
                ).build().also { INSTANCE = it }
            }
    }
}
