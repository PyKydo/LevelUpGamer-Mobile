package cl.duoc.levelupgamer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.levelupgamer.ui.navigation.AppNavigation
import cl.duoc.levelupgamer.ui.theme.LevelUpGamerTheme

class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LevelUpGamerTheme {
                AppNavigation() // Aquí se carga toda la navegación
            }
        }
    }
}