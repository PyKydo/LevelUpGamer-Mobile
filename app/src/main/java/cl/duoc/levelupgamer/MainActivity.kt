package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.repository.AuthRepository
import cl.duoc.levelupgamer.ui.navigation.LevelUpNavHost
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme
import cl.duoc.levelupgamer.viewmodel.ProductoViewModel
import cl.duoc.levelupgamer.viewmodel.ProductoViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.get(applicationContext)

        setContent {
            LevelUpGamerTheme {
                val productosVm: ProductoViewModel = viewModel(factory = ProductoViewModelFactory(database))
                val authRepository = remember { AuthRepository() }
                LevelUpNavHost(
                    productosVm = productosVm,
                    authRepository = authRepository,
                    database = database
                )
            }
        }
    }
}