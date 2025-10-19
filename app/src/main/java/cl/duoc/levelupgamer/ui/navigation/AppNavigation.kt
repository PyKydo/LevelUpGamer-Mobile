package cl.duoc.levelupgamer.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.levelupgamer.model.repository.AuthRepository
import cl.duoc.levelupgamer.navigation.AppScreen
import cl.duoc.levelupgamer.ui.CatalogScreen
import cl.duoc.levelupgamer.ui.LoginScreen
import cl.duoc.levelupgamer.ui.Navegation.splash.SplashScreen
import cl.duoc.levelupgamer.ui.RegistrationScreen
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModelFactory
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // 1. Creamos el repositorio que ambos ViewModels necesitan
    val authRepository = remember { AuthRepository() }

    NavHost(navController = navController, startDestination = AppScreen.Splash.route) {
        composable(AppScreen.Splash.route) {
            SplashScreen(navController)
        }
        composable(AppScreen.Login.route) {
            // 2. Creamos la Factory para el Login y se la pasamos al ViewModel
            val vm: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(authRepository)
            )
            LoginScreen(
                vm = vm,
                onLoggedIn = {
                    navController.navigate(AppScreen.Catalog.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(AppScreen.Register.route) }
            )
        }
        composable(AppScreen.Register.route) {
            // 3. Hacemos lo mismo para el RegistrationViewModel
            val vm: RegistrationViewModel = viewModel(
                factory = RegistrationViewModelFactory(authRepository)
            )
            RegistrationScreen(
                vm = vm,
                onRegistered = {
                    navController.popBackStack()
                },
                onGoToLogin = {
                    navController.popBackStack()
                }
            )
        }
    }
}