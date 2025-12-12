package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.local.toProductoSnapshot
import cl.duoc.levelupgamer.service.NotificationService
import cl.duoc.levelupgamer.ui.components.FormTextField
import cl.duoc.levelupgamer.ui.components.GlowCard
import cl.duoc.levelupgamer.ui.components.PrimaryActionButton
import cl.duoc.levelupgamer.ui.components.SectionHeader
import cl.duoc.levelupgamer.ui.theme.LocalLevelUpSpacing
import cl.duoc.levelupgamer.util.formatCurrency
import cl.duoc.levelupgamer.viewmodel.CheckoutUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    items: List<CarritoItemEntity>,
    initialAddress: String?,
    checkoutState: CheckoutUiState,
    onBack: () -> Unit,
    onCheckout: (Double, String, String?) -> Unit,
    onCheckoutStateConsumed: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val lineItems = remember(items) { items.map { PaymentCartLineItem(it.toProductoSnapshot(), it) } }
    val totalPrice = remember(items) { items.sumOf { it.unitPrice * it.cantidad } }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var direccionEnvio by rememberSaveable(initialAddress) { mutableStateOf(initialAddress.orEmpty()) }
    var notas by rememberSaveable { mutableStateOf("") }
    var direccionError by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val formattedTotal = remember(totalPrice) { formatCurrency(totalPrice) }
    val spacing = LocalLevelUpSpacing.current
    val buttonLabel = if (checkoutState.isProcessing) "Procesando pago..." else "Realizar pago (simulado)"
    val canCheckout = lineItems.isNotEmpty()

    LaunchedEffect(checkoutState.pedidoConfirmado) {
        checkoutState.pedidoConfirmado?.let { pedido ->
            NotificationService(context).mostrarNotificacionCompraExitosa(pedido.id)
            snackbarHostState.showSnackbar("Pago realizado exitosamente (Pedido #${pedido.id})")
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
                title = { Text("Pago y Confirmación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.mdDp, vertical = spacing.smDp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(spacing.lgDp)
        ) {
            SectionHeader(title = "Completa tu pedido")
            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.smDp)) {
                    Text(
                        text = "Información de entrega",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    FormTextField(
                        value = direccionEnvio,
                        onValueChange = {
                            direccionEnvio = it
                            if (direccionError != null) direccionError = null
                        },
                        label = "Dirección de envío",
                        modifier = Modifier.fillMaxWidth(),
                        supportingText = direccionError,
                        isError = direccionError != null
                    )
                    FormTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = "Notas adicionales (opcional)",
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                    Text(
                        text = "Usaremos esta información solo para preparar la entrega de tu pedido simulado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            GlowCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.smDp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Resumen del pedido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formattedTotal,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (lineItems.isEmpty()) {
                        Text(
                            text = "Tu carrito está vacío",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(spacing.xsDp)) {
                            lineItems.forEach { lineItem ->
                                PaymentLineItemRow(lineItem = lineItem)
                            }
                        }
                    }
                }
            }

            PrimaryActionButton(
                text = buttonLabel,
                modifier = Modifier.fillMaxWidth(),
                enabled = canCheckout && !checkoutState.isProcessing,
                loading = checkoutState.isProcessing,
                onClick = {
                    val trimmed = direccionEnvio.trim()
                    if (trimmed.isBlank()) {
                        direccionError = "Ingresa una dirección de envío"
                        scope.launch { snackbarHostState.showSnackbar("La dirección de envío es obligatoria") }
                    } else {
                        onCheckout(totalPrice, trimmed, notas.ifBlank { null })
                    }
                }
            )
        }
    }
}

private data class PaymentCartLineItem(val producto: Producto, val item: CarritoItemEntity)

@Composable
private fun PaymentLineItemRow(lineItem: PaymentCartLineItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lineItem.producto.nombre,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "x${lineItem.item.cantidad}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = formatCurrency(lineItem.item.unitPrice * lineItem.item.cantidad),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
