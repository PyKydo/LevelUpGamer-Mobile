package cl.duoc.levelupgamer.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    onPrimary = OnDarkPrimary,
    primaryContainer = Graphite,
    onPrimaryContainer = OnDarkPrimary,
    secondary = ElectricBlue,
    onSecondary = OnDarkPrimary,
    secondaryContainer = Charcoal,
    onSecondaryContainer = OnDarkPrimary,
    tertiary = PulseCyan,
    onTertiary = OnDarkPrimary,
    background = MidnightNavy,
    onBackground = OnDarkPrimary,
    surface = DeepSpace,
    onSurface = OnDarkPrimary,
    surfaceVariant = Graphite,
    onSurfaceVariant = OnDarkPrimary,
    outline = SoftSlate,
    error = ErrorRed,
    onError = OnDarkPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = CommandBlue,
    onPrimary = CloudWhite,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = CloudWhite,
    secondary = PulseCyan,
    onSecondary = OnLightPrimary,
    secondaryContainer = MistGray,
    onSecondaryContainer = OnLightPrimary,
    tertiary = VividMagenta,
    onTertiary = CloudWhite,
    background = CloudWhite,
    onBackground = OnLightPrimary,
    surface = RetailIvory,
    onSurface = OnLightPrimary,
    surfaceVariant = MistGray,
    onSurfaceVariant = OnLightPrimary,
    outline = SoftSlate,
    error = ErrorRed,
    onError = CloudWhite
)

@Composable
fun LevelUpGamerTheme(
    darkTheme: Boolean = true,
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

    CompositionLocalProvider(LocalLevelUpSpacing provides LevelUpSpacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LevelUpTypography,
            shapes = LevelUpShapes,
            content = content
        )
    }
}