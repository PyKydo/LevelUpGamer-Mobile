package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupgamer.di.ServiceLocator
import cl.duoc.levelupgamer.ui.navigation.LevelUpNavHost
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme
import cl.duoc.levelupgamer.viewmodel.ProductoViewModel
import cl.duoc.levelupgamer.viewmodel.ProductoViewModelFactory

class MainActivity : ComponentActivity() {

    private val serviceLocator by lazy { ServiceLocator.get(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val usuarioRepository = serviceLocator.usuarioRepository
        val productoRepository = serviceLocator.productoRepository
        val blogRepository = serviceLocator.blogRepository
        val carritoRepository = serviceLocator.carritoRepository
        val pedidoRepository = serviceLocator.pedidoRepository

        setContent {
            LevelUpGamerTheme {
                val productosVm: ProductoViewModel = viewModel(factory = ProductoViewModelFactory(productoRepository))
                LevelUpNavHost(
                    productosVm = productosVm,
                    usuarioRepository = usuarioRepository,
                    carritoRepository = carritoRepository,
                    pedidoRepository = pedidoRepository,
                    blogRepository = blogRepository
                )
            }
        }
    }
}
