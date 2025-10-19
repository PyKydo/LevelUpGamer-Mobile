package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.local.CarritoItemEntity
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarritoViewModel(
    private val repo: CarritoRepository,
    private val usuarioId: Long
) : ViewModel() {

    val items: StateFlow<List<CarritoItemEntity>> =
        repo.observarCarrito(usuarioId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun agregar(productoId: Long, incremento: Int = 1) = viewModelScope.launch {
        repo.agregarOIncrementar(usuarioId, productoId, incremento)
    }

    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int) = viewModelScope.launch {
        repo.actualizarCantidad(itemId, nuevaCantidad)
    }

    fun eliminar(itemId: Long) = viewModelScope.launch { repo.eliminar(itemId) }

    fun limpiar() = viewModelScope.launch { repo.limpiarCarrito(usuarioId) }
}
