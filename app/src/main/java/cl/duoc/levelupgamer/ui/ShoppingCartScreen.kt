package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.util.formatCurrency
import cl.duoc.levelupgamer.viewmodel.CheckoutUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(
    items: List<CarritoItemEntity>,
    productos: List<Producto>,
    initialAddress: String?,
    checkoutState: CheckoutUiState,
    onBack: () -> Unit,
    onChangeQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onCheckout: (String, String?) -> Unit,
    onCheckoutStateConsumed: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    onGoToPayment: () -> Unit
) {
    val lineItems = remember(items, productos) {
        items.mapNotNull { item ->
            val producto = productos.firstOrNull { it.id == item.productoId }
            producto?.let { CartLineItem(it, item) }
        }
    }
    val totalPrice = remember(lineItems) { lineItems.sumOf { it.producto.precio * it.item.cantidad } }
    val totalUnits = remember(lineItems) { lineItems.sumOf { it.item.cantidad } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (lineItems.isEmpty()) {
                EmptyCartState()
            } else {
                val constraintsMaxWidth = maxWidth
                val isWideLayout = constraintsMaxWidth >= 720.dp
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    // Use SpaceBetween so the summary stays pinned to the bottom
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header removed to avoid duplicate "Resumen" — keep only bottom summary with payment button
                    if (isWideLayout) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CartItemsSection(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                lineItems = lineItems,
                                onChangeQuantity = onChangeQuantity,
                                onRemoveItem = onRemoveItem
                            )
                            CheckoutSummaryPreview(
                                modifier = Modifier
                                    .widthIn(max = constraintsMaxWidth * 0.4f)
                                    .fillMaxHeight(),
                                totalPrice = totalPrice,
                                totalUnits = totalUnits,
                                onGoToPayment = onGoToPayment
                            )
                        }
                    } else {
                        CartItemsSection(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            lineItems = lineItems,
                            onChangeQuantity = onChangeQuantity,
                            onRemoveItem = onRemoveItem
                        )
                        CheckoutSummaryPreview(
                            modifier = Modifier.fillMaxWidth(),
                            totalPrice = totalPrice,
                            totalUnits = totalUnits,
                            onGoToPayment = onGoToPayment
                        )
                    }
                }
            }
        }
    }
}

private data class CartLineItem(val producto: Producto, val item: CarritoItemEntity)

@Composable
private fun EmptyCartState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Tu carrito está vacío",
                style = MaterialTheme.typography.headlineSmall,
                fontStyle = FontStyle.Italic
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explora el catálogo para comenzar una compra",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Top cart summary removed; bottom `CheckoutSummaryPreview` remains as the single cart summary.

@Composable
private fun CartItemsSection(
    modifier: Modifier = Modifier,
    lineItems: List<CartLineItem>,
    onChangeQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit
) {
    Card(
        // Ensure the items section fills available vertical space when a weight is provided
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(lineItems, key = { it.item.id }) { lineItem ->
                CartItemCard(
                    producto = lineItem.producto,
                    quantity = lineItem.item.cantidad,
                    canDecrease = lineItem.item.cantidad > 1,
                    onDecrease = { onChangeQuantity(lineItem.item.id, lineItem.item.cantidad - 1) },
                    onIncrease = { onChangeQuantity(lineItem.item.id, lineItem.item.cantidad + 1) },
                    onRemove = { onRemoveItem(lineItem.item.id) }
                )
            }
        }
    }
}

@Composable
private fun CheckoutSummaryPreview(
    modifier: Modifier = Modifier,
    totalPrice: Double,
    totalUnits: Int,
    onGoToPayment: () -> Unit
) {
    val formattedTotal = remember(totalPrice) { formatCurrency(totalPrice) }
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                text = "$totalUnits ${if (totalUnits == 1) "artículo" else "artículos"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total estimado", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = formattedTotal,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text("Impuestos incluidos", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onGoToPayment,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Continuar al pago")
            }
        }
    }
}
