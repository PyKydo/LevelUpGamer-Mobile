package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.levelupgamer.model.local.AppDatabase
import cl.duoc.levelupgamer.model.repository.CarritoRepository

class CarritoViewModelFactory(private val db: AppDatabase, private val usuarioId: Long) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repo = CarritoRepository(db.carritoItemDao())
        return CarritoViewModel(repo, usuarioId) as T
    }
}