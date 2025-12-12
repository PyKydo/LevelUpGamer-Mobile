package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.BuildConfig
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.util.formatCurrency
import cl.duoc.levelupgamer.ui.theme.spacing
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    producto: Producto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onAddClick: (Producto) -> Unit
) {
    val formattedPrice = remember(producto.precio) { formatCurrency(producto.precio) }
    val context = LocalContext.current
    val imageResId = remember(producto.codigo, producto.imageUrl) {
        resolveProductImageResId(context, producto)
    }
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val explicitUrl = producto.imageUrl.trim()
            if (explicitUrl.startsWith("http", ignoreCase = true) || explicitUrl.startsWith("/")) {
                val model = if (explicitUrl.startsWith("/")) {
                    BuildConfig.API_BASE_URL.trimEnd('/') + explicitUrl
                } else explicitUrl
                AsyncImage(
                    model = model,
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = imageResId),
                    error = painterResource(id = imageResId)
                )
            } else {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = producto.nombre,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md.dp)) {
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                FilledTonalIconButton(onClick = { onAddClick(producto) }) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "Agregar al carrito"
                    )
                }
            }
        }
    }
}
