package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.Producto
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.repository.ProductoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProductoViewModel(private val repo: ProductoRepository) : ViewModel() {
    val productos: StateFlow<List<Producto>> =
        repo.observarProductos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )
}

class ProductoViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = ProductoRepository(db.productoDao())
        return ProductoViewModel(repo) as T
    }
}
