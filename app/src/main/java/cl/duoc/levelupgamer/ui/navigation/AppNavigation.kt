package cl.duoc.levelupgamer.ui.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.ui.ChangePasswordScreen
import cl.duoc.levelupgamer.ui.EditProfileScreen
import cl.duoc.levelupgamer.ui.ProfileScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    // --- Se convierte el usuario en un estado que puede cambiar ---
    var user by remember {
        mutableStateOf(
            Usuario(
                id = 1L,
                nombre = "Usuario", // Se restaura tu nombre de usuario
                email = "usuario@email.com",
                fechaNacimiento = "01/01/2000",
                fotoPerfilUrl = null
            )
        )
    }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) } // Se añade el estado para la imagen
    // ------------------------------------------------------------

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
                user = user,
                imageUri = profileImageUri, // Se pasa el estado de la imagen
                onImageUriChange = { newUri -> profileImageUri = newUri }, // Se pasa la función para cambiarla
                onEditClick = { navController.navigate("edit_profile") },
                onLogoutClick = { context.findActivity()?.finish() }
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
                user = user,
                onBackClick = { navController.popBackStack() },
                onChangePasswordClick = { navController.navigate("change_password") },
                // Se añade la función para guardar los cambios
                onSaveChanges = { newName, newEmail ->
                    user = user.copy(nombre = newName, email = newEmail)
                    navController.popBackStack()
                }
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

// Función de ayuda para encontrar la Activity de forma segura
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}