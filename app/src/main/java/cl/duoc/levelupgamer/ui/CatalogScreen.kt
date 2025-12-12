package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import cl.duoc.levelupgamer.ui.theme.spacing
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.ui.components.ProductCard
import cl.duoc.levelupgamer.ui.components.EmptyState
import cl.duoc.levelupgamer.ui.components.ShimmerProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    products: List<Producto>,
    isLoading: Boolean = false,
    onProductClick: (Producto) -> Unit,
    onAddToCart: (Producto) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catálogo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) {
        val contentModifier = Modifier
            .fillMaxSize()
            .padding(it)
        when {
            isLoading && products.isEmpty() -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = contentModifier,
                    contentPadding = PaddingValues(MaterialTheme.spacing.md.dp),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md.dp),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CatalogIntro(productCount = 0)
                    }
                    items(6) {
                        ShimmerProductCard()
                    }
                }
            }
            products.isEmpty() -> {
                Box(
                    modifier = contentModifier,
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Outlined.ShoppingCart,
                        title = "Sin productos disponibles",
                        subtitle = "Intenta actualizar el catálogo o vuelve más tarde."
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = contentModifier,
                    contentPadding = PaddingValues(MaterialTheme.spacing.md.dp),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md.dp),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CatalogIntro(productCount = products.size)
                    }
                    items(products, key = { it.id }) { product ->
                        ProductCard(
                            producto = product,
                            onClick = { onProductClick(product) },
                            onAddClick = onAddToCart
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CatalogIntro(productCount: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = MaterialTheme.spacing.sm.dp)
    ) {
        Text("Explora LevelUp Gamer", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "$productCount ${if (productCount == 1) "producto" else "productos"} disponibles",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}