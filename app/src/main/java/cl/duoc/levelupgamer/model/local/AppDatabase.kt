package cl.duoc.levelupgamer.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.model.local.dao.CarritoItemDao
import cl.duoc.levelupgamer.model.local.dao.ProductoDao
import cl.duoc.levelupgamer.model.local.dao.UsuarioDao

@Database(
    entities = [Usuario::class, Producto::class, CarritoItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun carritoItemDao(): CarritoItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "levelupgamer.db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            fun insert(
                                nombre: String,
                                descripcion: String,
                                precio: Double,
                                imageUrl: String = "",
                                categoria: String,
                                codigo: String
                            ) {
                                db.execSQL(
                                    "INSERT INTO productos (nombre, descripcion, precio, imageUrl, categoria, codigo) VALUES (?, ?, ?, ?, ?, ?)",
                                    arrayOf(nombre, descripcion, precio, imageUrl, categoria, codigo)
                                )
                            }

                            insert(
                                nombre = "Catan",
                                descripcion = "Categoría: Juegos de Mesa | Código: JM001",
                                precio = 29990.0,
                                categoria = "Juegos de Mesa",
                                codigo = "JM001"
                            )
                            insert(
                                nombre = "Carcassonne",
                                descripcion = "Categoría: Juegos de Mesa | Código: JM002",
                                precio = 24990.0,
                                categoria = "Juegos de Mesa",
                                codigo = "JM002"
                            )
                            insert(
                                nombre = "Controlador Inalámbrico Xbox Series X",
                                descripcion = "Categoría: Accesorios | Código: AC001",
                                precio = 59990.0,
                                categoria = "Accesorios",
                                codigo = "AC001"
                            )
                            insert(
                                nombre = "Auriculares Gamer HyperX Cloud II",
                                descripcion = "Categoría: Accesorios | Código: AC002",
                                precio = 79990.0,
                                categoria = "Accesorios",
                                codigo = "AC002"
                            )
                            insert(
                                nombre = "PlayStation 5",
                                descripcion = "Categoría: Consolas | Código: CO001",
                                precio = 549990.0,
                                categoria = "Consolas",
                                codigo = "CO001"
                            )
                            insert(
                                nombre = "PC Gamer ASUS ROG Strix",
                                descripcion = "Categoría: Computadores Gamers | Código: CG001",
                                precio = 1299990.0,
                                categoria = "Computadores Gamers",
                                codigo = "CG001"
                            )
                            insert(
                                nombre = "Silla Gamer Secretlab Titan",
                                descripcion = "Categoría: Sillas Gamers | Código: SG001",
                                precio = 349990.0,
                                categoria = "Sillas Gamers",
                                codigo = "SG001"
                            )
                            insert(
                                nombre = "Mouse Gamer Logitech G502 HERO",
                                descripcion = "Categoría: Mouse | Código: MS001",
                                precio = 49990.0,
                                categoria = "Mouse",
                                codigo = "MS001"
                            )
                            insert(
                                nombre = "Mousepad Razer Goliathus Extended Chroma",
                                descripcion = "Categoría: Mousepad | Código: MP001",
                                precio = 29990.0,
                                categoria = "Mousepad",
                                codigo = "MP001"
                            )
                            insert(
                                nombre = "Polera Gamer Personalizada 'Level-Up'",
                                descripcion = "Categoría: Poleras Personalizadas | Código: PP001",
                                precio = 14990.0,
                                categoria = "Poleras Personalizadas",
                                codigo = "PP001"
                            )
                        }
                    })
                    .build()
                INSTANCE = db
                db
            }

    }
}
