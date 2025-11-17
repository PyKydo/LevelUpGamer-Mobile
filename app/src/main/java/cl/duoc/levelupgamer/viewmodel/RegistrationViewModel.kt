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
    val apellidos: String = "",
    val run: String = "",
    val email: String = "",
    val contrasena: String = "",
    val contrasenaConfirm: String = "",
    val fechaNacimiento: String = "",
    val region: String = "",
    val comuna: String = "",
    val direccion: String = "",
    val codigoReferido: String = "",
    val aceptaTerminos: Boolean = false,

    val nombreError: String? = null,
    val apellidosError: String? = null,
    val runError: String? = null,
    val emailError: String? = null,
    val contrasenaError: String? = null,
    val contrasenaConfirmError: String? = null,
    val fechaNacimientoError: String? = null,
    val regionError: String? = null,
    val comunaError: String? = null,
    val direccionError: String? = null,
    val aceptaTerminosError: String? = null,

    val error: String? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
)

class RegistrationViewModel(private val authRepository: InAuthRepository) : ViewModel() {

    private val _form = MutableStateFlow(RegistrationFormState())
    val form: StateFlow<RegistrationFormState> = _form.asStateFlow()

    fun onChangeNombre(v: String) =
        _form.update { it.copy(nombre = v, nombreError = null, error = null) }

    fun onChangeApellidos(v: String) =
        _form.update { it.copy(apellidos = v, apellidosError = null, error = null) }

    fun onChangeRun(v: String) =
        _form.update { it.copy(run = v, runError = null, error = null) }

    fun onChangeEmail(v: String) =
        _form.update { it.copy(email = v, emailError = null, error = null) }

    fun onChangeContrasena(v: String) =
        _form.update { it.copy(contrasena = v, contrasenaError = null, error = null) }

    fun onChangeContrasenaConfirm(v: String) =
        _form.update { it.copy(contrasenaConfirm = v, contrasenaConfirmError = null, error = null) }

    fun onChangeFechaNacimiento(v: String) =
        _form.update { it.copy(fechaNacimiento = v, fechaNacimientoError = null, error = null) }

    fun onChangeRegion(v: String) =
        _form.update { it.copy(region = v, regionError = null, error = null) }

    fun onChangeComuna(v: String) =
        _form.update { it.copy(comuna = v, comunaError = null, error = null) }

    fun onChangeDireccion(v: String) =
        _form.update { it.copy(direccion = v, direccionError = null, error = null) }

    fun onChangeCodigoReferido(v: String) =
        _form.update { it.copy(codigoReferido = v, error = null) }

    fun onChangeAceptaTerminos(checked: Boolean) =
        _form.update { it.copy(aceptaTerminos = checked, aceptaTerminosError = null, error = null) }

    fun limpiarFormulario() {
        _form.value = RegistrationFormState()
    }

