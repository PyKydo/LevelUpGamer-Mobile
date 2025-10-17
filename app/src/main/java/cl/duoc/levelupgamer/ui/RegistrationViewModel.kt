package cl.duoc.levelupgamer.ui

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class RegistrationViewModel : ViewModel() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRegistrationClick(
        name: String,
        email: String,
        birthDate: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank() || email.isBlank() || birthDate.isBlank()) {
            onError("Por favor, complete todos los campos.")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("El formato del correo electrónico no es válido.")
            return
        }

        val age = calculateAge(birthDate)
        if (age == -1) {
            onError("El formato de la fecha de nacimiento debe ser dd/mm/yyyy.")
            return
        }

        if (age < 18) {
            onError("Debe ser mayor de 18 años para registrarse.")
            return
        }

        val hasDiscount = email.endsWith("@duoc.cl", ignoreCase = true) || email.endsWith("@profesor.duoc.cl", ignoreCase = true)
        val successMessage = if (hasDiscount) {
            "¡Registro exitoso! Tienes un 20% de descuento de por vida."
        } else {
            "Registro exitoso"
        }

        // TODO: Aquí iría la lógica para guardar el usuario en la base de datos

        onSuccess(successMessage)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAge(birthDate: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val date = LocalDate.parse(birthDate, formatter)
            Period.between(date, LocalDate.now()).years
        } catch (e: DateTimeParseException) {
            -1 // Devuelve -1 si el formato de fecha es incorrecto
        }
    }
}