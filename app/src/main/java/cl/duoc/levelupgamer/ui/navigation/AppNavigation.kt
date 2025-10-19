package cl.duoc.levelupgamer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.levelupgamer.model.repository.AuthRepository
import cl.duoc.levelupgamer.navigation.AppScreen
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
    val authRepository = remember { AuthRepository() }

    NavHost(
        navController = navController, 
        startDestination = AppScreen.Splash.route
    ) {
        composable(AppScreen.Splash.route) {
            SplashScreen(navController)
        }
        composable(
            AppScreen.Login.route,
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left, 
                    animationSpec = tween(700) // Se restaura la duración a 700ms
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right, 
                    animationSpec = tween(700)
                )
            }
        ) {
            val vm: LoginViewModel = viewModel(factory = LoginViewModelFactory(authRepository))
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
        composable(
            AppScreen.Register.route,
            enterTransition = { 
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left, 
                    animationSpec = tween(700)
                )
            },
            popExitTransition = { 
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right, 
                    animationSpec = tween(700)
                )
            }
        ) {
            val vm: RegistrationViewModel = viewModel(factory = RegistrationViewModelFactory(authRepository))
            RegistrationScreen(
                vm = vm,
                onRegistered = { navController.popBackStack() },
                onGoToLogin = { navController.popBackStack() }
            )
        }
    }
}