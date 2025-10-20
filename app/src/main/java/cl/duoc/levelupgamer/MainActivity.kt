package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import cl.duoc.levelupgamer.model.repository.AuthRepository
import cl.duoc.levelupgamer.ui.LoginScreen
import cl.duoc.levelupgamer.ui.RegistrationScreen
import cl.duoc.levelupgamer.ui.CatalogScreen
import cl.duoc.levelupgamer.ui.ProductDetailScreen
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModelFactory
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.viewmodel.ProductoViewModel
import cl.duoc.levelupgamer.viewmodel.ProductoViewModelFactory

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = AppDatabase.get(applicationContext)

        setContent {
            LevelUpGamerTheme {
                val authRepository = AuthRepository()
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository))
                        LoginScreen(
                            vm = vm,
                            onRegisterClick = { navController.navigate("register") },
                            onLoggedIn = {
                                // Tras iniciar sesión correctamente, ir al catálogo
                                navController.navigate("catalog") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }
                    composable("register") {
                        val vm: RegistrationViewModel = viewModel(factory = RegistrationViewModelFactory(authRepository))
                        RegistrationScreen(
                            vm = vm,
                            onRegistered = {
                                // Tras registrarse exitosamente, debe volver al login
                                navController.popBackStack()
                            },
                            onGoToLogin = { navController.popBackStack() }
                        )
                    }
                    composable("catalog") {
                        val productosVm: ProductoViewModel = viewModel(factory = ProductoViewModelFactory(db))
                        val productos = productosVm.productos.collectAsState().value
                        CatalogScreen(products = productos) { producto ->
                            navController.navigate("productDetail/${producto.id}")
                        }
                    }
                    composable("productDetail/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
                        val productosVm: ProductoViewModel = viewModel(factory = ProductoViewModelFactory(db))
                        val productos = productosVm.productos.collectAsState().value
                        val product = productos.find { p -> p.id == productId }
                        if (product != null) {
                            ProductDetailScreen(producto = product) {
                                navController.popBackStack()
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}