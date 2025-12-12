package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cl.duoc.levelupgamer.BuildConfig
import cl.duoc.levelupgamer.R
import cl.duoc.levelupgamer.model.Blog
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.ui.components.GlowCard
import cl.duoc.levelupgamer.ui.components.MetricChip
import cl.duoc.levelupgamer.ui.components.SectionHeader
import cl.duoc.levelupgamer.ui.theme.LocalLevelUpSpacing
import cl.duoc.levelupgamer.ui.resolveProductImageResId
import cl.duoc.levelupgamer.util.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    productosDestacados: List<Producto>,
    blogsDestacados: List<Blog>,
    totalProductos: Int,
    puntosUsuario: Int,
    productosEnCarrito: Int,
    onProductClick: (Producto) -> Unit,
    onBlogClick: (Blog) -> Unit,
    onProfileClick: () -> Unit
) {
    val spacing = LocalLevelUpSpacing.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explora LevelUp") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Ir al perfil"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = spacing.mdDp, vertical = spacing.smDp),
            verticalArrangement = Arrangement.spacedBy(spacing.lgDp)
        ) {
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.smDp)) {
                    Text(
                        text = "Resumen",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.smDp)
                    ) {
                        MetricChip(
                            label = "Tus puntos",
                            value = puntosUsuario.coerceAtLeast(0).toString(),
                            modifier = Modifier.weight(1f)
                        )
                        MetricChip(
                            label = "En carrito",
                            value = productosEnCarrito.coerceAtLeast(0).toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.smDp)
                    ) {
                        MetricChip(
                            label = "Productos",
                            value = totalProductos.coerceAtLeast(0).toString(),
                            modifier = Modifier.weight(1f)
                        )
                        MetricChip(
                            label = "Blogs",
                            value = blogsDestacados.size.coerceAtLeast(0).toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            SectionHeader(title = "Productos destacados")
            if (productosDestacados.isEmpty()) {
                Text(
                    text = "Aún no hay productos destacados, vuelve pronto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.smDp),
                    contentPadding = PaddingValues(horizontal = spacing.xsDp)
                ) {
                    items(productosDestacados, key = { it.id }) { producto ->
                        FeaturedProductTile(producto = producto, onClick = { onProductClick(producto) })
                    }
                }
            }

            SectionHeader(title = "Blogs destacados")
            if (blogsDestacados.isEmpty()) {
                Text(
                    text = "Publicaremos nuevas guías y reseñas muy pronto.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.smDp),
                    contentPadding = PaddingValues(horizontal = spacing.xsDp)
                ) {
                    items(blogsDestacados, key = { it.id }) { blog ->
                        FeaturedBlogCard(blog = blog, onClick = { onBlogClick(blog) })
                    }
                }
            }

        }
    }
}

@Composable
private fun FeaturedProductTile(
    producto: Producto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageResId = remember(producto.codigo, producto.imageUrl) { resolveProductImageResId(context, producto) }
    val formattedPrice = remember(producto.precio) { formatCurrency(producto.precio) }
    GlowCard(
        modifier = modifier
            .width(240.dp)
            .heightIn(min = 280.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        background = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                val explicitUrl = producto.imageUrl.trim()
                if (explicitUrl.startsWith("http", ignoreCase = true) || explicitUrl.startsWith("/")) {
                    val model = if (explicitUrl.startsWith("/")) {
                        BuildConfig.API_BASE_URL.trimEnd('/') + explicitUrl
                    } else explicitUrl
                    AsyncImage(
                        model = model,
                        contentDescription = producto.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = imageResId),
                        error = painterResource(id = imageResId)
                    )
                } else {
                    Image(
                        painter = painterResource(id = imageResId),
                        contentDescription = producto.nombre,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FeaturedBlogCard(
    blog: Blog,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageModel = remember(blog.imageUrl) { resolveBlogImageUrl(blog.imageUrl) }
    GlowCard(
        modifier = modifier
            .width(320.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        background = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = blog.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.logo)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = blog.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = blog.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = blog.summary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Leer artículo",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun resolveBlogImageUrl(raw: String?): String? {
    val value = raw?.trim().orEmpty()
    if (value.isEmpty()) return null
    if (value.startsWith("http", ignoreCase = true)) return value
    return if (value.startsWith("/")) {
        BuildConfig.API_BASE_URL.trimEnd('/') + value
    } else {
        null
    }
}

