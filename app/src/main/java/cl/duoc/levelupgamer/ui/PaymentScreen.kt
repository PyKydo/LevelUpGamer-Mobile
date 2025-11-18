package cl.duoc.levelupgamer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.service.NotificationService
import cl.duoc.levelupgamer.viewmodel.CheckoutUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    items: List<CarritoItemEntity>,
    productos: List<Producto>,
    initialAddress: String?,
    checkoutState: CheckoutUiState,
    onBack: () -> Unit,
    onCheckout: (String, String?) -> Unit,
    onCheckoutStateConsumed: () -> Unit,
    onCheckoutSuccess: () -> Unit
) {
    val lineItems = remember(items, productos) {
        items.mapNotNull { item ->
            val producto = productos.firstOrNull { it.id == item.productoId }
                producto?.let { PaymentCartLineItem(it, item) }
        }
    }
    val totalPrice = lineItems.sumOf { it.producto.precio * it.item.cantidad }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var direccionEnvio by rememberSaveable(initialAddress) { mutableStateOf(initialAddress.orEmpty()) }
    var notas by rememberSaveable { mutableStateOf("") }
    var direccionError by remember { mutableStateOf<String?>(null) }

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
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Información de entrega", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = direccionEnvio,
                    onValueChange = { direccionEnvio = it; if (direccionError != null) direccionError = null },
                    label = { Text("Dirección de envío") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = direccionError != null,
                    supportingText = {
                        direccionError?.let { error -> Text(text = error, color = MaterialTheme.colorScheme.error) }
                    }
                )

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas adicionales (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Resumen del pedido", style = MaterialTheme.typography.titleMedium)
                Text("Total a pagar: $${String.format("%.2f", totalPrice)}", style = MaterialTheme.typography.headlineSmall)

                Button(
                    onClick = {
                        val trimmed = direccionEnvio.trim()
                        if (trimmed.isBlank()) {
                            direccionError = "Ingresa una dirección de envío"
                            scope.launch { snackbarHostState.showSnackbar("La dirección de envío es obligatoria") }
                        } else {
                            onCheckout(trimmed, notas.ifBlank { null })
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (checkoutState.isProcessing) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp, modifier = Modifier.height(18.dp))
                    } else {
                        Text("Realizar pago (simulado)", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}

private data class PaymentCartLineItem(val producto: Producto, val item: CarritoItemEntity)
