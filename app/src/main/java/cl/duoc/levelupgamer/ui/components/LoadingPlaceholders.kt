package cl.duoc.levelupgamer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.shimmerEffect(): Modifier = composed {
    val gradientWidthPx = with(LocalDensity.current) { 240.dp.toPx() }
    val transition = rememberInfiniteTransition(label = "shimmer_transition")
    val offsetX = transition.animateFloat(
        initialValue = 0f,
        targetValue = gradientWidthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )
    val colorScheme = MaterialTheme.colorScheme
    val shimmerColors = remember(colorScheme.surfaceVariant, colorScheme.surface) {
        listOf(
            colorScheme.surfaceVariant.copy(alpha = 0.6f),
            colorScheme.surface.copy(alpha = 0.2f),
            colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    }
    background(
        brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(x = -gradientWidthPx, y = -gradientWidthPx),
            end = Offset(x = offsetX.value, y = offsetX.value)
        )
    )
}

@Composable
fun ShimmerProductCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )
            Spacer(
                modifier = Modifier
                    .height(18.dp)
                    .fillMaxWidth()
                    .shimmerEffect()
            )
            Spacer(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.6f)
                    .shimmerEffect()
            )
            RowPlaceholder()
        }
    }
}

@Composable
private fun RowPlaceholder() {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(
            modifier = Modifier
                .size(width = 80.dp, height = 24.dp)
                .shimmerEffect()
        )
        Spacer(
            modifier = Modifier
                .size(width = 42.dp, height = 42.dp)
                .shimmerEffect()
        )
    }
}
