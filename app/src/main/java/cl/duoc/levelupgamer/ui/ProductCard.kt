package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import cl.duoc.levelupgamer.BuildConfig
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.ui.resolveProductImageResId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(producto: Producto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        val context = LocalContext.current
        val imageResId = remember(producto.codigo, producto.imageUrl) {
            resolveProductImageResId(context, producto)
        }
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
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
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    minLines = 2, 
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$${producto.precio}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
