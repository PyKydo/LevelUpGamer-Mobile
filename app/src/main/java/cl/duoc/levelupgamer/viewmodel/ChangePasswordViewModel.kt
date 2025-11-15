package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val usuarioRepository: UsuarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState = _uiState.asStateFlow()

    fun onCurrentPasswordChange(password: String) {
        _uiState.update { it.copy(currentPassword = password) }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password) }
    }

    fun changePassword() {
        if (!validateInput()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }
            try {
                usuarioRepository.changePassword(
                    currentPassword = _uiState.value.currentPassword,
                    newPassword = _uiState.value.newPassword
                )
                _uiState.update { it.copy(isLoading = false, success = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun validateInput(): Boolean {
        val state = _uiState.value
        if (state.currentPassword.isBlank() || state.newPassword.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return false
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(error = "Las contraseñas nuevas no coinciden") }
            return false
        }
        return true
    }
}

data class ChangePasswordUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

class ChangePasswordViewModelFactory(private val usuarioRepository: UsuarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChangePasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChangePasswordViewModel(usuarioRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}