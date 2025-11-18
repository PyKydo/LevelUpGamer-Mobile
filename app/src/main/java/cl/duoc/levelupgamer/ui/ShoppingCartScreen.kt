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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.viewmodel.CheckoutUiState
import cl.duoc.levelupgamer.service.NotificationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    val totalPrice = lineItems.sumOf { it.producto.precio * it.item.cantidad }
    val totalUnits = lineItems.sumOf { it.item.cantidad }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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

                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CartInformationHeader(totalUnits = totalUnits, totalPrice = totalPrice)
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
                            Column(
                                modifier = Modifier
                                    .widthIn(max = constraintsMaxWidth * 0.45f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Button(
                                    onClick = { onGoToPayment() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Ir a Pago", color = MaterialTheme.colorScheme.onPrimary)
                                }
                            }
                        }
                    } else {
                        CartItemsSection(
                            modifier = Modifier.weight(1f),
                            lineItems = lineItems,
                            onChangeQuantity = onChangeQuantity,
                            onRemoveItem = onRemoveItem
                        )
                        Button(
                            onClick = { onGoToPayment() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Ir a Pago", color = MaterialTheme.colorScheme.onPrimary)
                        }
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

@Composable
private fun CartInformationHeader(totalUnits: Int, totalPrice: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Resumen del carrito", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "$totalUnits ${if (totalUnits == 1) "artículo" else "artículos"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$${String.format("%.2f", totalPrice)}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun CartItemsSection(
    modifier: Modifier = Modifier,
    lineItems: List<CartLineItem>,
    onChangeQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
private fun CheckoutSummaryCard(
    modifier: Modifier = Modifier,
    totalPrice: Double,
    direccionEnvio: String,
    direccionError: String?,
    notas: String,
    isProcessing: Boolean,
    onAddressChanged: (String) -> Unit,
    onNotesChanged: (String) -> Unit,
    onCheckout: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Información de entrega", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = direccionEnvio,
                onValueChange = onAddressChanged,
                label = { Text("Dirección de envío") },
                modifier = Modifier.fillMaxWidth(),
                isError = direccionError != null,
                supportingText = {
                    direccionError?.let { error ->
                        Text(text = error, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            OutlinedTextField(
                value = notas,
                onValueChange = onNotesChanged,
                label = { Text("Notas adicionales (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total a pagar", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = "$${String.format("%.2f", totalPrice)}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Button(
                onClick = onCheckout,
                enabled = !isProcessing,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSecondary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text("Finalizar Compra", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

private fun validateAndCheckout(
    direccionEnvio: String,
    notas: String,
    setError: (String?) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    onCheckout: (String, String?) -> Unit
) {
    setError(null)
    val trimmedAddress = direccionEnvio.trim()
    if (trimmedAddress.isBlank()) {
        setError("Ingresa una dirección de envío")
        scope.launch {
            snackbarHostState.showSnackbar("La dirección de envío es obligatoria")
        }
    } else {
        onCheckout(trimmedAddress, notas.ifBlank { null })
    }
}
