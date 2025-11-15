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
import java.util.concurrent.Executors

@Database(
    entities = [Usuario::class, Producto::class, CarritoItemEntity::class],
    version = 3, // Mantenemos la versión 3, ya que la migración es destructiva
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
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Usamos SQL directo para precargar los datos, es la forma más robusta.
                            Executors.newSingleThreadExecutor().execute {
                                PREPOPULATE_DATA.forEach { producto ->
                                    db.execSQL(
                                        "INSERT INTO productos (nombre, descripcion, precio, imageUrl, categoria, codigo) VALUES (?, ?, ?, ?, ?, ?)",
                                        arrayOf(
                                            producto.nombre,
                                            producto.descripcion,
                                            producto.precio,
                                            producto.imageUrl,
                                            producto.categoria,
                                            producto.codigo
                                        )
                                    )
                                }
                            }
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            // Si la base de datos ya existía y está vacía, aseguramos pre-poblado.
                            Executors.newSingleThreadExecutor().execute {
                                val cursor = db.query("SELECT COUNT(*) FROM productos", emptyArray<Any?>())
                                var count = 0
                                if (cursor.moveToFirst()) {
                                    count = cursor.getInt(0)
                                }
                                cursor.close()
                                if (count == 0) {
                                    PREPOPULATE_DATA.forEach { producto ->
                                        db.execSQL(
                                            "INSERT INTO productos (nombre, descripcion, precio, imageUrl, categoria, codigo) VALUES (?, ?, ?, ?, ?, ?)",
                                            arrayOf(
                                                producto.nombre,
                                                producto.descripcion,
                                                producto.precio,
                                                producto.imageUrl,
                                                producto.categoria,
                                                producto.codigo
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = db
                db
            }

        private val PREPOPULATE_DATA = listOf(
            Producto(nombre = "Catan", descripcion = "Categoría: Juegos de Mesa | Código: JM001", precio = 29990.0, imageUrl = "products_jm001_catan", categoria = "Juegos de Mesa", codigo = "JM001"),
            Producto(nombre = "Carcassonne", descripcion = "Categoría: Juegos de Mesa | Código: JM002", precio = 24990.0, imageUrl = "products_jm002_carcassonne", categoria = "Juegos de Mesa", codigo = "JM002"),
            Producto(nombre = "Controlador Inalámbrico Xbox Series X", descripcion = "Categoría: Accesorios | Código: AC001", precio = 59990.0, imageUrl = "products_ac001_xbox_controller", categoria = "Accesorios", codigo = "AC001"),
            Producto(nombre = "Auriculares Gamer HyperX Cloud II", descripcion = "Categoría: Accesorios | Código: AC002", precio = 79990.0, imageUrl = "products_ac002_hyperx_cloud", categoria = "Accesorios", codigo = "AC002"),
            Producto(nombre = "PlayStation 5", descripcion = "Categoría: Consolas | Código: CO001", precio = 549990.0, imageUrl = "products_co001_ps5", categoria = "Consolas", codigo = "CO001"),
            Producto(nombre = "PC Gamer ASUS ROG Strix", descripcion = "Categoría: Computadores Gamers | Código: CG001", precio = 1299990.0, imageUrl = "products_cg001_asus_rog", categoria = "Computadores Gamers", codigo = "CG001"),
            Producto(nombre = "Silla Gamer Secretlab Titan", descripcion = "Categoría: Sillas Gamers | Código: SG001", precio = 349990.0, imageUrl = "products_sg001_secretlab_titan", categoria = "Sillas Gamers", codigo = "SG001"),
            Producto(nombre = "Mouse Gamer Logitech G502 HERO", descripcion = "Categoría: Mouse | Código: MS001", precio = 49990.0, imageUrl = "products_ms001_logitech_g502", categoria = "Mouse", codigo = "MS001"),
            Producto(nombre = "Mousepad Razer Goliathus Extended Chroma", descripcion = "Categoría: Mousepad | Código: MP001", precio = 29990.0, imageUrl = "products_mp001_razer_goliathus", categoria = "Mousepad", codigo = "MP001"),
            Producto(nombre = "Polera Gamer Personalizada 'Level-Up'", descripcion = "Categoría: Poleras Personalizadas | Código: PP001", precio = 14990.0, imageUrl = "products_pp001_levelup_tshirt", categoria = "Poleras Personalizadas", codigo = "PP001")
        )
    }
}