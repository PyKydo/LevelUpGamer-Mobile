package cl.duoc.levelupgamer.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = NeonGreen,
    background = Black,
    surface = Black,
    onPrimary = Black,      // Color del texto sobre el color primario (ej. en botones azules)
    onSecondary = Black,    // Color del texto sobre el color secundario (ej. en botones verdes)
    onBackground = OffWhite,  // Color del texto sobre el fondo negro
    onSurface = OffWhite      // Color del texto sobre las superficies negras
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    secondary = NeonGreen,
    background = OffWhite,
    surface = OffWhite,
    onPrimary = OffWhite,
    onSecondary = Black,
    onBackground = Black,
    onSurface = Black
)

@Composable
fun LevelUpGamerTheme(
    darkTheme: Boolean = true, // Forzar el tema oscuro
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