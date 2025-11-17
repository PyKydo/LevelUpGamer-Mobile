package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.levelupgamer.model.repository.CarritoRepository
import cl.duoc.levelupgamer.model.repository.PedidoRepository
import cl.duoc.levelupgamer.model.repository.UsuarioRepository

class CarritoViewModelFactory(
    private val repo: CarritoRepository,
    private val pedidoRepository: PedidoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val usuarioId: Long
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CarritoViewModel(repo, pedidoRepository, usuarioRepository, usuarioId) as T
    }
}