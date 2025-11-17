package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.repository.UsuarioRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "El cambio de contraseña debe ser exitoso con datos válidos" {
        runTest(testDispatcher) {
            val usuarioRepository: UsuarioRepository = mockk()
            coEvery { usuarioRepository.changePassword(any(), any()) } returns Unit
            val viewModel = ChangePasswordViewModel(usuarioRepository)


            viewModel.onCurrentPasswordChange("passActual123")
            viewModel.onNewPasswordChange("passNueva456!")
            viewModel.onConfirmPasswordChange("passNueva456!")
            viewModel.changePassword()
            advanceUntilIdle()


            val uiState = viewModel.uiState.value
            uiState.success shouldBe true
            uiState.error shouldBe null
        }
    }

    "El cambio debe fallar si la contraseña actual es incorrecta" {
        runTest(testDispatcher) {
            val usuarioRepository: UsuarioRepository = mockk()
            val errorMessage = "La contraseña actual es incorrecta"
            coEvery { usuarioRepository.changePassword(any(), any()) } throws IllegalArgumentException(errorMessage)
            val viewModel = ChangePasswordViewModel(usuarioRepository)

            viewModel.onCurrentPasswordChange("passIncorrecta")
            viewModel.onNewPasswordChange("passNueva456!")
            viewModel.onConfirmPasswordChange("passNueva456!")
            viewModel.changePassword()
            advanceUntilIdle()

            val uiState = viewModel.uiState.value
            uiState.success shouldBe false
            uiState.error shouldBe errorMessage
        }
    }

    "El cambio debe fallar si las nuevas contraseñas no coinciden" {
        runTest(testDispatcher) {
            val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
            val viewModel = ChangePasswordViewModel(usuarioRepository)

            viewModel.onCurrentPasswordChange("passActual123")
            viewModel.onNewPasswordChange("passNueva456!")
            viewModel.onConfirmPasswordChange("ESTA-NO-COINCIDE") // Contraseñas no coinciden
            viewModel.changePassword()
            advanceUntilIdle()

            val uiState = viewModel.uiState.value
            uiState.success shouldBe false
            uiState.error shouldBe "Las contraseñas nuevas no coinciden"
            coVerify(exactly = 0) { usuarioRepository.changePassword(any(), any()) }
        }
    }
})