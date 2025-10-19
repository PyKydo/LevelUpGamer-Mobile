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
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.ui.EditProfileScreen
import cl.duoc.levelupgamer.ui.ProfileScreen
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // --- Datos de ejemplo  ---
        val sampleUser = Usuario(
            id = 1,
            nombre = "Usuario",
            email = "usuario@email.com",
            contrasena = "",
            fechaNacimiento = "01/01/2000",
            fotoPerfilUrl = null
        )
        // -----------------------------------------------------

        setContent {
            LevelUpGamerTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "profile") {
                    composable(
                        "profile",
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
                        ProfileScreen(
                            user = sampleUser, 
                            onEditClick = { navController.navigate("editProfile") }
                        )
                    }
                    composable(
                        "editProfile",
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
                        EditProfileScreen(user = sampleUser, onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}