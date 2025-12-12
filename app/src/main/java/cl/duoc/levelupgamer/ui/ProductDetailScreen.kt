package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.BuildConfig
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.ui.components.LevelUpHighContrastOnPrimary
import cl.duoc.levelupgamer.util.formatCurrency
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    producto: Producto,
    onBackClick: () -> Unit,
    onAddToCart: (Producto) -> Boolean,
    onGoToCart: () -> Unit
) {
    var isExpanded by remember(producto.id) { mutableStateOf(false) }
    var selectedImageIndex by remember(producto.id) { mutableStateOf(0) }
    val context = LocalContext.current
    val imageResId = remember(producto.codigo, producto.imageUrl) {
        resolveProductImageResId(context, producto)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val imageGallery = remember(producto.imageUrl, producto.gallery) { buildProductGallery(producto) }

    val formattedPrice = remember(producto.precio) { formatCurrency(producto.precio) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            ProductImageGallery(
                images = imageGallery,
                selectedIndex = selectedImageIndex,
                onSelect = { index -> selectedImageIndex = index },
                placeholderResId = imageResId,
                contentDescription = producto.nombre
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Código: ${producto.codigo.ifBlank { "N/D" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column {
                    Text(
                        text = producto.descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isExpanded) "Leer menos" else "Leer más",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (onAddToCart(producto)) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Producto agregado al carrito")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = LevelUpHighContrastOnPrimary
                    )
                ) {
                    Text("Añadir al Carrito")
                }
                OutlinedButton(
                    onClick = onGoToCart,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text("Ver Carrito")
                }
            }
        }
    }
}

@Composable
private fun ProductImageGallery(
    images: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    placeholderResId: Int,
    contentDescription: String
) {
    val heroImage = images.getOrNull(selectedIndex)
    val heroModel = resolveProductImageModel(heroImage)
    val heroModifier = Modifier
        .fillMaxWidth()
        .height(300.dp)
        .clip(RoundedCornerShape(20.dp))

    if (heroModel != null) {
        AsyncImage(
            model = heroModel,
            contentDescription = contentDescription,
            modifier = heroModifier,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = placeholderResId),
            error = painterResource(id = placeholderResId)
        )
    } else {
        Image(
            painter = painterResource(id = placeholderResId),
            contentDescription = contentDescription,
            modifier = heroModifier,
            contentScale = ContentScale.Crop
        )
    }

    if (images.size > 1) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(images) { index, path ->
                val resolved = resolveProductImageModel(path)
                val shape = RoundedCornerShape(12.dp)
                val borderColor = if (index == selectedIndex) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
                val thumbnailModifier = Modifier
                    .size(72.dp)
                    .clip(shape)
                    .border(BorderStroke(1.dp, borderColor), shape)
                    .clickable { onSelect(index) }

                if (resolved != null) {
                    AsyncImage(
                        model = resolved,
                        contentDescription = null,
                        modifier = thumbnailModifier,
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = placeholderResId),
                        error = painterResource(id = placeholderResId)
                    )
                } else {
                    Image(
                        painter = painterResource(id = placeholderResId),
                        contentDescription = null,
                        modifier = thumbnailModifier,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

private fun buildProductGallery(producto: Producto): List<String> {
    val candidates = mutableListOf<String>()
    val primary = producto.imageUrl.trim()
    if (primary.isNotEmpty()) {
        candidates += primary
    }
    producto.gallery.forEach { raw ->
        val normalized = raw.trim()
        if (normalized.isNotEmpty() && normalized !in candidates) {
            candidates += normalized
        }
    }
    return candidates
}

private fun resolveProductImageModel(raw: String?): String? {
    val value = raw?.trim().orEmpty()
    if (value.isEmpty()) return null
    if (value.startsWith("http", ignoreCase = true)) return value
    val normalized = if (value.startsWith("/")) value else "/$value"
    return BuildConfig.API_BASE_URL.trimEnd('/') + normalized
}