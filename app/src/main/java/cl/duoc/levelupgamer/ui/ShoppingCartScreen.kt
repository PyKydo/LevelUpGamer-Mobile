package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.toProductoSnapshot
import cl.duoc.levelupgamer.ui.components.CartItemCard
import cl.duoc.levelupgamer.ui.components.EmptyState
import cl.duoc.levelupgamer.ui.components.LevelUpHighContrastOnPrimary
import cl.duoc.levelupgamer.util.formatCurrency
import cl.duoc.levelupgamer.viewmodel.CheckoutUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(
    items: List<CarritoItemEntity>,
    initialAddress: String?,
    checkoutState: CheckoutUiState,
    readOnly: Boolean,
    errorMessage: String?,
    onChangeQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onCheckout: (Double, String, String?) -> Unit,
    onCheckoutStateConsumed: () -> Unit,
    onCheckoutSuccess: () -> Unit,
    onGoToPayment: () -> Unit,
    onExploreCatalog: () -> Unit,
    onRetrySync: () -> Unit
) {
    val lineItems = remember(items) {
        items.map { item -> CartLineItem(item.toProductoSnapshot(), item) }
    }
    val totalPrice = remember(items) { items.sumOf { it.unitPrice * it.cantidad } }
    val totalUnits = remember(items) { items.sumOf { it.cantidad } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Filled.ShoppingCart,
                        title = "Tu carrito está vacío",
                        subtitle = "Explora el catálogo y agrega tus favoritos."
                    ) {
                        Button(
                            onClick = onExploreCatalog,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = LevelUpHighContrastOnPrimary
                            )
                        ) {
                            Text("Ver catálogo")
                        }
                    }
                }
            } else {
                val constraintsMaxWidth = maxWidth
                val isWideLayout = constraintsMaxWidth >= 720.dp
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CartStatusBanner(
                        readOnly = readOnly,
                        errorMessage = errorMessage,
                        onRetry = onRetrySync
                    )
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
                                enabled = !readOnly,
                                onChangeQuantity = onChangeQuantity,
                                onRemoveItem = onRemoveItem
                            )
                            CheckoutSummaryPreview(
                                modifier = Modifier
                                    .widthIn(max = constraintsMaxWidth * 0.4f)
                                    .fillMaxHeight(),
                                totalPrice = totalPrice,
                                totalUnits = totalUnits,
                                onGoToPayment = onGoToPayment,
                                enabled = !readOnly,
                                disabledMessage = if (readOnly) "Sin conexión. Recupera la señal para continuar con el pago." else null
                            )
                        }
                    } else {
                        CartItemsSection(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            lineItems = lineItems,
                            enabled = !readOnly,
                            onChangeQuantity = onChangeQuantity,
                            onRemoveItem = onRemoveItem
                        )
                        CheckoutSummaryPreview(
                            modifier = Modifier.fillMaxWidth(),
                            totalPrice = totalPrice,
                            totalUnits = totalUnits,
                            onGoToPayment = onGoToPayment,
                            enabled = !readOnly,
                            disabledMessage = if (readOnly) "Recupera tu conexión para finalizar la compra." else null
                        )
                    }
                }
            }
        }
    }
}

private data class CartLineItem(val producto: Producto, val item: CarritoItemEntity)

@Composable
private fun CartItemsSection(
    modifier: Modifier = Modifier,
    lineItems: List<CartLineItem>,
    enabled: Boolean,
    onChangeQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit
) {
    Card(

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
                    onRemove = { onRemoveItem(lineItem.item.id) },
                    enabled = enabled
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
    onGoToPayment: () -> Unit,
    enabled: Boolean,
    disabledMessage: String?
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
                enabled = enabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = LevelUpHighContrastOnPrimary
                )
            ) {
                Text("Continuar al pago")
            }
            if (!enabled && !disabledMessage.isNullOrBlank()) {
                Text(
                    text = disabledMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CartStatusBanner(
    readOnly: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    if (!readOnly && errorMessage.isNullOrBlank()) return

    val (icon, title, body) = when {
        readOnly && !errorMessage.isNullOrBlank() -> Triple(
            Icons.Filled.WifiOff,
            "Modo sin conexión",
            errorMessage
        )
        readOnly -> Triple(
            Icons.Filled.WifiOff,
            "Modo sin conexión",
            "No pudimos sincronizar el carrito. Puedes revisarlo, pero no modificarlo hasta recuperar la conexión."
        )
        else -> Triple(
            Icons.Filled.Refresh,
            "No pudimos actualizar el carrito",
            errorMessage ?: "Intenta nuevamente."
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onErrorContainer)
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            OutlinedButton(onClick = onRetry) {
                Text("Reintentar")
            }
        }
    }
}
