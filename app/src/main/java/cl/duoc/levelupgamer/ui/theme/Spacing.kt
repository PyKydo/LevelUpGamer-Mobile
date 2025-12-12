package cl.duoc.levelupgamer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

data class LevelUpSpacing(
    val xs: Float = 4f,
    val sm: Float = 8f,
    val md: Float = 16f,
    val lg: Float = 24f,
    val xl: Float = 32f,
    val xxl: Float = 40f
) {
    val xsDp get() = xs.dp
    val smDp get() = sm.dp
    val mdDp get() = md.dp
    val lgDp get() = lg.dp
    val xlDp get() = xl.dp
    val xxlDp get() = xxl.dp
}

val LocalLevelUpSpacing = staticCompositionLocalOf { LevelUpSpacing() }

val MaterialTheme.spacing: LevelUpSpacing
    @Composable
    get() = LocalLevelUpSpacing.current
