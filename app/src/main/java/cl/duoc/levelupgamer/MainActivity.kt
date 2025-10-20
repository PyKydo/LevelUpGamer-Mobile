package cl.duoc.levelupgamer

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.levelupgamer.model.Usuario
import cl.duoc.levelupgamer.ui.ChangePasswordScreen
import cl.duoc.levelupgamer.ui.EditProfileScreen
import cl.duoc.levelupgamer.ui.ProfileScreen
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LevelUpGamerTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // --- Se centraliza el estado del usuario y la imagen aquí ---
                var user by remember {
                    mutableStateOf(
                        Usuario(
                            id = 1L,
                            nombre = "Usuario", // Se restaura tu nombre
                            email = "usuariogamer@email.com",
                            fechaNacimiento = "01/01/2000",
                            fotoPerfilUrl = null
                        )
                    )
                }
                var profileImageUri by remember { mutableStateOf<Uri?>(null) }
                // ----------------------------------------------------------------

                NavHost(navController = navController, startDestination = "profile") {
                    composable("login") { 
                        LoginScreen()
                    }
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
                            imageUri = profileImageUri,
                            onImageUriChange = { newUri -> profileImageUri = newUri },
                            onEditClick = { navController.navigate("editProfile") },
                            onLogoutClick = { context.findActivity()?.finish() }
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
                        EditProfileScreen(
                            user = user,
                            onBackClick = { navController.popBackStack() },
                            onChangePasswordClick = { navController.navigate("change_password") },
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
        }
    }
}

@Composable
fun LoginScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Pantalla de Login")
    }
}

// Función de ayuda para encontrar la Activity de forma segura
private fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}