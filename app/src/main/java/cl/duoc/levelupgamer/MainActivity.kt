package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import cl.duoc.levelupgamer.ui.navigation.LevelUpNavHost
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme
import cl.duoc.levelupgamer.viewmodel.ProductoViewModel
import cl.duoc.levelupgamer.viewmodel.ProductoViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.get(applicationContext)

        // Garantizar pre-poblado usando el DAO (Room) para que el InvalidationTracker detecte cambios
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val count = database.productoDao().contar()
                if (count == 0) {
                    // Inserción usando el DAO (suspend)
                    AppDatabasePrepopulator.PREPOPULATE_DATA.forEach { p ->
                        database.productoDao().insertar(p)
                    }
                }
            } catch (t: Throwable) {
                // Si algo falla aquí, no queremos bloquear el arranque; sólo loguear en debug.
                t.printStackTrace()
            }
        }

        setContent {
            LevelUpGamerTheme {
                val productosVm: ProductoViewModel = viewModel(factory = ProductoViewModelFactory(database))
                val usuarioRepository = remember { UsuarioRepository(database.usuarioDao()) }
                LevelUpNavHost(
                    productosVm = productosVm,
                    usuarioRepository = usuarioRepository,
                    database = database
                )
            }
        }
    }
}

// Pequeño helper para exponer los datos de pre-poblado sin duplicar contenido en MainActivity
internal object AppDatabasePrepopulator {
    val PREPOPULATE_DATA = listOf(
        cl.duoc.levelupgamer.model.Producto(nombre = "Catan", descripcion = "Categoría: Juegos de Mesa | Código: JM001", precio = 29990.0, imageUrl = "products_jm001_catan", categoria = "Juegos de Mesa", codigo = "JM001"),
        cl.duoc.levelupgamer.model.Producto(nombre = "Carcassonne", descripcion = "Categoría: Juegos de Mesa | Código: JM002", precio = 24990.0, imageUrl = "products_jm002_carcassonne", categoria = "Juegos de Mesa", codigo = "JM002"),
        cl.duoc.levelupgamer.model.Producto(nombre = "Controlador Inalámbrico Xbox Series X", descripcion = "Categoría: Accesorios | Código: AC001", precio = 59990.0, imageUrl = "products_ac001_xbox_controller", categoria = "Accesorios", codigo = "AC001"),
        cl.duoc.levelupgamer.model.Producto(nombre = "Auriculares Gamer HyperX Cloud II", descripcion = "Categoría: Accesorios | Código: AC002", precio = 79990.0, imageUrl = "products_ac002_hyperx_cloud", categoria = "Accesorios", codigo = "AC002"),
        cl.duoc.levelupgamer.model.Producto(nombre = "PlayStation 5", descripcion = "Categoría: Consolas | Código: CO001", precio = 549990.0, imageUrl = "products_co001_ps5", categoria = "Consolas", codigo = "CO001"),
        cl.duoc.levelupgamer.model.Producto(nombre = "PC Gamer ASUS ROG Strix", descripcion = "Categoría: Computadores Gamers | Código: CG001", precio = 1299990.0, imageUrl = "products_cg001_asus_rog", categoria = "Computadores Gamers", codigo = "CG001"),
        cl.duoc.levelupgamer.model.Producto(nombre = "Silla Gamer Secretlab Titan", descripcion = "Categoría: Sillas Gamers | Código: SG001", precio = 349990.0, imageUrl = "products_sg001_secretlab_titan", categoria = "Sillas Gamers", codigo = "SG001"),
        cl.duoc.levelupgamer.model.Producto(nombre = "Mouse Gamer Logitech G502 HERO", descripcion = "Categoría: Mouse | Código: MS001", precio = 49990.0, imageUrl = "products_ms001_logitech_g502", categoria = "Mouse", codigo = "MS001"),
        cl.duoc.levelupgamer.model.Producto(nombre = "Mousepad Razer Goliathus Extended Chroma", descripcion = "Categoría: Mousepad | Código: MP001", precio = 29990.0, imageUrl = "products_mp001_razer_goliathus", categoria = "Mousepad", codigo = "MP001"),
        cl.duoc.levelupgamer.model.Producto(nombre = "Polera Gamer Personalizada 'Level-Up'", descripcion = "Categoría: Poleras Personalizadas | Código: PP001", precio = 14990.0, imageUrl = "products_pp001_levelup_tshirt", categoria = "Poleras Personalizadas", codigo = "PP001")
    )
}
