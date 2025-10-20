package cl.duoc.levelupgamer


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupgamer.model.repository.AuthRepository
import cl.duoc.levelupgamer.ui.LoginScreen
import cl.duoc.levelupgamer.ui.RegistrationScreen
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.ui.EditProfileScreen
import cl.duoc.levelupgamer.ui.ProfileScreen
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme
import cl.duoc.levelupgamer.viewmodel.LoginViewModel
import cl.duoc.levelupgamer.viewmodel.LoginViewModelFactory
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModel
import cl.duoc.levelupgamer.viewmodel.RegistrationViewModelFactory
import cl.duoc.levelupgamer.model.local.AppDatabase

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sampleUser = Usuario(
            id = 1,
            nombre = "Usuario",
            email = "usuario@email.com",
            fechaNacimiento = "01/01/2000",
            fotoPerfilUrl = null
        )
        // -----------------------------------------------------

        setContent {
            LevelUpGamerTheme {
                // Inicialización de Room si aplica
                AppDatabase.get(applicationContext)

                val authRepository = AuthRepository()
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable(
                        route = "login",
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
                                navController.navigate("profile") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(
                        route = "register",
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

                    composable(route = "profile") {
                        ProfileScreen(
                            user = sampleUser,
                            onEditClick = { navController.navigate("editProfile") }
                        )
                    }

                    composable(
                        route = "editProfile",
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
                        EditProfileScreen(
                            user = sampleUser,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}