package cl.duoc.levelupgamer.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Paleta de colores para el tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,    // Botones y elementos destacados
    secondary = NeonGreen,     // Acentos secundarios
    background = Black,        // Fondo predominante
    surface = DarkGray,        // Superficies elevadas
    onPrimary = OffWhite,      // Texto sobre azul eléctrico
    onSecondary = Black,       // Texto sobre verde neón
    onBackground = OffWhite,   // Texto sobre fondo oscuro
    onSurface = OffWhite       // Texto sobre superficies
)

// El tema claro no se usará, pero lo dejamos definido
private val LightColorScheme = lightColorScheme(
    primary = NeonGreen,
    secondary = NeonGreen,
    background = OffWhite,
    surface = OffWhite,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun LevelUpGamerTheme(
    darkTheme: Boolean = true, // Forzamos el tema oscuro
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}