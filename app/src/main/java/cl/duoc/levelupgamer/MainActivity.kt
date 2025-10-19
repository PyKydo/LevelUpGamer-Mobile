package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.levelupgamer.model.repository.AuthRepository
import cl.duoc.levelupgamer.ui.LoginScreen
import cl.duoc.levelupgamer.ui.RegistrationScreen
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModelFactory
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUpGamerTheme {
                val authRepository = AuthRepository()
                val navController = rememberNavController()
                NavHost(
                    navController = navController, 
                    startDestination = "login"
                ) {
                    composable(
                        "login",
                        exitTransition = {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left, 
                                animationSpec = tween(700)
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
                            onRegisterClick = { navController.navigate("register") },
                            onLoggedIn = {
                                // Aun nada
                            }
                        )
                    }
                    composable(
                        "register",
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
                            onRegistered = {
                                navController.popBackStack()
                            },
                            onGoToLogin = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}