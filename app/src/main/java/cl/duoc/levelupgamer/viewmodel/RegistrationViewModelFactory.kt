package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.levelupgamer.model.repository.UsuarioRepository

class RegistrationViewModelFactory(
    private val usuarioRepository: UsuarioRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegistrationViewModel(usuarioRepository) as T
    }
}