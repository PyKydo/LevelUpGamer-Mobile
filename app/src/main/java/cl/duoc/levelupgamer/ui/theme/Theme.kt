package cl.duoc.levelupgamer.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.platform.LocalContext

// Paleta de colores para el tema oscuro
private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,       // Color principal para elementos interactivos
    secondary = NeonGreen,     // Color secundario, también verde
    background = Black,        // Fondo principal de la app
    surface = DarkGray,        // Color para superficies como tarjetas
    onPrimary = Black,         // Texto sobre el color primario (verde)
    onSecondary = Black,       // Texto sobre el color secundario (verde)
    onBackground = OffWhite,   // Texto sobre el fondo principal (negro)
    onSurface = OffWhite       // Texto sobre superficies (tarjetas)
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