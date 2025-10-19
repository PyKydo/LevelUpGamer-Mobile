package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.repository.InAuthRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginViewModelFactory(
	private val authRepository: InAuthRepository
) : ViewModelProvider.Factory {
	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
			return LoginViewModel(authRepository) as T
	}
}