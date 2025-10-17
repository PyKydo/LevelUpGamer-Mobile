package cl.duoc.levelupgamer.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistrationViewModel : ViewModel() {

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
            "¡Registro exitoso!"
        }

        // TODO: Aquí iría la lógica para guardar el usuario en la base de datos

        onSuccess(successMessage)
    }

    private fun calculateAge(birthDate: String): Int {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dob = Calendar.getInstance()
            dob.time = sdf.parse(birthDate) ?: return -1

            val today = Calendar.getInstance()

            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--
            }
            age
        } catch (e: Exception) {
            -1 // Devuelve -1 si el formato de fecha es incorrecto
        }
    }
}