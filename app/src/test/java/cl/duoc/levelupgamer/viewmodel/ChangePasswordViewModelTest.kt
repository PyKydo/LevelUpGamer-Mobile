package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@ExperimentalCoroutinesApi
class ChangePasswordViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()
    lateinit var viewModel: ChangePasswordViewModel
    val authRepository: UsuarioRepository = mockk() // Se mockea la clase concreta

    beforeTest {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChangePasswordViewModel(authRepository)
    }

    afterTest {
        Dispatchers.resetMain()
    }

    "El cambio de contraseña debe ser exitoso con datos validos" {
        runTest(testDispatcher) {
            coEvery { authRepository.changePassword(any(), any()) } just runs

            viewModel.onCurrentPasswordChange("passActual123")
            viewModel.onNewPasswordChange("passNueva456!")
            viewModel.onConfirmPasswordChange("passNueva456!")
            viewModel.changePassword()
            advanceUntilIdle()

            val uiState = viewModel.uiState.value
            uiState.success shouldBe true
            uiState.error shouldBe null
            coVerify(exactly = 1) { authRepository.changePassword("passActual123", "passNueva456!") }
        }
    }

    "El cambio debe fallar si la contraseña actual es incorrecta" {
        runTest(testDispatcher) {
            val errorMessage = "La contraseña actual es incorrecta"
            coEvery { authRepository.changePassword(any(), any()) } throws IllegalArgumentException(errorMessage)

            viewModel.onCurrentPasswordChange("pass-incorrecta")
            viewModel.onNewPasswordChange("passNueva456!")
            viewModel.onConfirmPasswordChange("passNueva456!")
            viewModel.changePassword()
            advanceUntilIdle()

            val uiState = viewModel.uiState.value
            uiState.success shouldBe false
            uiState.error shouldBe errorMessage
        }
    }

    "El cambio debe fallar si las contraseñas nuevas no coinciden" {
        runTest(testDispatcher) {
            val repository: UsuarioRepository = mockk(relaxed = true) // Se mockea la clase concreta
            val vm = ChangePasswordViewModel(repository)

            vm.onCurrentPasswordChange("passActual123")
            vm.onNewPasswordChange("passNueva456!")
            vm.onConfirmPasswordChange("ESTA-NO-COINCIDE")
            vm.changePassword()
            advanceUntilIdle()

            val uiState = vm.uiState.value
            uiState.success shouldBe false
            uiState.error shouldBe "Las contraseñas nuevas no coinciden"
            coVerify(exactly = 0) { repository.changePassword(any(), any()) }
        }
    }
})