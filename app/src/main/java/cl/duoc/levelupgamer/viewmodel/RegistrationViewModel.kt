package cl.duoc.levelupgamer.viewmodel

import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.ViewModel
import cl.duoc.levelupgamer.viewmodel.state.RegistrationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.regex.Pattern

class RegistrationViewModel: ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Initial)
    val registrationState: StateFlow<RegistrationState> = _registrationState

    private fun validateName(name: String): Boolean {
        return name.isNotBlank()
    }

    private fun validateEmail(email: String): Boolean {
        val pattern = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$")
        return pattern.matcher(email).matches()
    }

    private fun validateAge(birthDate: String): Boolean {
        val sdf = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = LocalDate.now()
        val dateOfBirth = LocalDate.parse(birthDate, sdf)

        val age = Period.between(dateOfBirth, currentDate)

        return (age >= 18)
    }

    fun onRegistrationClick(name: String, email: String, birthDate: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        _registrationState.value = RegistrationState.Loading

    }
}