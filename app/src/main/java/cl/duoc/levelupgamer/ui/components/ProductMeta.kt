package cl.duoc.levelupgamer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Producto
import kotlin.math.roundToInt

@Composable
fun RatingBar(
    rating: Float,
    maxRating: Int = 5,
    modifier: Modifier = Modifier,
    showValue: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(maxRating) { index ->
            val tint = if (index + 1 <= rating) colorScheme.primary else colorScheme.outline
            Icon(
                imageVector = if (index + 1 <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
        if (showValue) {
            Text(
                text = String.format("%.1f", rating),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

enum class ProductStatus { AVAILABLE, LOW_STOCK, OUT_OF_STOCK }

@Composable
fun StatusChip(status: ProductStatus, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val (label, color) = when (status) {
        ProductStatus.AVAILABLE -> "Disponible" to colorScheme.primary
        ProductStatus.LOW_STOCK -> "Últimas unidades" to colorScheme.tertiary
        ProductStatus.OUT_OF_STOCK -> "Agotado" to colorScheme.error
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
            .background(color.copy(alpha = 0.15f), MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

data class ProductBadge(val text: String, val containerColor: Color, val contentColor: Color)

@Composable
fun rememberProductBadge(producto: Producto): ProductBadge? {
    val colorScheme = MaterialTheme.colorScheme
    val discountBadge = producto.descuento?.takeIf { it > 0 }?.let { discount ->
        val percent = (discount * 100).roundToInt().coerceAtLeast(1)
        ProductBadge(
            text = "-${percent}%",
            containerColor = colorScheme.error,
            contentColor = colorScheme.onError
        )
    }
    return when {
        discountBadge != null -> discountBadge
        producto.codigo.startsWith("N", ignoreCase = true) || producto.id % 4L == 0L ->
            ProductBadge(
                text = "Nuevo",
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary
            )
        producto.stock >= 10 ->
            ProductBadge(
                text = "Popular",
                containerColor = colorScheme.secondary,
                contentColor = colorScheme.onSecondary
            )
        else -> null
    }
}

@Composable
fun rememberProductStatus(producto: Producto): ProductStatus = remember(producto.id, producto.stock) {
    when {
        producto.stock <= 0 -> ProductStatus.OUT_OF_STOCK
        producto.stock in 1..5 -> ProductStatus.LOW_STOCK
        else -> ProductStatus.AVAILABLE
    }
}

@Composable
fun rememberProductRating(producto: Producto): Float = remember(producto.id, producto.descuento, producto.stock) {
    val base = ((producto.id % 5) + 3).coerceAtMost(5).toInt()
    val bonus = if ((producto.descuento ?: 0.0) > 0.0) 0.3f else 0f
    (base + bonus).coerceIn(3f, 5f)
}

@Composable
fun ProductBadgeView(badge: ProductBadge, modifier: Modifier = Modifier) {
    Badge(
        modifier = modifier,
        containerColor = badge.containerColor,
        contentColor = badge.contentColor,
        content = {
            Text(
                text = badge.text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    )
}
