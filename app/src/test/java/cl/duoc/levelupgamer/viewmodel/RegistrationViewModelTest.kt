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

            val usuarioRepository: UsuarioRepository = mockk()
            val dummyUser = Usuario(1, "Nuevo Usuario", "nuevo@test.com", "ValidPass123!", "01/01/2000")
            coEvery { usuarioRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns dummyUser
            val viewModel = RegistrationViewModel(usuarioRepository)

            viewModel.onChangeNombre("Nuevo Usuario")
            viewModel.onChangeRun("11.111.111-1")
            viewModel.onChangeApellido("Test")
            viewModel.onChangeEmail("nuevo@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe true
            uiState.error shouldBe null
        }
    }

    "El registro debe fallar si el email ya existe" {
        runTest(testDispatcher) {
            val usuarioRepository: UsuarioRepository = mockk()
            val errorMessage = "El email ya está registrado"
            coEvery { usuarioRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws IllegalArgumentException(errorMessage)
            val viewModel = RegistrationViewModel(usuarioRepository)

            viewModel.onChangeNombre("Otro Usuario")
            viewModel.onChangeRun("22.222.222-2")
            viewModel.onChangeApellido("Prueba")
            viewModel.onChangeEmail("existente@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.error shouldBe errorMessage
        }
    }

    "El registro debe fallar si la contraseña es inválida" {
        runTest(testDispatcher) {
            val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(usuarioRepository)

            viewModel.onChangeNombre("Test")
            viewModel.onChangeRun("11.111.111-1")
            viewModel.onChangeApellido("Apellido")
            viewModel.onChangeEmail("test@test.com")
            viewModel.onChangeContrasena("corta") // Contraseña inválida
            viewModel.onChangeContrasenaConfirm("corta")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.contrasenaError shouldNotBe null
            coVerify(exactly = 0) { usuarioRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }
    }

    "El registro debe fallar si el usuario es menor de edad" {
        runTest(testDispatcher) {
            val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(usuarioRepository)
            val fechaMenorDeEdad = java.time.LocalDate.now().minusYears(17).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))

            viewModel.onChangeNombre("Joven Usuario")
            viewModel.onChangeRun("33.333.333-3")
            viewModel.onChangeApellido("Menor")
            viewModel.onChangeEmail("joven@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento(fechaMenorDeEdad)
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.fechaNacimientoError shouldBe "Debes tener al menos 18 años."
            coVerify(exactly = 0) { usuarioRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }
    }

    "El registro debe fallar si el RUN es inválido" {
        runTest(testDispatcher) {
            val usuarioRepository: UsuarioRepository = mockk(relaxed = true)
            val viewModel = RegistrationViewModel(usuarioRepository)

            viewModel.onChangeNombre("Nombre Valido")
            viewModel.onChangeApellido("Apellido Valido")
            viewModel.onChangeEmail("email.valido@test.com")
            viewModel.onChangeContrasena("ValidPass123!")
            viewModel.onChangeContrasenaConfirm("ValidPass123!")
            viewModel.onChangeFechaNacimiento("01/01/2000")
            viewModel.onChangeRegion("Metropolitana")
            viewModel.onChangeComuna("Santiago")
            viewModel.onChangeDireccion("Av. Siempre Viva 123")
            viewModel.onChangeAceptaTerminos(true)
            viewModel.onChangeRun("11.111.111-2") 
            viewModel.registrar()
            advanceUntilIdle()

            val uiState = viewModel.form.value
            uiState.isSuccess shouldBe false
            uiState.runError shouldBe "El RUN no es válido"
            coVerify(exactly = 0) { usuarioRepository.registrar(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
        }
    }
})