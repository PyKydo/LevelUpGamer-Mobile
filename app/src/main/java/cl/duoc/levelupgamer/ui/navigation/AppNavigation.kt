package cl.duoc.levelupgamer.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.ui.ChangePasswordScreen
import cl.duoc.levelupgamer.ui.EditProfileScreen
import cl.duoc.levelupgamer.ui.ProfileScreen

@Composable
fun AppNavigation() {
    // --- Datos de ejemplo (corregidos para que coincidan con la clase Usuario) ---
    val sampleUser = Usuario(
        id = 1L,
        nombre = "Usuario",
        email = "usuario@email.com",
        fechaNacimiento = "01/01/2000",
        fotoPerfilUrl = null
    )
    // -----------------------------------------------------

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
                onEditClick = { navController.navigate("edit_profile") }
            )
        }
        composable(
            "edit_profile",
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
                onBackClick = { navController.popBackStack() },
                onChangePasswordClick = { navController.navigate("change_password") } // <-- Conectamos el nuevo campo
            )
        }
        composable(
            "change_password",
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
            ChangePasswordScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
