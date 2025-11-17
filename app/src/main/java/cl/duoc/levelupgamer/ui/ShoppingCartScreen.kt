package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.viewmodel.CheckoutUiState
import cl.duoc.levelupgamer.service.NotificationService
import kotlinx.coroutines.delay
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
    onCheckoutSuccess: () -> Unit
) {
    val lineItems = remember(items, productos) {
        items.mapNotNull { item ->
            val producto = productos.firstOrNull { it.id == item.productoId }
            producto?.let { CartLineItem(it, item) }
        }
    }
    val totalPrice = lineItems.sumOf { it.producto.precio * it.item.cantidad }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var direccionEnvio by remember(initialAddress) { mutableStateOf(initialAddress.orEmpty()) }
    var notas by remember { mutableStateOf("") }
    var direccionError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(checkoutState.pedidoConfirmado) {
        checkoutState.pedidoConfirmado?.let { pedido ->
            NotificationService(context).mostrarNotificacionCompraExitosa()
            snackbarHostState.showSnackbar("Pedido #${pedido.id} confirmado")
            notas = ""
            onCheckoutStateConsumed()
            delay(1_000)
            onCheckoutSuccess()
        }
    }

    LaunchedEffect(checkoutState.error) {
        checkoutState.error?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            onCheckoutStateConsumed()
        }
    }

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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (lineItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tu carrito está vacío",
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
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
                if (lineItems.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = direccionEnvio,
                        onValueChange = {
                            direccionEnvio = it
                            if (direccionError != null) direccionError = null
                        },
                        label = { Text("Dirección de envío") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        isError = direccionError != null,
                        supportingText = {
                            direccionError?.let { error ->
                                Text(text = error, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Notas adicionales (opcional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        minLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(
                                "$${String.format("%.2f", totalPrice)}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                direccionError = null
                                val trimmedAddress = direccionEnvio.trim()
                                if (trimmedAddress.isBlank()) {
                                    direccionError = "Ingresa una dirección de envío"
                                    scope.launch {
                                        snackbarHostState.showSnackbar("La dirección de envío es obligatoria")
                                    }
                                } else {
                                    onCheckout(trimmedAddress, notas.ifBlank { null })
                                }
                            },
                            enabled = lineItems.isNotEmpty() && !checkoutState.isProcessing,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            if (checkoutState.isProcessing) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text("Finalizar Compra", color = MaterialTheme.colorScheme.onSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class CartLineItem(val producto: Producto, val item: CarritoItemEntity)
