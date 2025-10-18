package cl.duoc.levelupgamer.viewmodel.state

sealed class RegistrationState {
    object Initial: RegistrationState()
    object Loading: RegistrationState()
    data class Success(val message: String): RegistrationState()
    data class Error(val error: String): RegistrationState()
}