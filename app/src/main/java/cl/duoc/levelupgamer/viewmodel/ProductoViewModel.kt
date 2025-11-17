package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(private val repo: ProductoRepository) : ViewModel() {
    private val _syncing = MutableStateFlow(false)
    private val _syncError = MutableStateFlow<String?>(null)

    val productos: StateFlow<List<Producto>> =
        repo.observarProductos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val isSyncing: StateFlow<Boolean> = _syncing.asStateFlow()
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    init {
        sincronizarCatalogo()
    }

    fun sincronizarCatalogo() {
        viewModelScope.launch {
            _syncing.value = true
            _syncError.value = null
            runCatching { repo.sincronizarCatalogo() }
                .onFailure { error -> _syncError.value = error.message }
            _syncing.value = false
        }
    }
}
