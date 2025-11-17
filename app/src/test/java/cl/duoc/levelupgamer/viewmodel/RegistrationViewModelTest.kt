package cl.duoc.levelupgamer.viewmodel

import cl.duoc.levelupgamer.model.Usuario
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
class RegistrationViewModelTest : StringSpec({

    val testDispatcher = StandardTestDispatcher()

    beforeSpec {
        Dispatchers.setMain(testDispatcher)
    }

    afterSpec {
        Dispatchers.resetMain()
    }

    "El registro debe ser exitoso con datos válidos" {
        runTest(testDispatcher) {
            // 1. Preparación
            val usuarioRepository: UsuarioRepository = mockk()
            val dummyUser = Usuario(1, "Nuevo Usuario", "nuevo@test.com", "ValidPass123!", "01/01/2000")
            coEvery { usuarioRepository.registrar(any(), any(), any(), any()) } returns dummyUser
            val viewModel = RegistrationViewModel(usuarioRepository)

            // 2. Acción
            viewModel.onChangeNombre("Nuevo Usuario")
            viewModel.onChangeEmail("nuevo@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!") // Se añade la confirmación
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.registrar()
            advanceUntilIdle()

            // 3. Verificación
            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe true
            uiState.error shouldBe null
        }
    }

    "El registro debe fallar si el email ya existe" {
        runTest(testDispatcher) {
            // 1. Preparación
            val usuarioRepository: UsuarioRepository = mockk()
            val errorMessage = "El email ya está registrado"
            coEvery { usuarioRepository.registrar(any(), any(), any(), any()) } throws IllegalArgumentException(errorMessage)
            val viewModel = RegistrationViewModel(usuarioRepository)

            // 2. Acción
            viewModel.onChangeNombre("Otro Usuario")
            viewModel.onChangeEmail("existente@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!") // Se añade la confirmación
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.registrar()
            advanceUntilIdle()

            // 3. Verificación
            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.error shouldBe errorMessage
        }
    }

    "El registro debe fallar si la contraseña es inválida" {
        runTest(testDispatcher) {
            // 1. Preparación
            val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(usuarioRepository)

            // 2. Acción
            viewModel.onChangeNombre("Test")
            viewModel.onChangeEmail("test@test.com")
            viewModel.onChangeContrasena("corta") // Contraseña inválida
            viewModel.onChangeContrasenaConfirm("corta") // Se añade la confirmación para aislar el error
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.registrar()
            advanceUntilIdle()

            // 3. Verificación
            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.contrasenaError shouldNotBe null
            coVerify(exactly = 0) { usuarioRepository.registrar(any(), any(), any(), any()) }
        }
    }

    "El registro debe fallar si el usuario es menor de edad" {
        runTest(testDispatcher) {
            // 1. Preparación
            val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(usuarioRepository)
            val fechaMenorDeEdad = java.time.LocalDate.now().minusYears(17).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            // 2. Acción
            viewModel.onChangeNombre("Joven Usuario")
            viewModel.onChangeEmail("joven@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!") // Se añade la confirmación
            viewModel.onChangeFechaNacimiento(fechaMenorDeEdad)
            viewModel.registrar()
            advanceUntilIdle()

            // 3. Verificación
            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.fechaNacimientoError shouldBe "Debes tener al menos 18 años."
            coVerify(exactly = 0) { usuarioRepository.registrar(any(), any(), any(), any()) }
        }
    }
})