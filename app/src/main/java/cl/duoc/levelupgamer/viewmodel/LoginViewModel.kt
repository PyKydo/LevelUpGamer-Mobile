package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.repository.InAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginFormState(
    val email: String = "",
    val contrasena: String = "",

    val emailError: String? = null,
    val contrasenaError: String? = null,

    val error: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
)

class LoginViewModel(private val authRepository: InAuthRepository) : ViewModel() {

    private val _form = MutableStateFlow(LoginFormState())
    val form: StateFlow<LoginFormState> = _form.asStateFlow()

    fun onChangeEmail(v: String) =
        _form.update { it.copy(email = v, emailError = null, error = null) }

    fun onChangeContrasena(v: String) =
        _form.update { it.copy(contrasena = v, contrasenaError = null, error = null) }

    fun limpiarFormulario() {
        _form.value = LoginFormState()
    }

    fun iniciarSesion() = viewModelScope.launch {
        val current = _form.value
        val email = current.email.trim()
        val contrasena = current.contrasena

        var hasError = false

        if (!validarEmail(email)) {
            hasError = true
            _form.update { it.copy(emailError = "El email no es válido", error = "El email no es válido") }
        }

        if (contrasena.isBlank()) {
            hasError = true
            _form.update { it.copy(contrasenaError = "Ingresa tu contraseña") }
        }

        if (hasError) return@launch

        _form.update { it.copy(isLoading = true, error = null) }

        try {
            authRepository.iniciarSesion(email = email, contrasena = contrasena)
            _form.update { it.copy(isLoading = false, isSuccess = true) }
        } catch (t: Throwable) {
            _form.update { it.copy(isLoading = false, error = t.message ?: "No se pudo iniciar sesión") }
        }
    }

    private fun validarEmail(email: String): Boolean {
        val regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$".toRegex(setOf(RegexOption.IGNORE_CASE))
        return email.matches(regex)
    }
}