    fun registrar() = viewModelScope.launch {
        val current = _form.value
        val nombre = current.nombre.trim()
        val apellidos = current.apellidos.trim()
        val run = current.run.trim()
        val email = current.email.trim()
        val contrasena = current.contrasena
        val contrasenaConfirm = current.contrasenaConfirm
        val fechaNacimientoTexto = current.fechaNacimiento.trim()
        val region = current.region.trim()
        val comuna = current.comuna.trim()
        val direccion = current.direccion.trim()
        val codigoReferido = current.codigoReferido.trim().ifBlank { null }
        val aceptaTerminos = current.aceptaTerminos

        var hasError = false
        var firstError: String? = null

        if (nombre.isBlank()) {
            hasError = true
            val msg = "El nombre no puede estar vacío"
            _form.update { it.copy(nombreError = msg) }
            firstError = firstError ?: msg
        }

        if (apellidos.isBlank()) {
            hasError = true
            val msg = "Los apellidos no pueden estar vacíos"
            _form.update { it.copy(apellidosError = msg) }
            firstError = firstError ?: msg
        }

        if (run.isBlank()) {
            hasError = true
            val msg = "El RUN es obligatorio"
            _form.update { it.copy(runError = msg) }
            firstError = firstError ?: msg
        } else if (!validarRun(run)) {
            hasError = true
            val msg = "RUN inválido"
            _form.update { it.copy(runError = msg) }
            firstError = firstError ?: msg
        }

        if (email.isBlank()) {
            hasError = true
            val msg = "El email no puede estar vacío"
            _form.update { it.copy(emailError = msg) }
            firstError = firstError ?: msg
        } else if (!validarEmail(email)) {
            hasError = true
            val msg = "El email no es válido"
            _form.update { it.copy(emailError = msg) }
            firstError = firstError ?: msg
        }

        if (contrasena.isBlank()) {
            hasError = true
            val msg = "La contraseña no puede estar vacía"
            _form.update { it.copy(contrasenaError = msg) }
            firstError = firstError ?: msg
        } else if (!validarContrasena(contrasena)) {
            hasError = true
            val msg = "La contraseña debe tener 8+ caracteres, mayúscula, minúscula, dígito y símbolo."
            _form.update { it.copy(contrasenaError = msg) }
            firstError = firstError ?: msg
        }

        // Validación de confirmación de contraseña
        if (contrasenaConfirm.isBlank()) {
            hasError = true
            val msg = "Debes confirmar la contraseña"
            _form.update { it.copy(contrasenaConfirmError = msg) }
            firstError = firstError ?: msg
        } else if (contrasena != contrasenaConfirm) {
            hasError = true
            val msg = "Las contraseñas no coinciden"
            _form.update { it.copy(contrasenaConfirmError = msg) }
            firstError = firstError ?: msg
        }

        val dob = if (fechaNacimientoTexto.isBlank()) null else parseDate(fechaNacimientoTexto)
        if (fechaNacimientoTexto.isBlank()) {
            hasError = true
            val msg = "La fecha de nacimiento no puede estar vacía"
            _form.update { it.copy(fechaNacimientoError = msg) }
            firstError = firstError ?: msg
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

        if (region.isBlank()) {
            hasError = true
            val msg = "La región es obligatoria"
            _form.update { it.copy(regionError = msg) }
            if (firstError == null) firstError = msg
        }

        if (comuna.isBlank()) {
            hasError = true
            val msg = "La comuna es obligatoria"
            _form.update { it.copy(comunaError = msg) }
            if (firstError == null) firstError = msg
        }

        if (direccion.isBlank()) {
            hasError = true
            val msg = "La dirección es obligatoria"
            _form.update { it.copy(direccionError = msg) }
            if (firstError == null) firstError = msg
        }

        if (!aceptaTerminos) {
            hasError = true
            val msg = "Debes aceptar los términos y condiciones"
            _form.update { it.copy(aceptaTerminosError = msg) }
            if (firstError == null) firstError = msg
        }

        if (hasError) {
            _form.update { it.copy(error = firstError) }
            return@launch
        }

        val fechaNacimientoIso = dob!!.format(DateTimeFormatter.ISO_LOCAL_DATE)

        _form.update { it.copy(isLoading = true, error = null) }

        try {
            authRepository.registrar(
                run = run,
                nombre = nombre,
                apellidos = apellidos,
                correo = email,
                contrasena = contrasena,
                fechaNacimiento = fechaNacimientoIso,
                region = region,
                comuna = comuna,
                direccion = direccion,
                codigoReferido = codigoReferido
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

    private fun validarRun(run: String): Boolean {
        val normalized = run.replace(".", "").uppercase()
        val match = Regex("^[0-9]{7,8}-[0-9K]$")
        return match.matches(normalized) && validarDigitoVerificador(normalized)
    }

    private fun validarDigitoVerificador(runNormalizado: String): Boolean {
        val parts = runNormalizado.split("-")
        if (parts.size != 2) return false
        val numberPart = parts[0]
        val dv = parts[1]
        var sum = 0
        var multiplier = 2
        for (i in numberPart.length - 1 downTo 0) {
            sum += Character.getNumericValue(numberPart[i]) * multiplier
            multiplier = if (multiplier == 7) 2 else multiplier + 1
        }
        val remainder = 11 - (sum % 11)
        val expected = when (remainder) {
            11 -> "0"
            10 -> "K"
            else -> remainder.toString()
        }
        return expected.equals(dv, ignoreCase = true)
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