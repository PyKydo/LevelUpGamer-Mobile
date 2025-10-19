package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

        // --- Datos de ejemplo (Fuente única de la verdad) ---
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
                    composable("profile") {
                        // Se le pasa el usuario a la pantalla de perfil
                        ProfileScreen(
                            user = sampleUser, 
                            onEditClick = { navController.navigate("editProfile") }
                        )
                    }
                    composable("editProfile") {
                        EditProfileScreen(user = sampleUser, onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}