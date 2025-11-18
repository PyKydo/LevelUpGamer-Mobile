package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import cl.duoc.levelupgamer.model.repository.PedidoRepository
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import cl.duoc.levelupgamer.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import cl.duoc.levelupgamer.util.NetworkErrorMapper

class CarritoViewModel(
    private val repo: CarritoRepository,
    private val pedidoRepository: PedidoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val usuarioId: Long
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    private val _checkoutState = MutableStateFlow(CheckoutUiState())

    val items: StateFlow<List<CarritoItemEntity>> =
        repo.observarCarrito(usuarioId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val error: StateFlow<String?> = _error.asStateFlow()
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState.asStateFlow()

    fun agregar(productoId: Long, cantidad: Int = 1) = launchCartOperation {
        repo.agregar(usuarioId, productoId, cantidad)
    }

    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int) = launchCartOperation {
        repo.actualizarCantidad(usuarioId, itemId, nuevaCantidad)
    }

    fun eliminar(itemId: Long) = launchCartOperation {
        repo.eliminarItem(usuarioId, itemId)
    }

    fun limpiar() = launchCartOperation { repo.limpiarCarrito(usuarioId) }

    fun realizarCheckout(direccion: String, notas: String?) {
        viewModelScope.launch {
            if (_checkoutState.value.isProcessing) return@launch
            _checkoutState.value = CheckoutUiState(isProcessing = true)
            runCatching {
                val currentItems = items.value
                val pedido = pedidoRepository.crearPedido(
                    userId = usuarioId,
                    items = currentItems,
                    direccionEnvio = direccion.trim(),
                    notas = notas?.takeIf { it.isNotBlank() }
                )
                repo.limpiarCarrito(usuarioId)
                pedido
            }.onSuccess { pedido ->
                runCatching { usuarioRepository.refreshPerfil() }
                _checkoutState.value = CheckoutUiState(pedidoConfirmado = pedido)
            }.onFailure { throwable ->
                _checkoutState.value = CheckoutUiState(
                    error = cl.duoc.levelupgamer.util.NetworkErrorMapper.map(throwable)
                )
            }
        }
    }

    fun consumirResultadoCheckout() {
        _checkoutState.value = CheckoutUiState()
    }

    private fun launchCartOperation(block: suspend () -> Unit) {
        viewModelScope.launch {
            _error.value = null
            try {
                block()
            } catch (t: Throwable) {
                _error.value = NetworkErrorMapper.map(t)
            }
        }
    }
}

data class CheckoutUiState(
    val isProcessing: Boolean = false,
    val pedidoConfirmado: Pedido? = null,
    val error: String? = null
)
