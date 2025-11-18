package cl.duoc.levelupgamer.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.local.dao.ProductoDao

@Database(
	entities = [Producto::class, CarritoItemEntity::class],
	version = 5,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
	abstract fun productoDao(): ProductoDao
	abstract fun carritoItemDao(): CarritoItemDao

	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun get(context: Context): AppDatabase =
			INSTANCE ?: synchronized(this) {
				Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"levelupgamer.db"
				)
					.fallbackToDestructiveMigration(true)
					.build()
					.also { INSTANCE = it }
			}
	}
}
