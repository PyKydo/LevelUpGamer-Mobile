package cl.duoc.levelupgamer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import cl.duoc.levelupgamer.ui.theme.spacing

@Composable
fun GlowCard(
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface,
    glow: Brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.35f)
        )
    ),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)
    val glowPadding = (MaterialTheme.spacing.xs / 2f).dp
    val contentPadding = MaterialTheme.spacing.lg.dp
    Box(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, clip = false)
            .background(glow, shape)
            .padding(glowPadding)
            .clip(shape)
            .background(background)
            .padding(contentPadding)
    ) {
        content()
    }
}
