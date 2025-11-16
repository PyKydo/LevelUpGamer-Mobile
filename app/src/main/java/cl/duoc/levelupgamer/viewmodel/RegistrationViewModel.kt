package cl.duoc.levelupgamer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.levelupgamer.model.repository.InAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

data class RegistrationFormState(
    val nombre: String = "",
    val email: String = "",
    val contrasena: String = "",
    val contrasenaConfirm: String = "",
    val fechaNacimiento: String = "",

    val nombreError: String? = null,
    val emailError: String? = null,
    val contrasenaError: String? = null,
    val contrasenaConfirmError: String? = null,
    val fechaNacimientoError: String? = null,

    val error: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
)

class RegistrationViewModel(private val authRepository: InAuthRepository) : ViewModel() {

    private val _form = MutableStateFlow(RegistrationFormState())
    val form: StateFlow<RegistrationFormState> = _form.asStateFlow()

    fun onChangeNombre(v: String) =
        _form.update { it.copy(nombre = v, nombreError = null, error = null) }

    fun onChangeEmail(v: String) =
        _form.update { it.copy(email = v, emailError = null, error = null) }

    fun onChangeContrasena(v: String) =
        _form.update { it.copy(contrasena = v, contrasenaError = null, error = null) }

    fun onChangeContrasenaConfirm(v: String) =
        _form.update { it.copy(contrasenaConfirm = v, contrasenaConfirmError = null, error = null) }

    fun onChangeFechaNacimiento(v: String) =
        _form.update { it.copy(fechaNacimiento = v, fechaNacimientoError = null, error = null) }

    fun limpiarFormulario() {
        _form.value = RegistrationFormState()
    }

    fun registrar() = viewModelScope.launch {
        val current = _form.value
        val nombre = current.nombre.trim()
        val email = current.email.trim()
        val contrasena = current.contrasena
        val contrasenaConfirm = current.contrasenaConfirm
        val fechaNacimiento = current.fechaNacimiento.trim()

        var hasError = false
        var firstError: String? = null

        if (nombre.isBlank()) {
            hasError = true
            val msg = "El nombre no puede estar vacío"
            _form.update { it.copy(nombreError = msg) }
            if (firstError == null) firstError = msg
        }

        if (email.isBlank()) {
            hasError = true
            val msg = "El email no puede estar vacío"
            _form.update { it.copy(emailError = msg) }
            if (firstError == null) firstError = msg
        } else if (!validarEmail(email)) {
            hasError = true
            val msg = "El email no es válido"
            _form.update { it.copy(emailError = msg) }
            if (firstError == null) firstError = msg
        }

        if (contrasena.isBlank()) {
            hasError = true
            val msg = "La contraseña no puede estar vacía"
            _form.update { it.copy(contrasenaError = msg) }
            if (firstError == null) firstError = msg
        } else if (!validarContrasena(contrasena)) {
            hasError = true
            val msg = "La contraseña debe tener 8+ caracteres, mayúscula, minúscula, dígito y símbolo."
            _form.update { it.copy(contrasenaError = msg) }
            if (firstError == null) firstError = msg
        }

        // Validación de confirmación de contraseña
        if (contrasenaConfirm.isBlank()) {
            hasError = true
            val msg = "Debes confirmar la contraseña"
            _form.update { it.copy(contrasenaConfirmError = msg) }
            if (firstError == null) firstError = msg
        } else if (contrasena != contrasenaConfirm) {
            hasError = true
            val msg = "Las contraseñas no coinciden"
            _form.update { it.copy(contrasenaConfirmError = msg) }
            if (firstError == null) firstError = msg
        }

        val dob = if (fechaNacimiento.isBlank()) null else parseDate(fechaNacimiento)
        if (fechaNacimiento.isBlank()) {
            hasError = true
            val msg = "La fecha de nacimiento no puede estar vacía"
            _form.update { it.copy(fechaNacimientoError = msg) }
            if (firstError == null) firstError = msg
        } else if (dob == null) {
            hasError = true
            val msg = "Usa formato dd/MM/yyyy."
            _form.update { it.copy(fechaNacimientoError = msg) }
            if (firstError == null) firstError = msg
        } else if (!esMayorDeEdad(dob)) {
            hasError = true
            val msg = "Debes tener al menos 18 años."
            _form.update { it.copy(fechaNacimientoError = msg) }
            if (firstError == null) firstError = msg
        }

        if (hasError) {
            _form.update { it.copy(error = firstError) }
            return@launch
        }

        _form.update { it.copy(isLoading = true, error = null) }

        try {
            authRepository.registrar(
                nombre = nombre,
                email = email,
                contrasena = contrasena,
                fechaNacimiento = fechaNacimiento
            )
            _form.update { it.copy(isLoading = false, isSuccess = true) }
        } catch (t: Throwable) {
            _form.update { it.copy(isLoading = false, error = t.message ?: "Error inesperado al registrar") }
        }
    }

    private fun validarEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE)
        return pattern.matcher(email).matches()
    }

    private fun esMayorDeEdad(dob: LocalDate): Boolean {
        val edad = Period.between(dob, LocalDate.now()).years
        return edad >= 18
    }

    private fun parseDate(s: String): LocalDate? = try {
        val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        LocalDate.parse(s, fmt)
    } catch (_: DateTimeParseException) {
        null
    }

    private fun validarContrasena(contrasena: String): Boolean {
        val pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$") // al menos debe tener 8 caracteres, 1 minúscula, 1 mayúscula, 1 dígito y 1 símbolo
        return pattern.matcher(contrasena).matches()
    }
}